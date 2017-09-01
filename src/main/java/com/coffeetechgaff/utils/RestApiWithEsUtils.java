package com.coffeetechgaff.utils;

import com.coffeetechgaff.enums.ValidateAttributeEnums;
import com.coffeetechgaff.model.EsMetadata;
import com.coffeetechgaff.system.EnvProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

/**
 * Created by VivekSubedi on 8/23/17.
 */
public class RestApiWithEsUtils {

    private static final Logger logger = LoggerFactory.getLogger(RestApiWithEsUtils.class);


    public static final String ES_INDEX = "testdata";
    public static final String DOCUMENT_TYPE = "testdoc";
    public static final String TRANSPORT_PORT = "9300";
    public static final String HITS = "hits";
    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String RESPONSE = "Response";
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String GOLD = "gold";
    public static final String DELETED = "deleted";
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
    
    public static final String ES_IP = EnvProperty.getInstance().getEnvVar("ES_IP");
	public static final String ES_TRANSPORT_PORT = EnvProperty.getInstance().getEnvVar("ES_TRANSPORT_PORT");
	public static final String ES_CLUSTER_NAME = EnvProperty.getInstance().getEnvVar("ES_CLUSTER_NAME");
	public static final String ES_METADATA_INDEX_NAME = EnvProperty.getInstance().getEnvVar(
			"ES_METADATA_INDEX_NAME");
	public static final String ES_METADATA_DOCUMENT_TYPE = EnvProperty.getInstance().getEnvVar(
			"ES_METADATA_DOCUMENT_TYPE");
	public static final String ESPAGESIZE = EnvProperty.getInstance().getEnvVar("ES_PAGE_SIZE");
	public static final String PATH_HOME = "path.home";
	public static final String CLUSTER_NAME = "cluster.name";

	// Kafka constant variables
	public static final String KAFKAIP = EnvProperty.getInstance().getEnvVar("KAFKA_IP");
	public static final String KAFKAPORT = EnvProperty.getInstance().getEnvVar("KAFKA_PORT");
	public static final String TOPIC = EnvProperty.getInstance().getEnvVar("KAFKA_TOPIC");
	public static final String BROKERLIST = KAFKAIP + ":" + KAFKAPORT;

    private static ObjectMapper mapper = new ObjectMapper();
    
    private RestApiWithEsUtils() {
    		//private constructor
    }


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
    
    /**
	 * Maps JSON payload to @ElasticSearchDataSourceMetadata POJO and validates
	 * each attributes of the POJO
	 * 
	 * @param metadata
	 *            - JSON in String
	 * @return @Map of attribute and its value
	 */
	public static Map<String, Object> getCleanedAttributes(EsMetadata metadata){

		Map<String, Object> attributeMap = new HashMap<>();

		// Filters out fields from JSON object and puts them into a map if
		// not null or empty
		ValidateAttributeEnums[] attributes = ValidateAttributeEnums.values();
		for(ValidateAttributeEnums validateAttributesEnums : attributes){
			if(logger.isInfoEnabled()){
				logger.info("Clean attribute {}  -- {}", validateAttributesEnums.toString(),
						validateAttributesEnums.validate(metadata));
			}

			attributeMap.putAll(validateAttributesEnums.validate(metadata));
		}
		return attributeMap;

	}
	
	public static Map<String, Object> getFinalCleanedMetadata(String payload){
		// converting payload to our pojo

		logger.info("getFinalCleanedMetadata payload {}", payload);
		Map<String, Object> responseMap = new HashMap<>();
		EsMetadata rawMetadata = null;
		try{
			rawMetadata = mapper.readValue(payload, EsMetadata.class);
		}catch(IOException e1){
			logger.error("getFinalCleanedMetadata Parse error {}", e1.getMessage(), e1);
			responseMap.put(RESPONSE, Response.serverError().entity("Something went wrong while parsing payload").build());
			return responseMap;
		}

		// validating our metadata
		Map<String, Object> attributeMap = getCleanedAttributes(rawMetadata);

		Map<String, Object> newAttributeMap = new HashMap<>(attributeMap);
		newAttributeMap.remove(SOURCE_FIELD_CREATION_DATE);
		newAttributeMap.remove(SOURCE_FIELD_LAST_UPDATED);

		// checking if the attributeMap is empty or not. AttributeMap sholdn't
		// be empty or null
		if(newAttributeMap.isEmpty()){
			responseMap.put(RESPONSE, Response.serverError().entity("Payload mapping error").build());
			return responseMap;
		}

		// convert the validated map to the meatadata object
		EsMetadata metadata = mapper.convertValue(attributeMap, EsMetadata.class);
		responseMap.put("metadata", metadata);

		return responseMap;
	}
}
