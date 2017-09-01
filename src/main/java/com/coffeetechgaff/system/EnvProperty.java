package com.coffeetechgaff.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 
 * @author VivekSubedi
 *
 */
public class EnvProperty {
	
	private static final Logger logger = LoggerFactory.getLogger(EnvProperty.class);

	private static EnvProperty instance = null;
	private EnvPropertyReader propertiesGetter = new EnvPropertyReader();
	private boolean readPropFile = true;

	private Properties readProperties(){
		Properties properties = new Properties();
		try{
			properties = propertiesGetter.getPropValues();
		}catch(Exception e) {
			logger.error("Exception ", e);
		}
		return properties;
	}

	private EnvProperty(){}

	/**
	 * Returns an instance that only reads the system enviroment or property
	 * values.
	 *
	 * @return EnvProperty instance that seraches system env and property
	 *         values.
	 */
	public static EnvProperty getSystemOnly(){
		EnvProperty env = new EnvProperty();
		env.readPropFile = false;
		return env;
	}

	public static synchronized EnvProperty getInstance(){
		if(instance == null)
			instance = new EnvProperty();
		return instance;
	}

	/**
	 * Get the key-value from the following places in this priority order:
	 *
	 * <pre>
	 * 1. System property (-Dproperty=123 or System.setProperty() )
	 * 2. Propertyfile (only file name of 'env.properties' in the classpath)
	 * 3. Environment variable ( 'set property=123' in shell )
	 * </pre>
	 *
	 * @param key
	 * @return if nothing found, return null
	 */
	public String getEnvVar(String key){
		// load java properties first (for runtime properties, mostly from
		// tests)
		String envValue = System.getProperty(key);
		if(null != envValue)
			return envValue;
		// load environment variables second (for properties loaded into docker
		// containers mostly)
		envValue = System.getenv(key);
		if(null != envValue)
			return envValue;
		// load properties file third (most general properties, mostly defaults
		// for local tests)
		if(readPropFile){
			Properties properties = instance.readProperties();
			envValue = properties.getProperty(key);
			if(null != envValue)
				return envValue;
		}
		return envValue;
	}

	public String getEnvVar(String key, String defValue){
		String value = getEnvVar(key);
		if(null == value){
			logger.debug("Property {} is not set, returning default '{}'", key, defValue);
			return defValue;
		}
		return value;
	}

	/**
	 * This method preserves the order we used to have, where environment
	 * variables override the env.properties file.
	 */
	public String getEnv(String key){
		String envValue = System.getProperty(key);
		if(null != envValue)
			return envValue;

		envValue = System.getenv(key);
		if(null != envValue)
			return envValue;
		else{
			Properties properties = instance.readProperties();
			return properties.getProperty(key);
		}
	}

}
