package com.coffeetechgaff.service;

import com.coffeetechgaff.enums.Operation;
import com.coffeetechgaff.model.EsMetadata;

import java.io.IOException;

import javax.ws.rs.core.Response;

/**
 * Created by VivekSubedi on 8/25/17.
 */
public interface IRestApiWithEsService {
    Response indexMetadata(EsMetadata metadata);
    Response sendMessageToKafka(EsMetadata metadata, Operation operation);
    Response deleteMetadata(String id);
    Response retrieveMetadata(String payload);
    Response createEsRangeQuery(String startDateTime, String endDateTime, String fieldName) throws IOException;
    Response updateMetdata(EsMetadata metadata) throws IOException;
}
