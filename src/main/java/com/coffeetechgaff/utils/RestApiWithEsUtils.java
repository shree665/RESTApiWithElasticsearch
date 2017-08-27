package com.coffeetechgaff.utils;

import com.coffeetechgaff.model.EsMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VivekSubedi on 8/23/17.
 */
public class RestApiWithEsUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestApiWithEsUtils.class);


    public static final String ES_INDEX = "testdata";
    public static final String DOCUMENT_TYPE = "testdoc";
    public static final String ES_CLUSTER_NAME= "elasticsearch";
    public static final String ES_IP = "localhost";
    public static final String TRANSPORT_PORT = "9300";
    public static final String ESPAGESIZE = "1000";
    public static final String HITS = "hits";
    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String GOLD = "gold";
    public static final String SOURCE_TYPE_KAFKA = "kafka";
    public static final String SOURCE_TYPE_OGC = "ogc";
    public static final String SOURCE_TYPE_S3 = "s3";
    public static final String SOURCE_TYPE_ES = "elasticsearch";
    public static final String SOURCE_TYPE_GEOWAVE = "geowave";
    public static final String SOURCE_TYPE_GEOSERVERLAYER = "geoserverlayer";
    public static final String SOURCE_TYPE = TYPE;
    public static final String SOURCE_FIELD_TOPICNAME = "topicName";
    public static final String SOURCE_FIELD_ZOOKEEPERS = "zookeepers";
    public static final String SOURCE_FIELD_URL = "url";
    public static final String SOURCE_FIELD_ESINDEX = "esIndex";
    public static final String SOURCE_FIELD_REQUEST = "request";
    public static final String SOURCE_FIELD_TYPENAMES = "typeNames";
    public static final String SOURCE_FIELD_OUTPUTFORMAT = "outputFormat";
    public static final String SOURCE_FIELD_HOSTNAME = "hostname";
    public static final String SOURCE_FIELD_TRANSPORTPORT = "transportPort";
    public static final String SOURCE_FIELD_RESTPORT = "restPort";
    public static final String SOURCE_FIELD_CLUSTER = "cluster";
    public static final String SOURCE_FIELD_INDEX = "index";
    public static final String SOURCE_FIELD_ACCUMULOINSTANCENAME = "accumuloInstanceName";
    public static final String SOURCE_FIELD_ACCUMULOTABLENAMESPACE = "accumuloTableNamespace";
    public static final String SOURCE_FIELD_ACCUMULOTABLENAME = "accumuloTableName";
    public static final String SOURCE_FIELD_NAME = "name";
    public static final String SOURCE_FIELD_DESCRIPTION = "description";
    public static final String SOURCE_FIELD_CLASSIFICATION = "classification";
    public static final String SOURCE_FIELD_MATURITY = "maturity";
    public static final String SOURCE_FIELD_EXPIRATION_DATE = "expirationDate";
    public static final String SOURCE_FIELD_CREATION_DATE = "creationDate";
    public static final String SOURCE_FIELD_LAST_UPDATED = "lastUpdated";

    public static final String SOURCE_OGC_BASEURI = "baseUri";
    public static final String SOURCE_OGC_WMSURI = "wmsUri";
    public static final String SOURCE_OGC_WFSURI = "wfsUri";
    public static final String SOURCE_OGC_GEOJSONURI = "geojsonUri";
    public static final String SOURCE_OGC_STORE_NAME = "storeName";
    public static final String SOURCE_OGC_STORE_TYPE = "storeType";
    public static final String SOURCE_OGC_STORE_DATASOURCE_ID = "storeDataSourceId";
    public static final String SOURCE_OGC_STORE_WORKSPACE = "storeWorkspace";
    public static final String SOURCE_OGC_STORE_LAYERNAME = "storeLayerName";

    private static ObjectMapper mapper = new ObjectMapper();


    public static List<EsMetadata> parseDataSourceJson(SearchResponse response) throws IOException{
        // parsing JSON object
        JSONObject json = new JSONObject(response);

        JSONObject hitsObj = json.getJSONObject(HITS);
        JSONArray hitsArray = hitsObj.getJSONArray(HITS);
        // list to store the elastic search meta data object
        List<EsMetadata> metaDataList = new ArrayList<>(hitsArray.length());

        // we always loop through this array since there might more than one
        // element in the array
        for(int i = 0; i < hitsArray.length(); i++){
            JSONObject innerJsonObject = hitsArray.getJSONObject(i);
            JSONObject source = innerJsonObject.getJSONObject(SOURCE);

            try{
                EsMetadata metaData = mapper.readValue(source.toString(), EsMetadata.class);
                metaDataList.add(metaData);
            }catch(Exception e){
                logger.warn("Malformed data with content: {}", source.toString(), e);
            }
        }

        return metaDataList;

    }
}
