package com.pipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PipeClass extends AbstractMediator { 

	public boolean mediate(MessageContext context) { 
		
		return true;
	}
	  public static void main(String[] args) {
	        String fileName = "C:\\Users\\KNOT\\Downloads\\Book4 1.csv";
	        List<Map<String, List<Map<String, String>>>> allSections = new ArrayList<>();
	        Map<String, List<Map<String, String>>> currentSectionData = null;
	        List<String> sectionKeys = new ArrayList<>();

	        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                // Start of a new section
	                if (line.equals("[START]")) {
	                    startNewSection(allSections, currentSectionData);
	                    currentSectionData = new LinkedHashMap<>();
	                }
	                // End of the current section
	                else if (line.equals("[END]")) {
	                    startNewSection(allSections, currentSectionData);
	                    currentSectionData = null;
	                }
	                // Section header line
	                else if (line.startsWith("H:[")) {
	                    initializeSectionData(currentSectionData, sectionKeys, line);
	                }
	                // Data line
	                else if (line.startsWith("D:")) {
	                    addDataValues(currentSectionData, sectionKeys, line);
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        // Convert the processed data to JSON
	        ObjectMapper objectMapper = new ObjectMapper();
	        try {
	            String json = objectMapper.writeValueAsString(allSections);
	            System.out.println(json);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    private static void startNewSection(List<Map<String, List<Map<String, String>>>> allSections,
	                                        Map<String, List<Map<String, String>>> currentSectionData) {
	        if (currentSectionData != null) {
	            allSections.add(new LinkedHashMap<>(Map.of(currentSectionData.keySet().iterator().next(),
	                    currentSectionData.get(currentSectionData.keySet().iterator().next()))));
	        }
	    }

	    private static void initializeSectionData(Map<String, List<Map<String, String>>> currentSectionData,
	                                              List<String> sectionKeys, String line) {
	        sectionKeys.clear();
	        String[] keys = line.split("\\|");
	        for (int i = 2; i < keys.length; i++) {
	            sectionKeys.add(keys[i]);
	        }
	        currentSectionData.put(extractSectionKey(line), new ArrayList<>());
	    }

	    private static void addDataValues(Map<String, List<Map<String, String>>> currentSectionData,
	                                      List<String> sectionKeys, String line) {
	        String[] values = line.substring(2).split("\\|");
	        Map<String, String> sectionData = new LinkedHashMap<>();

	        for (int i = 0; i < values.length; i++) {
	            sectionData.put(sectionKeys.get(i), values[i]);
	        }

	        currentSectionData.get(currentSectionData.keySet().iterator().next()).add(sectionData);
	    }

	    private static String extractSectionKey(String line) {
	        return line.substring(line.indexOf("[") + 1, line.indexOf("]"));
	    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main2(String[] args) {
        String fileName = "C:\\Users\\KNOT\\Downloads\\Book4 1.csv"; // Replace with the actual path to your file
        List<Map<String, List<Map<String, String>>>> allSections = new ArrayList<>();
        Map<String, List<Map<String, String>>> currentSectionData = null;
        List<String> sectionKeys = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("[START]")) {
                    if (currentSectionData != null) {
                        allSections.add(new LinkedHashMap<>(Map.of(currentSectionData.keySet().iterator().next(), currentSectionData.get(currentSectionData.keySet().iterator().next()))));
                    }
                    currentSectionData = new LinkedHashMap<>();
                } else if (line.equals("[END]")) {
                    if (currentSectionData != null) {
                        allSections.add(new LinkedHashMap<>(Map.of(currentSectionData.keySet().iterator().next(), currentSectionData.get(currentSectionData.keySet().iterator().next()))));
                    }
                    currentSectionData = null;
                } else if (line.startsWith("H:[")) {
                    // Extract section key from the header
                    String currentSection = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    sectionKeys.clear();
                    String[] keys = line.split("\\|");
                    for (int i = 2; i < keys.length; i++) {
                        sectionKeys.add(keys[i]);
                    }
                    currentSectionData.put(currentSection, new ArrayList<>());
                } else if (line.startsWith("D:")) {
                    // Extract values from the data line
                    String[] values = line.substring(2).split("\\|");
                    Map<String, String> sectionData = new LinkedHashMap<>(); // Use LinkedHashMap

                    for (int i = 0; i < values.length; i++) {
                        sectionData.put(sectionKeys.get(i), values[i]);
                    }

                    currentSectionData.get(currentSectionData.keySet().iterator().next()).add(sectionData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the processed data to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(allSections);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
