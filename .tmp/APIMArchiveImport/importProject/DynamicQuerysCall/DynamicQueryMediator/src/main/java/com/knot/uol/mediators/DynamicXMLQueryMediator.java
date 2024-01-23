package com.knot.uol.mediators;

import org.apache.synapse.MessageContext;

import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.knot.uol.dto.MediatorResponse;
import com.knot.uol.dto.QueryConfig;
import com.knot.uol.utils.CommonUtils;
import com.knot.uol.utils.JDBCConnectionUtil;
import com.knot.uol.utils.PropertiesUtil;





import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


import java.util.Arrays;

import java.util.List;

import java.util.Properties;

public class DynamicXMLQueryMediator extends AbstractMediator {
		
	
	// private static final Logger logger =
	// Logger.getLogger(DynamicXMLQueryMediator.class);
	private static String apiRegistryConfigs = "F:\\JavaNotes\\DynamicQuery\\api-registry-configs.properties";

	@Override
	public boolean mediate(MessageContext messageContext) {

		 String queryName = null,statusCode = null,message = null,errorName = null,errorCode = null;
		 JsonArray dbResponse = null;
		
		try {
			org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
			
		//	apiRegistryConfigs = (String) messageContext.getProperty("apiregistry.config.path"); 
			String inputPayload = (String) messageContext.getProperty("payload");
			
			Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);

			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(inputPayload);
		    queryName = jsonElement.getAsJsonObject().get("queryName").getAsString();
			// Fetch dynamic query configuration from the database based on query name
			QueryConfig queryConfig = JDBCConnectionUtil.getQueryConfigFromDatabase(queryName, properties);
			
			System.out.println("log queryConfig value "+queryConfig);
		

			if (queryConfig == null || org.apache.commons.lang.StringUtils.isEmpty(queryConfig.getPropertiesFile())) {
				// Handle error: Query not found in the database
				System.out.println("queryconfig null");
				
				//Error Response 
				errorCode= "404";
				
				errorName="QueryName Error";
				
				statusCode="404";
				
				message="Query not found in the database please provide valid queryName";
				
				return true;
			}
			
			

			
			//Changes Made Here.
			// Get the dynamic query from the properties file based on query name
			String dynamicQuery = System.getProperty(queryName);

			if (dynamicQuery == null) {
				// Handle error: Query not found in the properties file
				System.out.println("dynamicQuery null");
				// Load the properties file containing dynamic queries
				Properties readQueryProperties = PropertiesUtil.propertiesFileRead(queryConfig.getPropertiesFile());
				dynamicQuery = readQueryProperties.getProperty(queryName);
				if (dynamicQuery == null) {
		             // Handle error: Query not found in the properties file
		             System.out.println("dynamicQuery null");
		         
				//Error Response 
				errorCode= "404";
				
				errorName="properties file";
				
				statusCode="404";
				
				message="Query not found in the properties file";
				
				return true;
				}
			}

			if (queryConfig.getParameters() != null) {
				List<String> inputParams = Arrays.asList(queryConfig.getParameters().split(","));

				// Replace placeholders in the query with request values

				// Check if the parsed element is an object, jsonElement is client given values
				if (jsonElement.isJsonObject()) {

					if (!jsonElement.getAsJsonObject().get("parameters").equals(null)) {
						
							JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("parameters");
							for (String keys : inputParams) {
								// Access key-value pairs
								System.out.println(jsonObject + "/" + jsonObject.get(keys));
								if (jsonObject.get(keys) != null) {
									String dynamicVal = jsonObject.get(keys).getAsString();
									System.out.println(keys + " dynamicVal : " + dynamicVal);
									dynamicQuery = dynamicQuery.replace("{" + keys + "}", dynamicVal);
								}
							}
					}
				} else {
					System.out.println("Invalid JSON: Not an object.");
					
					//Error Response 
					errorCode= "422";
					
					errorName="Invalid Data Format";
					
					statusCode="422";
					
					message="Invalid JSON: Not an object.";
					
					return true;
				}

				System.out.println("dynamic query data::" + dynamicQuery);
			}
			// Use the constructed dynamic query as needed
			// For example, you can set it as a property in the message context
			messageContext.setProperty("dynamicQuery", dynamicQuery);

			// Connect to the database based on system name and schema name
			String systemName = queryConfig.getSystemName();
			String schemaName = queryConfig.getSchemaName();

			// Execute the dynamic query using the database connection

			Connection conn = JDBCConnectionUtil.connectToDatabase(systemName, schemaName, properties);
			System.out.println("==============" + conn);
			if (conn == null) {
				// Handle error: Unable to connect to the database
				System.out.println("is connection null");
				
				//Error Response 
				errorCode= "500";
				
				errorName="Internal Server Error";
				
				statusCode="400";
				
				message="DB Connection null";
			}
			System.out.println("===>Dynmc query::" + dynamicQuery);
			PreparedStatement preparedStatement = conn.prepareStatement(dynamicQuery);
			
			boolean execute=preparedStatement.execute();
			
			JsonArray jsonarray ;
			if(execute) {
				ResultSet resultSet = preparedStatement.getResultSet();
						 jsonarray = CommonUtils.prepareResultSetJSONObject(resultSet);
						System.out.println("jsonarray==>" + jsonarray);
						messageContext.setProperty("dbResponse", jsonarray);
						dbResponse=jsonarray;
						statusCode="200";
						message="Success";
						resultSet.close();
			} else 
			{
				
				int effectedRows = preparedStatement.getUpdateCount();
				//messageContext.setProperty("dbResponse", effectedRows);
				
				System.out.println(effectedRows+" rows effected");
				
				String singleElement = effectedRows+" rows effected";
				
				statusCode="200";
				
				message="Success "+singleElement;

			}

			
			// Close the database resources
			
			preparedStatement.close();
			conn.close();

		} catch (Exception e) {
			// Handle database-related exceptions
			//e.printStackTrace();
			// Handle error: Database query execution error
			System.out.println("connection was null");
			
			//Error Response 
			errorCode= "500";
			
			errorName="unexpected error occured";
			
			statusCode="500";
			
			message=e.getMessage();
			

			
		}finally {
//		messageContext.setResponse(true);
		
		MediatorResponse obj = CommonUtils.buildResponse(queryName,statusCode,message,dbResponse,errorName,errorCode );
		Gson gson = new Gson();
		String json = gson.toJson(obj);

		messageContext.setProperty("mediatorResponse", json);
		//System.out.println("Final DB Response "+messageContext.getProperty("dbResponse"));
		
		}
		return true;

	}
	

	

}
