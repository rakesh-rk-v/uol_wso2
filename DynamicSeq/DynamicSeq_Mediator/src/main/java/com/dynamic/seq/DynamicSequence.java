package com.dynamic.seq;

import java.util.Arrays;
import java.util.List;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.base.SequenceMediator;

public class DynamicSequence extends AbstractMediator { 

	public boolean mediate(MessageContext mc) { 
		List<String> sampleSequence = Arrays.asList("https://bbb12cee-ff32-4f42-b392-a70685e90533.mock.pstmn.io/view-numbers?channel=?&category=?","https://bbb12cee-ff32-4f42-b392-a70685e90533.mock.pstmn.io/$msisdn/number-lock?status=?&channel=?" );
		for(String sequences:sampleSequence ) {
		SequenceMediator sequence = (SequenceMediator) mc.getSequence("DynamicSeq");
		mc.setProperty("sequences", sequences);
		boolean ret = sequence.mediate(mc);
		String seqResponse = (String) mc.getProperty("APIResponse");
		System.out.println("Sequence Mediate True/False = "+ret);
		log.info("Sequence mediate True/False "+ret);
		if (ret) {
			String  property = (String) mc.getProperty("DynamicSeqRes");
		
			mc.setProperty("customReq", seqResponse);
			System.out.println(property);
			log.info("Sequence Property = "+ret);
		}
		}
		return true;
	}
	
}
