package com.pipe;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
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

		// Convert the processed data to the desired JSON structure
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
			allSections.add(new LinkedHashMap<>(currentSectionData));
		}
	}

	private static void initializeSectionData(Map<String, List<Map<String, String>>> currentSectionData,
			List<String> sectionKeys, String line) {
		sectionKeys.clear();
		String[] keys = line.split("\\|");

// Start from index 3 to skip the "[args]" part
		for (int i = 3; i < keys.length; i++) {
			sectionKeys.add(keys[i]);
		}

		currentSectionData = new LinkedHashMap<>();
		currentSectionData.put(extractSectionKey(line), new ArrayList<>());
	}

	private static void addDataValues(Map<String, List<Map<String, String>>> currentSectionData,
			List<String> sectionKeys, String line) {
		String[] values = line.substring(2).split("\\|");
		Map<String, String> sectionData = new LinkedHashMap<>();
		for (int i = 0; i < values.length; i++) {
			sectionData.put(sectionKeys.get(i), values[i]);
		}
		currentSectionData.computeIfAbsent(extractSectionKey(line), k -> new ArrayList<>()).add(sectionData);
	}

	private static String extractSectionKey(String line) {
		int startIndex = line.indexOf("[") + 1;
		int endIndex = line.lastIndexOf("]");

		if (startIndex >= 0 && endIndex >= 0 && endIndex > startIndex) {
			return line.substring(startIndex, endIndex);
		} else {
			throw new IllegalArgumentException("Invalid section header line: " + line);
		}
	}

}
