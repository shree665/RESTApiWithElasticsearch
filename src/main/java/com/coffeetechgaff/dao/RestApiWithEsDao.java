package com.coffeetechgaff.dao;

import com.coffeetechgaff.model.EsMetadata;
import com.coffeetechgaff.utils.RestApiWithEsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VivekSubedi on 8/18/17.
 */
public class RestApiWithEsDao implements ElasticsearchOperation<EsMetadata>{

    private static final Logger logger = LoggerFactory.getLogger(RestApiWithEsDao.class);

    @SuppressWarnings("deprecation")
	@Override
    public String indexMetadata(EsMetadata metadata) throws IOException {
        // getting the client
        Client client = getClient();

        // instance a json mapper
        ObjectMapper mapper = new ObjectMapper();

        // generate json
        byte[] json = mapper.writeValueAsBytes(metadata);
        logger.info("Wrote Data EsMetadata POJO to byte array.");

        logger.info("Indexing data source record into elasticsearch; ");

        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(RestApiWithEsUtils.ES_INDEX,
                RestApiWithEsUtils.DOCUMENT_TYPE, metadata.getId()).setSource(json);
        IndexResponse indexResponse = indexRequestBuilder.get();

        logger.info("Data source record has been created with id: [{}] ", indexResponse.getId());

        return indexResponse.getId();
    }

    @Override
    public List<EsMetadata> executeQuery(QueryBuilder searchQuery) throws IOException{
        Client client = getClient();

        int pageSize = Integer.parseInt(RestApiWithEsUtils.ESPAGESIZE);
        SearchResponse response = client.prepareSearch(RestApiWithEsUtils.ES_INDEX)
                .setScroll(new TimeValue(600000)).setQuery(searchQuery).setSize(pageSize).get();
        logger.info("Successfully retrieved data from es");
        int totalHits = (int) response.getHits().getTotalHits();
        List<EsMetadata> list = new ArrayList<>(totalHits);
        logger.info("There are [{}] number of total records in this search", totalHits);
        do{
            List<EsMetadata> tempList = RestApiWithEsUtils.parseDataSourceJson(response);
            if(!tempList.isEmpty()){
                list.addAll(tempList);
                response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(600000)).get();
            }
        }while(response.getHits().getHits().length != 0);
        return list;
    }

    @Override
    public String updateData(UpdateRequest updateRequest) throws IOException{
        // getting the client
        Client client = getClient();
        UpdateResponse response = client.update(updateRequest).actionGet();
        return response.getId();
    }

    @Override
    public boolean deleteData(DeleteRequest deleteRequest) throws IOException{
        // We don't have to implement it since we are doing soft delete
        return false;
    }

    private Client getClient() throws IOException{
        return ElasticsearchClient.getElasticsearchClient(RestApiWithEsUtils.ES_CLUSTER_NAME,
                RestApiWithEsUtils.ES_IP, Integer.parseInt(RestApiWithEsUtils.TRANSPORT_PORT));
    }
}
