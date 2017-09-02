package com.coffeetechgaff.enums;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffeetechgaff.model.EsMetadata;
import com.coffeetechgaff.utils.DateUtils;

/**
 * 
 * @author VivekSubedi
 *
 */
public enum ValidateAttributeEnums implements ValidateAttributes<String, Object, EsMetadata> {
	NAME{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getName()) != null){
				localMap.put(Attributes.NAME.getAttributeName(), metaData.getName());
			}
			return localMap;
		}

	},
	DESCRIPTION{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getDescription()) != null){
				localMap.put(Attributes.DESCRIPTION.getAttributeName(), metaData.getDescription());
			}
			return localMap;
		}

	},
	CLASSIFICATION{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getClassification()) != null){
				localMap.put(Attributes.CLASSIFICATION.getAttributeName(), metaData.getClassification());
			}
			return localMap;
		}

	},
	MATURITY{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getMaturity()) != null){
				localMap.put(Attributes.MATURITY.getAttributeName(), metaData.getMaturity());
			}
			return localMap;
		}

	},
	STATUS{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getStatus()) != null){
				localMap.put(Attributes.STATUS.getAttributeName(), metaData.getStatus());
			}
			return localMap;
		}

	},
	SOURCE{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(metaData.getSource() != null){
				localMap.put(Attributes.SOURCE.getAttributeName(), metaData.getSource().toMap());
			}
			return localMap;
		}
	},
	ID{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getId()) != null){
				localMap.put(Attributes.ID.getAttributeName(), metaData.getId());
			}
			return localMap;
		}

	},
	CREATIONDATE{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getCreationDate()) == null){
				localMap.put(Attributes.CREATIONDATE.getAttributeName(), LocalDateTime.now().toString());
			}else{
				String formattedDate = LocalDateTime.now().toString();
				try{
					String format = DateUtils.determineDateFormat(metaData.getCreationDate());
					String logMessage = "Data Source the date time " + metaData.getCreationDate()
							+ " has been parsed to " + format + " ; ";
					logger.info(logMessage);
					if(format != null){
						formattedDate = DateUtils.getFormattedDateTime(metaData.getCreationDate(), format);
					}else{
						String errorMessage = "couldn't parse the date [" + metaData.getCreationDate()
								+ "]. The required format is yyyy-MM-dd HH:mm:ss. Passing current time; ";
						logger.error(errorMessage);
					}
				}catch(ParseException e){
					String errorMessage = "couldn't parse the date [" + metaData.getCreationDate()
							+ "]. The required format is yyyy-MM-dd HH:mm:ss. Passing current time; " + e.getMessage()
							+ "; ";
					logger.error(errorMessage);
				}
				localMap.put(Attributes.CREATIONDATE.getAttributeName(), formattedDate);
			}
			return localMap;
		}
	},
	LASTUPDATED{
		@Override
		public Map<String, Object> validate(EsMetadata metaData){
			Map<String, Object> localMap = new HashMap<>();
			if(validateString(metaData.getLastUpdated()) == null){
				localMap.put(Attributes.LASTUPDATED.getAttributeName(), LocalDateTime.now().toString());
			}else{
				String formattedDate = LocalDateTime.now().toString();
				try{
					String format = DateUtils.determineDateFormat(metaData.getLastUpdated());
					String logMessage = "Data Source the date time " + metaData.getLastUpdated()
							+ " has been parsed to " + format + " ; ";
					logger.info(logMessage);
					if(format != null){
						formattedDate = DateUtils.getFormattedDateTime(metaData.getLastUpdated(), format);
					}else{
						String errorMessage = "couldn't parse the date [" + metaData.getLastUpdated()
								+ "]. The required format is yyyy-MM-dd HH:mm:ss. Passing current time; ";
						logger.error(errorMessage);
					}
				}catch(ParseException e){
					String errorMessage = "couldn't parse the date [" + metaData.getLastUpdated()
							+ "]. The required format is yyyy-MM-dd HH:mm:ss. Passing current time; " + e.getMessage()
							+ "; ";
					logger.error(errorMessage);
				}
				localMap.put(Attributes.LASTUPDATED.getAttributeName(), formattedDate);
			}
			return localMap;
		}
	},
	EXPIRATIONDATE{
		@Override
		public Map<String, Object> validate(EsMetadata metadata){
			Map<String, Object> localMap = new HashMap<>();
			if(metadata.getExpirationDate() != null){
				localMap.put(Attributes.EXPIRATIONDATE.getAttributeName(), metadata.getExpirationDate());
			}
			return localMap;
		}
	};
	
	private static final Logger logger = LoggerFactory.getLogger(ValidateAttributeEnums.class);
	
	protected String validateString(String value){
		if(StringUtils.isNotBlank(value)){
			return value.trim();
		}

		return null;
	}

}
