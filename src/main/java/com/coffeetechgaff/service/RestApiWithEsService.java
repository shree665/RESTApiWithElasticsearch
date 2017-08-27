package com.coffeetechgaff.service;

import com.coffeetechgaff.dao.ElasticsearchOperation;
import com.coffeetechgaff.enums.Operation;
import com.coffeetechgaff.model.EsMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by VivekSubedi on 8/25/17.
 */
@Resource
@ManagedBean
public class RestApiWithEsService implements IRestApiWithEsService {

    private Logger logger = LoggerFactory.getLogger(RestApiWithEsService.class);

    public static final String CANNOT_CREATE = "The datasource can not be created in elasticsearch.";
    public static final String CANNOT_WRITE = "Cannot write a data source record in Geowave";
    public static final String CANNOT_CREATE_INDEX_WITH_MAPPING = "Can not create an index with a mapping";
    public static final String SCHEMA_AND_SCHEMA_ID_PRESENT = "Schema and SchemaId are both present, they cannot be registered at the same time";
    public static final String DATATYPES_AUTO_REGISTERED = "DataTypes are auto-generated from the schema, and cannot be registered manually";
    public static final String SCHEMA_INVALID = "The schema is invalid.";

    private ObjectMapper objectMapper = new ObjectMapper();


    @Inject
    private ElasticsearchOperation esDao;

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
        return null;
    }

    @Override
    public Response deleteMetadata(String id) {
        return null;
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
