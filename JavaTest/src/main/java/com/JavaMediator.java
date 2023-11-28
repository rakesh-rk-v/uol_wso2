package com;

import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

public class JavaMediator extends AbstractMediator { 

	public boolean mediate(MessageContext context) { 
		//String name = "Rakesh";
		int a = 29;
		float r = 6.9f;
		double d = 9.0;
		return true;
	}
}
