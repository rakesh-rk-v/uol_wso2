package com.knot.uol.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.knot.uol.dto.QueryConfig;


public class JDBCConnectionUtil {

	// Method to connect to the appropriate database based on system name and schema
	// name
	public static Connection connectToDatabase(String systemName, String schemaName) {
		// Implement database connection logic based on the system name
		// For example, use different JDBC drivers and connection parameters for MySQL
		// and Oracle
		Connection connectionObj = null;
		try {
			String dbUrl = null, dbUser = null, dbPassword = null ;
			
				dbUrl = System.getProperty(systemName + ".db.url"); // Change this to your database URL
				dbUser = System.getProperty(systemName + ".db.username");
				dbPassword = System.getProperty(systemName + ".db.password");
			String mysqlUrl = dbUrl + schemaName + "?user=" + dbUser + "&password=" + dbPassword;

			System.out.println("Database Connection URL::" + mysqlUrl);
			// connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			connectionObj = DriverManager.getConnection(mysqlUrl);

		} catch (Exception e) {
			// Handle database connection errors
			e.printStackTrace();
			System.out.println(e);
		}

		System.out.println("connection ==>" + connectionObj);
		return connectionObj;
	}
	
	// Method to fetch query configuration from the database based on query name
	public static QueryConfig getQueryConfigFromDatabase(String queryName) throws SQLException {
		// Load the properties file containing dynamic queries
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		String dbUrl = null, dbUser = null, dbPassword = null, queryConfigPath = null, dbDriver = null;// Change this to your database
																						// URL
			dbUrl = System.getProperty("apiregistry.db.url"); // Change this to your database URL
			dbUser = System.getProperty("apiregistry.db.username");
			dbPassword = System.getProperty("apiregistry.db.password");
			dbDriver = System.getProperty("apiregistry.db.driver");
			queryConfigPath = System.getProperty("apiregistry.db.queries.basic.path");
	

		QueryConfig queryConfig = null;
		try {
			// Establish a database connection
			Class.forName(dbDriver);
			 connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			System.out.println("query name::" + queryName);
			// Prepare and execute the SQL query to retrieve query configuration based on
			String sql = "SELECT * FROM apiregistry.dynamic_queries WHERE query_name = ?";
		    preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, queryName);
		    resultSet = preparedStatement.executeQuery();
			// Populate the query configuration
			queryConfig = new QueryConfig();
			while (resultSet.next()) {

				queryConfig.setSystemName(resultSet.getString("db_system"));
				queryConfig.setSchemaName(resultSet.getString("schema_name"));
			
				queryConfig.setPropertiesFile(queryConfigPath + resultSet.getString("propertiesFile"));

			}
			System.out.println("queryConfig obj" + queryConfig.getPropertiesFile());
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally {
			// Close the database resources
			resultSet.close();
			preparedStatement.close();
			connection.close();
		}
		return queryConfig;
	}

}
