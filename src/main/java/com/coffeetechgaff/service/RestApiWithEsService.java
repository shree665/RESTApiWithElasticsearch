package com.coffeetechgaff.service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.coffeetechgaff.dao.ElasticsearchOperation;
import com.coffeetechgaff.enums.Operation;
import com.coffeetechgaff.kafka.KafkaMessageProducer;
import com.coffeetechgaff.model.EsMetadata;
import com.coffeetechgaff.utils.RestApiWithEsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by VivekSubedi on 8/25/17.
 */
@Resource
@ManagedBean
public class RestApiWithEsService implements IRestApiWithEsService {

    private Logger logger = LoggerFactory.getLogger(RestApiWithEsService.class);

    private static final String CANNOT_CREATE = "The datasource can not be created in elasticsearch.";
    private static final String CANNOT_WRITE = "Cannot write a data source record in Geowave";
    private static final String CANNOT_CREATE_INDEX_WITH_MAPPING = "Can not create an index with a mapping";
    private static final String ES_QUERY_ERROR = "Something went wrong querying data source from Elasticsearch.";
    private static final String MULTIPLE_RECORDS = "More than one record was found";
    private static final String DATASOURCE_DELETED = "The data source you are trying to update has been deleted already";
    private static final String UPDATE_ERROR = "Something went wrong while updating for data source.";

    private ObjectMapper objectMapper = new ObjectMapper();
    private KafkaMessageProducer producer = new KafkaMessageProducer(RestApiWithEsUtils.BROKERLIST, RestApiWithEsUtils.TOPIC);
    public static final String ID = RestApiWithEsUtils.ID;


    @Inject
    private ElasticsearchOperation<EsMetadata> esDao;

    @Override
    public Response indexMetadata(EsMetadata metadata) {
        // create and add the uid
        String uid = UUID.randomUUID().toString();
        logger.info("Successfully generated uid [{}]", uid);
        metadata.setId(uid);

        // Set status as active
        metadata.setStatus("active");
        logger.info("Putting new data source into Elasticsearch ");

        Response response;
        try{
            response = writeDataToEs(metadata);
        } catch(IOException e) {
            return Response.serverError().entity(CANNOT_CREATE).build();
        }
        return response;
    }

    private Response writeDataToEs(EsMetadata metadata) throws IOException {
        // create a meta data object from the payload
        String dataSourceId = esDao.indexMetadata(metadata);
        logger.debug(dataSourceId);
        logger.info("Data Source Record has been created in elasticsearch");
        String jsonResponse = objectMapper.writeValueAsString(metadata);
        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
    }

    @Override
    public Response sendMessageToKafka(EsMetadata metadata, Operation operation) {
        try{
        		producer.sendMessage(metadata, operation);
            logger.info("Successfully sent Data Source [{}] message to Kafka", operation);
            return Response.status(Status.CREATED)
                    .entity(objectMapper.writeValueAsString(metadata)).build();
        } catch(IOException e) {
            return Response.serverError().entity("Couldn't send Data Source " + operation + " message to Kafka").build();
        }
    }

    @Override
    public Response deleteMetadata(String id) {
    		EsMetadata dataToDelete;
    		List<EsMetadata> listOfMetadata;
    		try {
    			listOfMetadata = getDataFromEs(id);
    		} catch(IOException e) {
    			logger.error("Couldn't query data source from Elasticsearch ", e);
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ES_QUERY_ERROR).build();
    		}
    		
    		if (listOfMetadata.size() != 1) {
    			logger.error("More than one data source record was found in Elasticsearch ");
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(MULTIPLE_RECORDS).build();
		}
    		
    		dataToDelete = listOfMetadata.get(0);
    		if ("deleted".equalsIgnoreCase(dataToDelete.getStatus())) {
    			logger.error(DATASOURCE_DELETED);
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(DATASOURCE_DELETED).build();
		}
    		UpdateRequest deleteRequest;
    		
    		try {
    			deleteRequest = createUpdateRequest(id);
    			String dataId = esDao.updateData(deleteRequest);
    			logger.debug(dataId);
    			logger.info("Data source record has been deleted from Elasticsearch of id [{}]", id);
    		} catch(IOException e){
    			logger.error("Something went wrong while updating Elasticsearch", e);
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(UPDATE_ERROR).build();
    		}
    		
        return sendMessageToKafka(dataToDelete, Operation.DELETE);
    }

	private UpdateRequest createUpdateRequest(String id) throws IOException{
        UpdateRequest deleteRequest = new UpdateRequest();
        deleteRequest.index(RestApiWithEsUtils.ES_METADATA_INDEX_NAME);
        deleteRequest.type(RestApiWithEsUtils.ES_METADATA_DOCUMENT_TYPE);
        deleteRequest.id(id);
        deleteRequest.doc(jsonBuilder().startObject().field(RestApiWithEsUtils.STATUS, RestApiWithEsUtils.DELETED)
                .endObject());
        return deleteRequest;
    }

    private List<EsMetadata> getDataFromEs(String id) throws IOException {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery(ID, id).minimumShouldMatch("1");
        logger.info("retrieve data source from elasticsearch: {}", queryBuilder);
        return esDao.executeQuery(queryBuilder);
    }

    @Override
    public Response retrieveMetadata(String payload) {

        return null;
    }

    @Override
    public Response createEsRangeQuery(String startDateTime, String endDateTime, String fieldName) {
        return null;
    }
}
