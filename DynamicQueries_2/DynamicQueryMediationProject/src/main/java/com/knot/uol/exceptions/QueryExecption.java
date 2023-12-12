package com.knot.uol.exceptions;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;

public class QueryExecption extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	QueryExecption qe;
	public void verifyRegistryPath(String registryPath,String queryName,MessageContext messageContext) throws Exception {
		System.out.println("Query Exception Occured");
		if (registryPath == null || registryPath.equals(null));
		String errorCode = "QUE_NM_001";
		String errorName = "QueryName Configuration Not Found in DB.";
		System.out.println("Messages are = "+errorCode+errorName+"--------Query Name = "+queryName);
		org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
		
		//org.apache.axis2.context.MessageContext axis2MessageContext = (org.apache.axis2.context.MessageContext) messageContext.getProperty("axis2messagecontext");
		System.out.println("\n MessageContext axis2MessageContext  = "+axis2MessageContext);
		HttpResponse response = (HttpResponse) axis2MessageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
		System.out.println("/n HttpResponse response  = "+axis2MessageContext);
		response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		System.out.println("Before Return Statement .....");
		System.out.println("Throwing The Exception Object");
		throw qe;
	}

	public String getMessage() {
		return "Please Enter the Query Correctly" ;
	}

}
