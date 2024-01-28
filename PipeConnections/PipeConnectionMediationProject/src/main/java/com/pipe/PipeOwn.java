package com.pipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PipeOwn {
	private static FileReader file;
	private static BufferedReader bufferedReader;
	private static Map <String, Map<String,String>> rootMap = new LinkedHashMap<>();
	// private static List<String> keyList = new ArrayList<>();
	public static void main(String[] args) {
		
		String fileName = "E:\\TestFiles\\Task\\Pipe\\HDPipes.txt"; 
		try {
			 file= new FileReader(fileName);
			bufferedReader = new BufferedReader(file);
			String line;
			while((line = bufferedReader.readLine()) != null) {
				if(line.equals("[START]")) {
					startNewSession(bufferedReader);
				}
				else if (line.equals("[END]")) {
					endCurrentSession();
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	private static void endCurrentSession() {
		
		
	}
	public static void startNewSession(BufferedReader bufferedReader) {
		String line;
		List<String> keyList = new ArrayList<>() ;
		List<String> valueList = new ArrayList<>() ;
		try {
			while((line = bufferedReader.readLine()) != null) {
				
				if(line.startsWith("H:[")) {
					 keyList = new ArrayList<>();
					String [] keys= line.split("\\|");
					for(String key: keys) {
						if(key.startsWith("H:[")) {
							String currentRootHeader = key.substring(key.indexOf("[")+1,key.indexOf("]"));
							System.out.println("Root Header ====>"+currentRootHeader);
							Map.of(currentRootHeader,new LinkedHashMap<>());
							rootMap.put(currentRootHeader, new LinkedHashMap<String,String>());
						
							continue;
						}
						if(key.equals("[args]")) {
							continue;
						}
						
						keyList.add(key);
						
					}
					System.out.println("Key List = "+keyList.toString());
				}
				if (line.startsWith("D:")) {
					line = line.replace("D:", "");
					valueList = new ArrayList<>();
				//	System.out.println("Values ");
					String [] values = line.split("\\|");
					for(String value: values) {
						if(value.equals("D:")) {
							continue;
						}
						valueList.add(value);
					}
					System.out.println("Value List ===> "+valueList);
					
				}
				
				
			}
			
			System.out.println("=============================================");
			
			
			
		} catch (IOException e) {
	
			e.printStackTrace();
		}
	}
	public static void keyValuePairs() {
		
	}

}
