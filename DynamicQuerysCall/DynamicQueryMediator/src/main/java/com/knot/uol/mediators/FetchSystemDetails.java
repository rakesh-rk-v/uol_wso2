package com.knot.uol.mediators;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.knot.uol.dto.QueryConfig;
import com.knot.uol.utils.JDBCConnectionUtil;
import com.knot.uol.utils.PropertiesUtil;

public class FetchSystemDetails extends AbstractMediator {

	@Override
	public boolean mediate(MessageContext mc) {
		String queryName= (String) mc.getProperty("queryName");
		String apiRegistryConfigs = (String) mc.getProperty("apiregistryConfigPath");
		if(queryName==null || queryName==" ") {
			log.info(new String("Please Enter a Valid QueryName"));
			return false;
		}
		String queryValue;
		if(System.getProperty(queryName)!=null) {
			queryValue = System.getProperty(queryName);
			log.info(new String("QueryName is in System Properties = "+queryValue));
			mc.setProperty("Query", queryValue);
			return true;
		}
		Properties properties = PropertiesUtil.propertiesFileRead(apiRegistryConfigs);
		try {
			QueryConfig queryConfig = JDBCConnectionUtil.getQueryConfigFromDatabase(queryName, properties);
			log.info(new String("Query Name = "+queryName));
			log.info("Properties File Path :: " + queryConfig.getPropertiesFile());
			if (queryConfig == null || org.apache.commons.lang.StringUtils.isEmpty(queryConfig.getPropertiesFile())) {
				// Handle error: Query not found in the database
			log.info("QueryConfig Object info is null /not existed in DB");
			return false;
			}
			Properties readQueryProperties = PropertiesUtil.propertiesFileRead(queryConfig.getPropertiesFile());
			log.debug(new String("Properties File = "+queryConfig.getPropertiesFile()));
			// Get the dynamic query from the properties file based on query name
			String dynamicQuery = readQueryProperties.getProperty(queryName);
			log.info(new String("Dynamic Query = "+dynamicQuery));
			mc.setProperty("Query", dynamicQuery);
			
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		log.info(new String("Query Name = "+queryName));
		
		return false;
	}
	public String getSystemDetailsFromDb(String queryName) {
		Connection con;
		return queryName;
		
	}

}
