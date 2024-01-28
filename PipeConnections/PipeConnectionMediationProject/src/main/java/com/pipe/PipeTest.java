package com.pipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PipeTest {

    public static void main(String[] args) {
        // Specify the file path
        String fileName = "E:\\TestFiles\\Task\\Pipe\\HDPipes.txt"; // Replace with the actual path to your file

        // List to store data in a structured format
        List<Map<String, List<Map<String, String>>>> dataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            Map<String, List<Map<String, String>>> currentData = null;
            List<String> sectionKeys = null;
            String currentSection = null;

            // Read lines from the file
            while ((line = br.readLine()) != null) {
                // Check for section start
                if (line.startsWith("[START]")) {
                    // Initialize data structures for a new section
                    currentData = new LinkedHashMap<>();
                    sectionKeys = null;
                    currentSection = null;
                } else if (line.startsWith("[END]")) {
                    // Check for section end, add data to the list
                    if (currentData != null) {
                        dataList.add(currentData);
                        currentData = null;
                        sectionKeys = null;
                        currentSection = null;
                    }
                } else if (line.startsWith("H:[")) {
                    // Handle header section, extract section name and keys
                    if (currentData == null) {
                        currentData = new LinkedHashMap<>();
                    }

                    currentSection = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    sectionKeys = new ArrayList<>();
                    String[] keys = line.split("\\|");
                    for (int i = 2; i < keys.length; i++) {
                        sectionKeys.add(keys[i]);
                    }

                    currentData.put(currentSection, new ArrayList<>());
                } else if (line.startsWith("D:")) {
                    // Handle data section, extract values and populate the map
                    if (currentData == null || sectionKeys == null || currentSection == null) {
                        throw new IllegalStateException("DEBUG: currentData, sectionKeys, and currentSection should not be null here");
                    }

                    String[] values = line.substring(2).split("\\|");
                    Map<String, String> sectionData = new LinkedHashMap<>();

                    for (int i = 0; i < sectionKeys.size(); i++) {
                        String key = sectionKeys.get(i);
                        String value = (i < values.length) ? values[i] : null;

                        if (value != null) {
                            value = value.trim();
                            if (value.isEmpty()) {
                                value = "\"\"";
                            } 
                        }

                        sectionData.put(key, value);
                    }

                    currentData.get(currentSection).add(sectionData);
                }
            }
        } catch (IOException e) {
            // Handle IO exception
            e.printStackTrace();
        }

        // Constructing a JSON-like list of key-value pairs
        List<Map<String, Object>> jsonList = new ArrayList<>();
        for (Map<String, List<Map<String, String>>> data : dataList) {
            Map<String, Object> jsonData = new LinkedHashMap<>();
            for (Map.Entry<String, List<Map<String, String>>> entry : data.entrySet()) {
                jsonData.put(entry.getKey(), entry.getValue());
            }
            jsonList.add(jsonData);
        }

        // Convert the list to a JSON string using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(jsonList);

            // Print the final JSON string
            System.out.println(jsonString);
        } catch (IOException e) {
            // Handle JSON processing exception
            e.printStackTrace();
        }
    }
}
