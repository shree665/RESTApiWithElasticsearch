package com.coffeetechgaff.dao;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Created by VivekSubedi on 8/18/17.
 */
public interface ElasticsearchOperation<T> {
    /**
     * Writes user provided into the Elasticsearch
     *
     * @param userObject
     *            -user provided data object
     * @param client
     *            -Elasticsearch client
     * @return UID of a record in elasticsearch
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     */
    String indexMetadata(T userObject) throws IOException;

    /**
     * Query the data in elasticsearch and returns the user provided object
     *
     * @param searchQuery
     *            -@QeuryBuilder elasticsearch query to query elasticsearch
     * @param client-Elasticsearch client
     * @return @List of Userdefined object
     * @throws com.fasterxml.jackson.core.JsonParseException
     * @throws com.fasterxml.jackson.databind.JsonMappingException
     * @throws IOException
     */
    List<T> executeQuery(QueryBuilder searchQuery) throws IOException;

    /**
     * Updates the existing data in elasticsearch
     *
     * @param updateRequest
     *            -Elasticsearch defined UpdateRequest
     * @param client
     *            -Elasticsearch Client
     * @return UID of a record in elasticsearch
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws IOException
     */
    String updateData(UpdateRequest updateRequest) throws IOException;

    /**
     * Deletes the data from elasticsearch
     *
     * @param deleteRequest
     *            -Elasticsearch delete request
     * @param client
     *            -Elasticsearch connection
     * @return true if deleted successfully
     */
    boolean deleteData(DeleteRequest deleteRequest) throws IOException;
}
