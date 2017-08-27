package com.coffeetechgaff.model;

import com.coffeetechgaff.utils.RestApiWithEsUtils;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by VivekSubedi on 8/25/17.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = RestApiWithEsUtils.SOURCE_TYPE)
@JsonSubTypes({@Type(value = KafkaSource.class, name = RestApiWithEsUtils.SOURCE_TYPE_KAFKA),
        @Type(value = OGCSource.class, name = RestApiWithEsUtils.SOURCE_TYPE_OGC),
        @Type(value = S3Source.class, name = RestApiWithEsUtils.SOURCE_TYPE_S3),
        @Type(value = GeowaveSource.class, name = RestApiWithEsUtils.SOURCE_TYPE_GEOWAVE),
        @Type(value = ElasticsearchSource.class, name = RestApiWithEsUtils.SOURCE_TYPE_ES)})
@ApiModel(value = RestApiWithEsUtils.SOURCE, description = "Data source location information, with subtypes KafkaSource.class, WFSSource.class, OGCSource.class, S3Source.class, GeowaveSource.class, ESSource.class based on discriminator 'type' with allowable values: kafka, ogc, s3, geowave, elasticsearch", subTypes = {
        KafkaSource.class, OGCSource.class, S3Source.class, GeowaveSource.class, ElasticsearchSource.class}, discriminator = RestApiWithEsUtils.TYPE)
public abstract class Source implements Serializable {
    private static final long serialVersionUID = -4140972150108306390L;

    public Map<String, String> toMap(){
        return null;
    }
}
