package io;

import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class DataLoader {
	
	private String FILE_PATH;
	BufferedReader reader;
	BufferedWriter writer;

	public DataLoader(String file_path) {
		this.FILE_PATH = file_path;
	}
	
	public Map<String, Integer> readFrequencies() {
		Map<String, Integer> freqs = new HashMap<>();
		
		try {
			reader = new BufferedReader(new FileReader(this.FILE_PATH));
			String current_line = reader.readLine().trim();
			while (current_line != "") {
				String[] line_parts = current_line.split("\t");
				freqs.put(line_parts[0], Integer.parseInt(line_parts[1]));
				current_line = reader.readLine().trim();
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return freqs;
	}
}
