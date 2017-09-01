package com.coffeetechgaff.enums;

import java.util.ArrayList;
import java.util.List;
import com.coffeetechgaff.utils.RestApiWithEsUtils;

public enum Attributes {
	NAME(RestApiWithEsUtils.SOURCE_FIELD_NAME), 
	DESCRIPTION(RestApiWithEsUtils.SOURCE_FIELD_DESCRIPTION), 
	CLASSIFICATION(RestApiWithEsUtils.SOURCE_FIELD_CLASSIFICATION), 
	MATURITY(RestApiWithEsUtils.SOURCE_FIELD_MATURITY), 
	SOURCE(RestApiWithEsUtils.SOURCE), 
	ID(RestApiWithEsUtils.ID), 
	CREATIONDATE(RestApiWithEsUtils.SOURCE_FIELD_CREATION_DATE), 
	LASTUPDATED(RestApiWithEsUtils.SOURCE_FIELD_LAST_UPDATED), 
	EXPIRATIONDATE(RestApiWithEsUtils.SOURCE_FIELD_EXPIRATION_DATE), 
	STATUS(RestApiWithEsUtils.STATUS);

	private String attributeName;

	private Attributes(String attributeName){
		this.attributeName = attributeName;
	}

	public String getAttributeName(){
		return attributeName;
	}

	// these fields must be in a metadata json object
	public static List<Attributes> getPotentialValueMissingAttributes(){
		List<Attributes> list = new ArrayList<>();
		list.add(NAME);
		list.add(DESCRIPTION);
		list.add(CLASSIFICATION);
		list.add(MATURITY);
		list.add(SOURCE);
		return list;

	}

}
