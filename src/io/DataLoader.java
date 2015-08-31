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
			while (current_line != "") {
				String[] line_parts = current_line.split("\t");
				freqs.put(line_parts[0], Integer.parseInt(line_parts[1]));
				current_line = reader.readLine().trim();
			}
		}
		catch (Exception e) { e.printStackTrace(); }	
		return freqs;
	}
	
	public void saveData(String OUTFILE_PATH) {
		              
	}
	
	public <K, V> void writeMap(Map<K, V> data, String OUTFILE_PATH) {
		try {
			writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			for (K key : data.keySet()) {
				writer.write(key + "\t" + data.get(key) + "\n");
			}
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void writeIndexing(Indexing indexing, String OUTFILE_PATH) {
		writeMap(indexing.getIndices(), OUTFILE_PATH + "indices.txt");
		writeMap(indexing.getLexicon(), OUTFILE_PATH + "lexicon.txt");
	}
	
}
