package io;

import java.util.*;
import java.io.*;

public class DataLoader {
	
	private String INFILE_PATH;
	BufferedReader reader;
	BufferedWriter writer;

	public DataLoader(String file_path) {
		this.INFILE_PATH = file_path;
	}
	
	public Map<String, Integer> readFrequencies() {
		Map<String, Integer> freqs = new HashMap<>();
		
		try {
			reader = new BufferedReader(new FileReader(this.INFILE_PATH));
			String current_line = reader.readLine().trim();
			try {
				while (current_line != "") {
					String[] line_parts = current_line.trim().split("\t");
					freqs.put(line_parts[0], Integer.parseInt(line_parts[1]));
					current_line = reader.readLine();
				}
			} 
			catch (NullPointerException e) {}
		}
		catch (IOException e) { e.printStackTrace(); }	
		return freqs;
	}
	
}
