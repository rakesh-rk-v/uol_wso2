package com.pipe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParseInputFile {
    public static void main(String[] args) {
        String fileName = "E:\\TestFiles\\Task\\Pipe\\HDPipes.txt";
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
            ArrayNode resultArray = objectMapper.createArrayNode();
            for (Map<String, List<Map<String, String>>> section : allSections) {
                ObjectNode sectionNode = objectMapper.createObjectNode();
                for (Map.Entry<String, List<Map<String, String>>> entry : section.entrySet()) {
                    sectionNode.set(entry.getKey(), convertDataToNode(entry.getValue()));
                }
                resultArray.add(sectionNode);
            }

            System.out.println(resultArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startNewSection(List<Map<String, List<Map<String, String>>>> allSections,
                                         Map<String, List<Map<String, String>>> currentSectionData) {
        if (currentSectionData != null && !currentSectionData.isEmpty()) {
            allSections.add(new LinkedHashMap<>(currentSectionData));
        }
    }

    private static void initializeSectionData(Map<String, List<Map<String, String>>> currentSectionData,
                                               List<String> sectionKeys, String line) {
        sectionKeys.clear();
        // Extract keys from the section header line after [args]
        String[] keys = line.split("\\[args\\]\\|")[1].split("\\|");
        for (int i = 0; i < keys.length; i++) {
            sectionKeys.add(keys[i]);
        }
        // Use the first key as the root key for the section
        String rootKey = sectionKeys.get(0);
        currentSectionData.put(rootKey, new ArrayList<>());
    }

    private static void addDataValues(Map<String, List<Map<String, String>>> currentSectionData,
                                       List<String> sectionKeys, String line) {
        // Check if there is a valid section key
        String sectionKey = extractSectionKey(line);
        if (!sectionKey.isEmpty()) {
            String[] values = line.substring(2).split("\\|");
            Map<String, String> sectionData = new LinkedHashMap<>();

            for (int i = 0; i < values.length; i++) {
                sectionData.put(sectionKeys.get(i), values[i]);
            }

            // Associate the data with the root key
            currentSectionData.get(sectionKey).add(sectionData);
        }
    }

    private static String extractSectionKey(String line) {
        int startIndex = line.indexOf("[");
        int endIndex = line.indexOf("]");
        if (startIndex != -1 && endIndex != -1) {
            return line.substring(startIndex + 1, endIndex);
        } else {
            // Handle the case when [ and ] are not found
            return "";
        }
    }

    private static ArrayNode convertDataToNode(List<Map<String, String>> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        for (Map<String, String> node : data) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            for (Map.Entry<String, String> entry : node.entrySet()) {
                objectNode.put(entry.getKey(), entry.getValue());
            }
            arrayNode.add(objectNode);
        }

        return arrayNode;
    }
}
