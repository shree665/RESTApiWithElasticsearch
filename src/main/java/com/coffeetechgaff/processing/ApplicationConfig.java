package com.coffeetechgaff.processing;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffeetechgaff.dao.ElasticsearchOperation;
import com.coffeetechgaff.dao.RestApiWithEsDao;
import com.coffeetechgaff.resources.IRestApiWithEsEndpoints;
import com.coffeetechgaff.resources.RestApiWithEsEndpoints;
import com.coffeetechgaff.service.IRestApiWithEsService;
import com.coffeetechgaff.service.RestApiWithEsService;
/**
 * 
 * @author VivekSubedi
 *
 */
@ApplicationPath("/rest")
public class ApplicationConfig extends ResourceConfig {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
	
	public ApplicationConfig(){
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(RestApiWithEsEndpoints.class).to(IRestApiWithEsEndpoints.class);
				bind(RestApiWithEsDao.class).to(ElasticsearchOperation.class);
				bind(RestApiWithEsService.class).to(IRestApiWithEsService.class);
			}
		});
		register(IRestApiWithEsEndpoints.class);
		logger.info("resources have been initialized...");
	}

}
