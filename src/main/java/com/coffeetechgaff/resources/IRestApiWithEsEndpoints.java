package com.coffeetechgaff.resources;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.coffeetechgaff.model.EsMetadata;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * @author VivekSubedi
 *
 */
@Path("/metadata")
@Api(tags = "REST endpoints to retrieve data from elasticsearch")
public interface IRestApiWithEsEndpoints {
	
	/**
	 * This REST end point will take a JSON payload to create a new metadata
	 * in Elasticsearch (and AWS S3?). The URL looks like
	 * http://localhost:8080/rest/v1/metadata with the JSON payload in
	 * the body of the POST
	 * 
	 * @param payload
	 *            - JSON formated string representing the data source
	 * @return the success or failure of the data source creation
	 * @throws InterruptedException
	 * @throws AccessException
	 * @throws IOException
	 */
	@POST
	@ApiOperation(value = "Registers a data source whith user provided metadata")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "", response = EsMetadata.class),
			@ApiResponse(code = 500, message = "Something went wrong"),})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMetadataInEs(@Context HttpHeaders header,
			@ApiParam(value = "Metadata object", required = true) String payload);
	
	
	/**
	 * This rest end point will take a datasource ID string so that it can
	 * delete the datasource, The URL looks like
	 * http://localhost:8080/rest
	 * /metadata/edsb4a4e-0cce-44d9-a833-cd0f622cf5ff
	 * 
	 * @param id
	 *            - the id of the data source
	 * @return the success or failure of the data source deletion
	 * @throws AccumuloException
	 * @throws AccumuloSecurityException
	 * @throws AccessException
	 * @throws IOException
	 */
	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Deletes a specified data source from storage")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Data source {id} has been successfully deleted."),
			@ApiResponse(code = 500, message = "Something went wrong while deleting record"),})
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteMetadataFromEs(@Context HttpHeaders header,
			@ApiParam(value = "ID of metadata", required = true) @PathParam("id") String id);
	
	/**
	 * This REST end point will take a unique ID to query data sources from
	 * Elasticsearch and GeoWave. The URL looks like
	 * http://localhost:8080
	 * /rest/metadata/4cf3aab5-919f-48ce-abe7-5d3d790c7e7d
	 *
	 * http://stackoverflow.com/questions/10687505/uuid-format-8-4-4-4-12-why
	 *
	 * @param id
	 *            - Unique identifier of the data source
	 * @return the data source with the specified uid
	 * @throws AccessException
	 * @throws IOException
	 */

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Reads and returns the data source with the specified ID", response = EsMetadata.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation"),
			@ApiResponse(code = 204, message = "no content"),
			@ApiResponse(code = 500, message = "Something went wring while query data"),})
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryById(@Context HttpHeaders header,
			@ApiParam(value = "ID of data", required = true) @PathParam("id") String id);
	
	
	/**
	 * This REST end point will take a query parameter to query data sources The
	 * URL looks like http://localhost:8080/rest/metadata/params/{fieldname}
	 *
	 * @param header
	 * @param p
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("/params/{fieldName}")
	@ApiOperation(value = "Reads and returns the data source that matches the query parameter", response = EsMetadata.class, responseContainer = "List")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Operation"),
			@ApiResponse(code = 204, message = "no content"),
			@ApiResponse(code = 500, message = "Something went wrong"),})
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryByParameters(
			@Context HttpHeaders header,
			@ApiParam(value = "fieldName Path Parameter", required = true) @PathParam("fieldName") String fieldName,
			@ApiParam(value = "startDateTime Query Parameter", required = false) @QueryParam("startDateTime") String startDateTime,
			@ApiParam(value = "endDateTime Query Parameter", required = false) @QueryParam("endDateTime") String endDateTime) throws IOException;
	
	
	/**
	 * This REST end point will take the datasource Id and JSON payload to
	 * update the data source
	 * http://localhost:8080/rest/metadata/edsb4a4e
	 * -0cce-44d9-a833-cd0f622cf5ff with the JSON payload in the body of the
	 * POST
	 * 
	 * @param sourceId
	 *            - the id of metadata
	 * @param payload
	 *            - JSON formated string representing the data source
	 * @return the success or failure of the data source update
	 * @throws AccessException
	 * @throws IOException
	 */
	@PUT
	@Path("/{id}")
	@ApiOperation(value = "Updates a specified metadata whith user provided value", response = EsMetadata.class, notes = "Updates any metadata attributes. If the user provides null value for a attributes, "
			+ "it will be ignored.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "Something went wrong"),})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMetadata(@Context HttpHeaders header,
			@ApiParam(value = "ID of Data Source", required = true) @PathParam("id") String sourceId,
			@ApiParam(value = "Metadata object", required = true) String payload) throws IOException;
	
	/**
	 * REST endpoint will take payload in JSON format and query the data from elasticsearch
	 * 
	 * @param header - header
	 * @param payload - metadata object in json format
	 * @return @List of EsMetadata
	 */
	@POST
	@Path("/query")
	@ApiOperation(value = "Reads and returns all metadata specified in the JSON payload", notes = "It will only return those metadata which matches all the attributes provided in payload. Null or blank attributes will be ignored from the ES query.", response = EsMetadata.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 204, message = "No content"),
			@ApiResponse(code = 500, message = "Something went wrong"),})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response queryByMetadata(@Context HttpHeaders header,
			@ApiParam(value = "Metadata object", required = true) String payload);
}
