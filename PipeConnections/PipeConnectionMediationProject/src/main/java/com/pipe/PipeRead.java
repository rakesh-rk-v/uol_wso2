// Java package declaration for the 'com.pipe' package
package com.pipe;

// Importing necessary Java libraries
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Class definition for PipeRead
public class PipeRead {
    // Main method, starting point of the program
    public static void main(String[] args) {
        // Path to the input file
        String fileName = "E:\\TestFiles\\Task\\Pipe\\HDPipes.txt"; // Replace with the actual path to your file

        // List to store data parsed from the input file
        List<Map<String, List<Map<String, String>>>> dataList = new ArrayList<>();

        // Variables to keep track of the current section and data being processed
        Map<String, List<Map<String, String>>> currentData = null;
        List<String> sectionKeys = null;
        String currentSection = null;

        // Using try-with-resources to automatically close the BufferedReader
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            // Loop through each line in the file
            while ((line = br.readLine()) != null) {
                // Check if the line indicates the start of a new section
                if (line.startsWith("[START]")) {
                    // Initialize variables for a new section
                    currentData = new LinkedHashMap<>();
                    sectionKeys = null;
                    currentSection = null;
                } else if (line.startsWith("[END]")) {
                    // Check if the line indicates the end of a section and add data to the list
                    if (currentData != null) {
                        dataList.add(currentData);
                        currentData = null;
                        sectionKeys = null;
                        currentSection = null;
                    }
                } else if (line.startsWith("H:[")) {
                    // Check if the line contains header information for a section
                    if (currentData == null) {
                        currentData = new LinkedHashMap<>();
                    }

                    // Extract section name and keys from the line
                    currentSection = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    sectionKeys = new ArrayList<>();
                    String[] keys = line.split("\\|");
                    for (int i = 2; i < keys.length; i++) {
                        sectionKeys.add(keys[i]);
                    }

                    // Initialize a list to store section data
                    currentData.put(currentSection, new ArrayList<>());
                } else if (line.startsWith("D:")) {
                    // Check if the line contains data for a section
                    if (currentData == null || sectionKeys == null || currentSection == null) {
                        // Throw an exception if the required variables are not properly initialized
                        throw new IllegalStateException("DEBUG: currentData, sectionKeys, and currentSection should not be null here");
                    }

                    // Extract values from the line and create a map representing the section data
                    String[] values = line.substring(2).split("\\|");
                    Map<String, String> sectionData = new LinkedHashMap<>();

                    for (int i = 0; i < sectionKeys.size(); i++) {
                        String key = sectionKeys.get(i);
                        String value = (i < values.length) ? values[i] : null;

                        // Trim and handle empty values
                        if (value != null) {
                            value = value.trim();
                            if (value.isEmpty()) {
                                value = "\"\"";
                            }
                        }

                        // Put key-value pair into the section data map
                        sectionData.put(key, value);
                    }

                    // Add section data to the current section
                    currentData.get(currentSection).add(sectionData);
                }
            }
        } catch (IOException e) {
            // Handle IO exception by printing the stack trace
            e.printStackTrace();
        }

        // Constructing a JSON-like string representation of the parsed data
        StringBuilder result = new StringBuilder("[\n");
        boolean firstData = true;
        for (Map<String, List<Map<String, String>>> data : dataList) {
            if (!firstData) {
                result.append(",\n");
            }
            result.append("{\n");
            boolean firstSection = true;
            for (Map.Entry<String, List<Map<String, String>>> entry : data.entrySet()) {
                if (!firstSection) {
                    result.append(",\n");
                }
                result.append("\"").append(entry.getKey()).append("\": [");
                List<Map<String, String>> sectionDataList = entry.getValue();
                boolean firstEntry = true;
                for (Map<String, String> sectionData : sectionDataList) {
                    if (!firstEntry) {
                        result.append(",");
                    }
                    result.append("{");
                    boolean firstKeyValue = true;
                    for (Map.Entry<String, String> keyValue : sectionData.entrySet()) {
                        if (!firstKeyValue) {
                            result.append(",");
                        }
                        result.append("\"").append(keyValue.getKey()).append("\": ").append(keyValue.getValue());
                        firstKeyValue = false;
                    }
                    result.append("}");
                    firstEntry = false;
                }
                result.append("]");
                firstSection = false;
            }
            result.append("\n}");
            firstData = false;
        }
        result.append("\n]\n");

        // Print the final result
        System.out.println(result.toString());
    }
}
