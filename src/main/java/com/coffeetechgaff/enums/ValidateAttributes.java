package com.coffeetechgaff.enums;

import java.util.Map;

/**
 * 
 * @author VivekSubedi
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
@FunctionalInterface
public interface ValidateAttributes<K, V, T> {
	/**
	 * Method to validate the specific attributes of user provided datatype
	 * 
	 * @param metaData
	 *            -user defined pojo
	 * @return @Map with attributename and its value
	 */
	Map<K, V> validate(T metaData);
}
