package com.knot.uol.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class UOLRequestLog extends AbstractMediator {
	
	  private String NIC;
	    private String MSISDN;
	    private String custid;
	    private String channel;
	    private String source_req_id;
	    private String child_req_id;
	    private String api_name;
	    private int processId;
	    private String processName;
	    private String request;
	    private String status;
	
	
	
	
	

	public String getNIC() {
			return NIC;
		}

		public void setNIC(String nIC) {
			NIC = nIC;
		}

		public String getMSISDN() {
			return MSISDN;
		}

		public void setMSISDN(String mSISDN) {
			MSISDN = mSISDN;
		}

		public String getCustid() {
			return custid;
		}

		public void setCustid(String custid) {
			this.custid = custid;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

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

		public String getApi_name() {
			return api_name;
		}

		public void setApi_name(String api_name) {
			this.api_name = api_name;
		}

		public int getProcessId() {
			return processId;
		}

		public void setProcessId(int processId) {
			this.processId = processId;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

		public String getRequest() {
			return request;
		}

		public void setRequest(String request) {
			this.request = request;
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
			String insertStatement = "INSERT INTO `uol_api_registry`.`api_log_handler` "
                    + "( `NIC`, `MSISDN`, `custid`, `channel`, `source_req_id`, `child_req_id`, "
                    + "`api_name`, `processId`, `processName`, `request`,  `status`) "
                    + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = con.prepareStatement(insertStatement);
			pstmt.setString(1, NIC);
			pstmt.setString(2, MSISDN);
			pstmt.setString(3, custid);
			pstmt.setString(4, channel);
			pstmt.setString(5, source_req_id);
			pstmt.setString(6, child_req_id);
			pstmt.setString(7, api_name);
			pstmt.setInt(8, processId);
			pstmt.setString(9, processName);
			pstmt.setString(10, request);
			pstmt.setString(11, status);
			
			boolean execute = pstmt.execute();
			if(!execute) {
				log.error(new String("The Insert Statement Not Executed Properly"));
				return false;
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return true;
	}

}
