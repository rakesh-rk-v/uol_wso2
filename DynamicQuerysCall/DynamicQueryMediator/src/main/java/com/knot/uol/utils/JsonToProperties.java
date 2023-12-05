package com.knot.uol.utils;

import java.lang.reflect.Type;
import java.util.Map;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JsonToProperties extends AbstractMediator {

	@Override
	public boolean mediate(MessageContext mc) {
		String inputAttributes = (String) mc.getProperty("inputAttributes");
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(inputAttributes);
		return false;
	}
	public static void main(String[] args) {
		String inputAttributes = "{\"sccode\":\"1\",\"submId\":\"1\",\"plcode\":\"1001\",\"rpcode\":\"1\"}";
		Gson gson = new Gson();
		 // Define the type of the Map
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
		 // Convert JSON string to a Map
        Map<String, String> keyValueMap = gson.fromJson(inputAttributes,mapType);
     // Display the key-value pairs
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
	
	}
}
