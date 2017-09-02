package com.coffeetechgaff.service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.coffeetechgaff.dao.ElasticsearchOperation;
import com.coffeetechgaff.enums.Attributes;
import com.coffeetechgaff.enums.Operation;
import com.coffeetechgaff.kafka.KafkaMessageProducer;
import com.coffeetechgaff.model.EsMetadata;
import com.coffeetechgaff.utils.RestApiWithEsUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * Created by VivekSubedi on 8/25/17.
 */
@Resource
@ManagedBean
public class RestApiWithEsService implements IRestApiWithEsService {

    private Logger logger = LoggerFactory.getLogger(RestApiWithEsService.class);

    private static final String CANNOT_CREATE = "The datasource can not be created in elasticsearch.";
    private static final String ES_QUERY_ERROR = "Something went wrong querying data source from Elasticsearch.";
    private static final String MULTIPLE_RECORDS = "More than one record was found";
    private static final String DATASOURCE_DELETED = "The data source you are trying to update has been deleted already";
    private static final String UPDATE_ERROR = "Something went wrong while updating for data source.";
    private static final String PAYLOAD_PARSE_ERROR = "Something went wrong while parsing payload to our data source pojo";
    private static final String QUERY_ERROR = "Something went wrong while querying the data source. Please check the server log for complete message";
    private static final String PARSE_ERROR = "Couldn't parse the returned data source data";
    private static final String NO_RECORDS = "There are no records";

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
    		EsMetadata metadata;
    		try {
    			metadata = objectMapper.readValue(payload, EsMetadata.class);
    		} catch(IOException e1){
    			logger.error("Something went wrong while parsing payload to data source pojo", e1);
    			return Response.serverError().entity(PAYLOAD_PARSE_ERROR).build();
    		}
    		
    		Map<String, Object> attributeMap = RestApiWithEsUtils.getCleanedAttributes(metadata);
    		JSONObject payLoadJsonObject = new JSONObject(payload);
    		Map<String, Object> jsonMap = payLoadJsonObject.toMap();
    		
    		// by default, we auto populated CeeateDate and Pool if the value is
    		// null in payload we need to remove it from our search
    		if(jsonMap != null){

    			if(!jsonMap.containsKey(Attributes.CREATIONDATE.getAttributeName())){
    				attributeMap.remove(Attributes.CREATIONDATE.getAttributeName());
    			}

    			if(!jsonMap.containsKey(Attributes.LASTUPDATED.getAttributeName())){
    				attributeMap.remove(Attributes.LASTUPDATED.getAttributeName());
    			}
    		}
    		
    		List<EsMetadata> returnedMetadataList;
    		try{
    			returnedMetadataList = getFilteredDataSources(attributeMap);
    		}catch(IOException e){
    			logger.error("Something went wrong while querying the data source", e);
    			return Response.serverError().entity(QUERY_ERROR).build();
    		}

