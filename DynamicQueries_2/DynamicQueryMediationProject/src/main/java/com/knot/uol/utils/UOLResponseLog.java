package com.knot.uol.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class UOLResponseLog extends AbstractMediator {
	
	    private String source_req_id;
	    private String child_req_id;
	    private String response;
	    private String status;

	public String getSource_req_id() {
			return source_req_id;
		}

		public void setSource_req_id(String source_req_id) {
			this.source_req_id = source_req_id;
		}

		public String getChild_req_id() {
			return child_req_id;
		}

		public void setChild_req_id(String child_req_id) {
			this.child_req_id = child_req_id;
		}

		public String getResponse() {
			return response;
		}

		public void setResponse(String response) {
			this.response = response;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	@Override
	public boolean mediate(MessageContext arg0) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		
		try {
			
			Connection con = DriverManager.getConnection("jdbc:mysql://172.16.110.240:3306/uol_api_registry","root","Adm!n@123");
			log.info("Connection Obj = " +con);
			String updateQuery = "UPDATE `uol_api_registry`.`api_log_handler` "
                    + "SET `response` = ?, `status` = ?  WHERE `source_req_id` = ? AND `child_req_id` = ?";
			PreparedStatement pstmt = con.prepareStatement(updateQuery);
			pstmt.setString(1,response);
			pstmt.setString(2,status);
			pstmt.setString(3,source_req_id);
			pstmt.setString(4,child_req_id);
			
			
			boolean execute = pstmt.execute();
			if(!execute) {
				log.error(new String("The Insert Statement Not Executed Properly"));
				return false;
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return false;
	}
}
