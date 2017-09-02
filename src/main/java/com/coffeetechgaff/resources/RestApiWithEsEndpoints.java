package com.coffeetechgaff.resources;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.json.JSONObject;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffeetechgaff.model.EsMetadata;
import com.coffeetechgaff.service.IRestApiWithEsService;
import com.coffeetechgaff.utils.DateUtils;
import com.coffeetechgaff.utils.RestApiWithEsUtils;
import com.google.json.JsonSanitizer;

/**
 * 
 * @author VivekSubedi
 *
 */
@RequestScoped
public class RestApiWithEsEndpoints implements IRestApiWithEsEndpoints {
	
	private static final Logger logger = LoggerFactory.getLogger(RestApiWithEsEndpoints.class);
	
	private static final String INVALID_DATETIME_MESSAGE = "DateTime is invalid format";
	private static final String DATE_MIN = "0001-01-01";
	private static final String DATE_MAX = "9999-12-31";
	
	@Inject
	private IRestApiWithEsService esService;

	@Override
	public Response createMetadataInEs(HttpHeaders header, String payload) {
		// check for the data source metadata
		if(StringUtils.isBlank(payload)){
			return Response.status(Status.NOT_ACCEPTABLE).entity("Payload can not be null or empty").build();
		}
		
		// sanitizing json
		String sanitizedJson = JsonSanitizer.sanitize(payload);

		// convert the metadata payload text to a json object
		Map<String, Object> createMetadata = RestApiWithEsUtils.getFinalCleanedMetadata(sanitizedJson);

		// see if there was an error parsing the json metadata text
		if(createMetadata.containsKey(RestApiWithEsUtils.RESPONSE)){
			// there was a parse error
			return (Response) createMetadata.get(RestApiWithEsUtils.RESPONSE);
		}
		
		EsMetadata metadata = (EsMetadata) createMetadata.get("metadata");
		logger.info("Successfully parsed the Payload to Metadata object");
		return esService.indexMetadata(metadata);
	}

	@Override
	public Response deleteMetadataFromEs(HttpHeaders header, String id) {
		if(StringUtils.isBlank(id)){
			return Response.status(Status.NOT_ACCEPTABLE).entity("uid cannot be null or empty").build();
		}

		// cleaning up the sourceId
		String cleanId = Encode.forJava(id).replace("\\", "");

		logger.info("Deleting metadata [{}]", cleanId);
		return esService.deleteMetadata(cleanId);
	}

	@Override
	public Response queryById(HttpHeaders header, String id) {
		if(StringUtils.isBlank(id)){
			return Response.status(Status.NOT_ACCEPTABLE).entity("uid cannot be null or empty").build();
		}
		
		String cleanId = Encode.forJava(id).replace("\\", "");
		logger.info("Retrieving a data source with uid [{}]", cleanId);
		JSONObject payloadObject = new JSONObject();
		payloadObject.put("id", cleanId);
		
		return esService.retrieveMetadata(payloadObject.toString());
	}

	@Override
	public Response queryByParameters(HttpHeaders header, String fieldName, String startDateTime, String endDateTime) throws IOException {
		if (StringUtils.isBlank(fieldName)) {
			return Response.status(Status.NOT_ACCEPTABLE).entity("fieldname cannot be null or empty").build();
		}
		
		String encodedFieldName = encodeIfNotNull(fieldName);
		String encodedStartDateTime = encodeIfNotNull(startDateTime);
		String encodedEndDateTime = encodeIfNotNull(endDateTime);
		
		logger.info("Query is on fieldName [{}]", encodedFieldName);
		String formattedStartDateTime;
		String formattedEndDateTime;
		try{
			formattedStartDateTime = formatDateTime(encodedStartDateTime, DATE_MIN);
			logger.info("formatted start datetime is [{}]", formattedStartDateTime);
			formattedEndDateTime = formatDateTime(encodedEndDateTime, DATE_MAX);
			logger.info("formatted end datetime is [{}]", formattedEndDateTime);
		}catch(ParseException e){
			logger.error(INVALID_DATETIME_MESSAGE);
			return Response.serverError().entity(INVALID_DATETIME_MESSAGE).build();
		}
		return esService.createEsRangeQuery(formattedStartDateTime, formattedEndDateTime, encodedFieldName);
	}
	
	private String encodeIfNotNull(String string){
		return string == null ? null : Encode.forJava(string);
	}
	
	private String formatDateTime(String date, String defaultDate) throws ParseException{
		String dateToFormat;
		if(StringUtils.isBlank(date)){
			logger.info("datetime is null or empty, default date [{}] will be used", defaultDate);
			dateToFormat = defaultDate;
		}else{
			dateToFormat = date;
		}
		String format = DateUtils.determineDateFormat(dateToFormat);
		if(format != null){
			return DateUtils.getFormattedDateTime(dateToFormat, format);
		}else{
			throw new ParseException("Try date in format yyyy-MM-dd HH:mm:ss", 0);
		}
	}

	@Override
	public Response updateMetadata(HttpHeaders header, String sourceId, String payload) throws IOException {
		if(StringUtils.isBlank(sourceId)){
			return Response.status(Status.NOT_ACCEPTABLE).entity("uid cannot be null or empty").build();
		}
		
		if(StringUtils.isBlank(payload)){
			return Response.status(Status.NOT_ACCEPTABLE).entity("payload cannot be null or empty").build();
		}
		
		// sanitizing json
		String sanitizedJson = JsonSanitizer.sanitize(payload);
		
		// Convert the string payload to a metadata object
		Map<String, Object> createMetadata = RestApiWithEsUtils.getFinalCleanedMetadata(sanitizedJson);
		// Confirm that there were no errors in parsing the metadata
		if(createMetadata.containsKey(RestApiWithEsUtils.RESPONSE)){
			// there was a parse error
			logger.error("Payload improperly formatted and cannot be parsed");
			return (Response) createMetadata.get(RestApiWithEsUtils.RESPONSE);
		}
		// Extract the metadata for the service and configure the ID
		EsMetadata metadata = (EsMetadata) createMetadata.get("metadata");
		
		// cleaning up the sourceId
		String clearedId = Encode.forJava(sourceId).replace("\\", "");
		metadata.setId(clearedId);
		
		logger.info("Updating metadata for [{}] ", clearedId);
		return esService.updateMetdata(metadata);
	}

	@Override
	public Response queryByMetadata(HttpHeaders header, String payload) {
		if(StringUtils.isBlank(payload)){
			return Response.status(Status.NOT_ACCEPTABLE).entity("payload cannot be null or empty").build();
		}
		
		// sanitizing json
		String sanitizedJson = JsonSanitizer.sanitize(payload);

		logger.info("Retrieving all data sources that meet payload");
		return esService.retrieveMetadata(sanitizedJson);
	}


}