    		return getDataResponse(returnedMetadataList);
    }
    
    @SuppressWarnings("unchecked")
	private List<EsMetadata> getFilteredDataSources(Map<String, Object> attributeMap) throws IOException{

		BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
		boolQuery1.mustNot(QueryBuilders.matchQuery(RestApiWithEsUtils.STATUS, RestApiWithEsUtils.DELETED));

		BoolQueryBuilder qb = QueryBuilders.boolQuery().must(boolQuery1);
		//for list attributes, need to use termQuery method to match the list values of a object
		for(Map.Entry<String, Object> entry : attributeMap.entrySet()){
			if(Attributes.SOURCE.getAttributeName().equalsIgnoreCase(entry.getKey())){
				for(Map.Entry<String, Object> localEntry : ((Map<String, Object>) entry.getValue()).entrySet()){
					if(localEntry.getValue() != null){
						String nestedQueryString = new StringBuilder().append(entry.getKey()).append(".")
								.append(localEntry.getKey()).toString();
						if((localEntry.getValue() instanceof String) || (localEntry.getValue() instanceof Integer)){
							qb.must(QueryBuilders.matchQuery(nestedQueryString, localEntry.getValue()));
						}else{
							logger.warn(
									"Unrecognized object in Query at [{}], sub object of [{}] must either be Strings or Integers",
									nestedQueryString, entry.getKey());
						}
					}
				}
			}else{
				qb.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
			}
		}

		String logMessage = "Run the query to find data sources from Elasticsearch " + qb.toString();
		logger.info(logMessage);

		List<EsMetadata> returnedDataSourceList = esDao.executeQuery(qb);
		logMessage = "Found " + returnedDataSourceList.size() + " number of data sources from Elasticsearch; ";
		logger.info(logMessage);

		return returnedDataSourceList;
	}
    
    private Response getDataResponse(List<EsMetadata> returnedDataSourceList){
		if(!returnedDataSourceList.isEmpty()){
			try{
				return Response.ok(
						objectMapper.writerWithDefaultPrettyPrinter()
								.writeValueAsString(returnedDataSourceList), MediaType.APPLICATION_JSON).build();
			}catch(JsonProcessingException e){
				logger.error(PARSE_ERROR, e);
				return Response.serverError().entity(PARSE_ERROR).build();
			}
		}else{
			return Response.status(Status.NO_CONTENT).entity("no content").build();
		}
	}

    @Override
    public Response createEsRangeQuery(String startDateTime, String endDateTime, String fieldName) throws IOException {
    		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(fieldName).gte(startDateTime).lte(endDateTime);
		String queryString = rangeQueryBuilder.toString();
		logger.info(queryString);
		List<EsMetadata> returnedDataSourceList = esDao.executeQuery(rangeQueryBuilder);
		logger.info("Found [{}] number of data sources from Elasticsearch", returnedDataSourceList.size());
		return getDataResponse(returnedDataSourceList);
    }

	@Override
	public Response updateMetdata(EsMetadata metadata) throws IOException {
		// building querybuilding using id
		QueryBuilder idQueryBuilder = getIdQueryBuilder(metadata.getId());

		List<EsMetadata> latestMetadataList;

		try{
			// quering for the id we are trying to update
			latestMetadataList = esDao.executeQuery(idQueryBuilder);
		}catch(IOException e){
			logger.error("Couldn't query data source in Elasticsearch", e);
			return Response.serverError().entity(QUERY_ERROR).build();
		}

		// checking that the record exists with that id
		if(latestMetadataList.isEmpty()){
			logger.error("There are no data source record in Elasticsearch");
			return Response.serverError().entity(NO_RECORDS).build();
		}
		// we are looking for only one record since all ids are unique
		if(latestMetadataList.size() > 1){
			logger.error("There shoule be only one data source record in Elasticsearch with this id ");
			return Response.serverError().entity("Multiple records").build();
		}

		EsMetadata esMetadata = latestMetadataList.get(0);


		// re-adding creating date because we don't want to change it
		metadata.setCreationDate(esMetadata.getCreationDate());
		
		// cleaning up user provided data
		Map<String, Object> attributeMap = RestApiWithEsUtils.getCleanedAttributes(metadata);
		try{
			if(attributeMap.get(RestApiWithEsUtils.SOURCE) != null){
				@SuppressWarnings("unchecked")
				Map<String, String> source = (Map<String, String>) attributeMap.get(RestApiWithEsUtils.SOURCE);
				if(source.get(RestApiWithEsUtils.TYPE) != null
						&& !StringUtils.equals(source.get(RestApiWithEsUtils.TYPE),
								esMetadata.getSource().toMap().get(RestApiWithEsUtils.TYPE))){
					String message = "Source Type cannot be changed";
					logger.error(message);
					return Response.serverError().entity("source type can not changed").build();
				}
			}
		}catch(Exception e){
			logger.error(
					"There is an error when checking that new source type is equal to stored source type, source type will not be changed",
					e);
		}
		
		// updating the data source in es
		String jsonStringToReturn;
		JSONObject newJSONObject;
		try{
			logger.debug("Attributes to be updated: {}", attributeMap);
			UpdateRequest updateRequest = getUpdateRequest(metadata.getId(), attributeMap);
			esDao.updateData(updateRequest);
			jsonStringToReturn = objectMapper.writeValueAsString(esMetadata);
			newJSONObject = new JSONObject(jsonStringToReturn);
			logger.info("Metadata has been updated successfully in Elasticsearch");
		}catch(IOException e){
			logger.error("Couldn't update data source in Elasticsearch", e);
			return Response.serverError().entity(UPDATE_ERROR).build();
		}
		
		// Print updated json
		for(String key : newJSONObject.keySet()){
			if(attributeMap.get(key) != null){
				newJSONObject.put(key, attributeMap.get(key));
			}
		}
		
		EsMetadata updatedMetadata = objectMapper.readValue(newJSONObject.toString(), EsMetadata.class);
		logger.info("Metadata is updated successfully");
		
		Response kafkaResponse = sendMessageToKafka(updatedMetadata, Operation.UPDATE);
		
		if(kafkaResponse.getStatus() != 200){
			return Response.serverError().entity("Cannot send UPDATE data source message to Kafka").build();
		}

		logger.info("Successfully send UPDATE data source message to Kafka; ");

		return Response.ok().entity(newJSONObject.toString()).build();
	}
	
	/*
	 * Creates UpdateRequest to elasticsearch and loads all the attributes that
	 * is needed
	 * 
	 * @param uid - Data Source Id in elastic search
	 * 
	 * @param attributeMap - Map of all the meta data for data source
	 * 
	 * @return UpdateRequest object
	 * 
	 * @throws IOException
	 */
	private UpdateRequest getUpdateRequest(String uid, Map<String, Object> attributeMap) throws IOException{
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(RestApiWithEsUtils.ES_METADATA_INDEX_NAME);
		updateRequest.type(RestApiWithEsUtils.ES_METADATA_DOCUMENT_TYPE);
		updateRequest.id(uid);
		XContentBuilder contentBuilder = buildAttributeJson(attributeMap);
		updateRequest.doc(contentBuilder);
		return updateRequest;
	}
	
	private XContentBuilder buildAttributeJson(Map<String, Object> attributeMap) throws IOException{

		// starting a JSON objet since we need to send data as JSON object to
		// elastic search
		XContentBuilder contentBuilder = jsonBuilder().startObject();

		// looping through attributes to create a JSON to update
		for(Map.Entry<String, Object> entry : attributeMap.entrySet()){

			if(entry.getKey().equalsIgnoreCase(RestApiWithEsUtils.SOURCE)){
				@SuppressWarnings("unchecked")
				Map<String, String> sourceMap = (Map<String, String>) entry.getValue();
				contentBuilder.startObject(entry.getKey());
				for(Entry<String, String> field : sourceMap.entrySet()){
					contentBuilder.field(field.getKey(), field.getValue());
				}
				contentBuilder.endObject();
			}else{
				contentBuilder.field(entry.getKey(), entry.getValue().toString());
			}
		}
		contentBuilder.endObject();
		String query = contentBuilder.string();
		logger.debug("looping through attributes to create a JSON to update:\n {}", query);
		return contentBuilder;
	}
	
	private QueryBuilder getIdQueryBuilder(String id){
		return QueryBuilders.matchQuery("id", id).minimumShouldMatch("1");
	}

}
