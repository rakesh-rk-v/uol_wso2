package com.knot.uol.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UOLKnotDBConnection {
	private static Connection con;
public static Connection getUOLConnection () {
	try {
		con = DriverManager.getConnection("jdbc:mysql://172.16.110.240:3306/uol_api_registry","root","Adm!n@123");
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
	return con;
	
}
}
