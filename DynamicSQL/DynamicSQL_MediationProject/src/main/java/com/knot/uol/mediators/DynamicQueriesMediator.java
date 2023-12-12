package com.knot.uol.mediators;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.google.gson.Gson;
import com.knot.uol.dto.QueryConfig;

public class DynamicQueriesMediator extends AbstractMediator {

	@Override
	public boolean mediate(MessageContext mc) {
		// Retrieving the payload from the MessageContext
		String inputPayload = (String) mc.getProperty("payLoad");
		// Deserializing the JSON payload into a QueryConfig object
		QueryConfig queryConfig = new Gson().fromJson(inputPayload, QueryConfig.class);
		String queryName = queryConfig.getQueryName();
		System.out.println("Query Name : "+queryName);
		if(queryName.isBlank()||queryName.isEmpty()) {	
			log.error(new String("Query Name Is Empty."));
			mc.setProperty("ERROR_MESSAGE", "THE INPUT queryName IS EMPTY");
			mc.setProperty("ERROR_MESSAGE", "THE GIVEN QUERY NAME  IS EMPTY");
			return false;
		}
		if(System.getProperty(queryName).equals(null)) {
			String query = System.getProperty(queryName);
			
		}

		return false;
	}

}
