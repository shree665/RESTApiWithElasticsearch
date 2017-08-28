package com.coffeetechgaff.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvPropertyReader {
	
	private static final Logger logger = LoggerFactory.getLogger(EnvPropertyReader.class);
	
	private InputStream inputStream;

	public Properties getPropValues() throws IOException{
		Properties prop = new Properties();
		try{
			String propFileName = "env.properties";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if(inputStream != null){
				prop.load(inputStream);
			}else{
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
		}catch(Exception e){
			logger.error("Exception: " , e);
		}finally{
			if(inputStream != null){
				inputStream.close();
			}
		}
		return prop;
	}

}
