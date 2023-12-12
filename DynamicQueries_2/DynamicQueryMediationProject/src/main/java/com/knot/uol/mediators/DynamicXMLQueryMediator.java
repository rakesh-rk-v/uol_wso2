package com.knot.uol.mediators;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.knot.uol.dto.QueryConfig;
import com.knot.uol.utils.CommonUtils;
import com.knot.uol.utils.JDBCConnectionUtil;
import com.knot.uol.utils.PropertiesUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DynamicXMLQueryMediator extends AbstractMediator {
    // private static final Logger logger =
    // Logger.getLogger(DynamicXMLQueryMediator.class);
    // private static String apiRegistryConfigs = "D:\\IntegrationStudio\\mi-dashboard\\conf\\api-registry-configs.properties";

  

    @Override
    public boolean mediate(MessageContext messageContext) {
    	 // org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
    	try {
        String apiRegistryConfigs = (String) messageContext.getProperty("apiRegistryConfigPath");
        System.out.println("File Registry Path = " + apiRegistryConfigs + " Size = " + apiRegistryConfigs.length());

        String inputPayload = (String) messageContext.getProperty("payload");
        System.out.println("PayLoad = " + inputPayload);
        Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(inputPayload);
        String queryName = jsonElement.getAsJsonObject().get("queryName").getAsString();
        try {
            if (apiRegistryConfigs == null || apiRegistryConfigs.equals(null) || apiRegistryConfigs.equals(" ")
                    || apiRegistryConfigs.length() == 0) {
                System.out.println("Please Set the API Registry Path Correctly");
            }
        } catch (Exception e) {
            System.out.println("IF Condition Failed So Exception Failed");
  

            e.printStackTrace();
        }
        
       
    		
        // Fetch dynamic query configuration from the database based on query name
        QueryConfig queryConfig = JDBCConnectionUtil.getQueryConfigFromDatabase(queryName, properties);

        if (queryConfig == null) {
            // Handle error: Query not found in the database
            System.out.println("queryconfig null");

        }
        String dynamicQuery=System.getProperty(queryName);
        if(queryName== null) {
        // Load the properties file containing dynamic queries
        Properties readQueryProperties = PropertiesUtil.propertiesFileRead(queryConfig.getPropertiesFile());
        // Get the dynamic query from the properties file based on query name
         dynamicQuery = readQueryProperties.getProperty(queryName);
         
         if (dynamicQuery == null) {
             // Handle error: Query not found in the properties file
             System.out.println("dynamicQuery null");
         }
    	}
       

        if (queryConfig.getParameters() != null) {
            List<String> inputParams = Arrays.asList(queryConfig.getParameters().split(","));

            // Replace placeholders in the query with request values

            // Check if the parsed element is an object
            if (jsonElement.isJsonObject()) {

                if (jsonElement.getAsJsonObject().get("parameters") != null) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("parameters");
                    for (String keys : inputParams) {
                        // Access key-value pairs
                        System.out.println(jsonObject + "/" + jsonObject.get(keys));
                        if (jsonObject.get(keys) != null) {
                            String dynamicVal = jsonObject.get(keys).getAsString();
                            System.out.println(keys + "dynamicVal : " + dynamicVal);
                            dynamicQuery = dynamicQuery.replace("{" + keys + "}", dynamicVal);
                        }
                        // Print key-value pairs
                    }
                }
            } else {
                System.out.println("Invalid JSON: Not an object.");
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
        }
        System.out.println("===>Dynmc query::" + dynamicQuery);
        PreparedStatement preparedStatement = conn.prepareStatement(dynamicQuery);
        ResultSet resultSet = preparedStatement.executeQuery();

        JsonArray jsonarray = CommonUtils.prepareResultSetJSONObject(resultSet);
        System.out.println("jsonarray==>" + jsonarray);
        messageContext.setProperty("response", jsonarray);
        // Close the database resources
        resultSet.close();
        preparedStatement.close();
        conn.close();

    } catch (Exception e) {
        // Handle database-related exceptions
        e.printStackTrace();
        // Handle error: Database query execution error
        System.out.println("connection was null");
    }
		return true;
}
    
    public boolean existing(MessageContext messageContext) {

    	String inputPayload = (String) messageContext.getProperty("payload");
    	JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(inputPayload);
        String queryName = jsonElement.getAsJsonObject().get("queryName").getAsString();
    	if(queryName.equals(null)|| queryName==" " || queryName.isBlank()) {
    		log.error(new String("The 'queryName'  parameter is NULL"));
    		return false;
    	}
    	if(System.getProperty(queryName)== null) {
    		log.debug(new String(queryName+" is not Loaded from properties File."));
    		boolean response = existing(messageContext);
    		return response;
    	}
    	try {
			QueryConfig queryConfig = JDBCConnectionUtil.getDBCredentialsFromDatabase(queryName);
		
    	 if (queryConfig.getParameters() != null) {
             List<String> inputParams = Arrays.asList(queryConfig.getParameters().split(","));

             // Replace placeholders in the query with request values

             // Check if the parsed element is an object
             
             if (jsonElement.isJsonObject()) {

                 if (jsonElement.getAsJsonObject().get("parameters") != null) {
                     JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("parameters");
                     for (String keys : inputParams) {
                         // Access key-value pairs
                         System.out.println(jsonObject + "/" + jsonObject.get(keys));
                         if (jsonObject.get(keys) != null) {
                             String dynamicVal = jsonObject.get(keys).getAsString();
                             System.out.println(keys + "dynamicVal : " + dynamicVal);
                             queryName = queryName.replace("{" + keys + "}", dynamicVal);
                         }
                         // Print key-value pairs
                     }
                 }
             } else {
                 System.out.println("Invalid JSON: Not an object.");
             }

             System.out.println("dynamic query data::" + queryName);
         }
         // Use the constructed dynamic query as needed
         // For example, you can set it as a property in the message context
         messageContext.setProperty("dynamicQuery", queryName);
         
         
         // Connect to the database based on system name and schema name
         String systemName = queryConfig.getSystemName();
         String schemaName = queryConfig.getSchemaName();

         // Execute the dynamic query using the database connection

         Connection conn = JDBCConnectionUtil.connectingDB(systemName, schemaName);
         System.out.println("==============" + conn);
         if (conn == null) {
             // Handle error: Unable to connect to the database
             System.out.println("is connection null");
         }
         System.out.println("===>Dynmc query::" + queryName);
         PreparedStatement preparedStatement = conn.prepareStatement(queryName);
         ResultSet resultSet = preparedStatement.executeQuery();
         
         JsonArray jsonarray = CommonUtils.prepareResultSetJSONObject(resultSet);
         System.out.println("jsonarray==>" + jsonarray);
         messageContext.setProperty("response", jsonarray);
         // Close the database resources
         resultSet.close();
         preparedStatement.close();
         conn.close();
         
    	} catch (SQLException e) {
			log.error(e);
			e.printStackTrace();
		}
        return true;

    }
}
