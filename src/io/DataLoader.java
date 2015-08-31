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
	
	public Indexing createIndicies(Map<String, Integer> data) {
		Map<Integer, Integer> indices = new HashMap<>();
		Map<Integer, String> lexicon = new HashMap<>();
		Map<String, Integer> data_ = sortByValues(data);
		
		int index = 0;
		for (String key : data_.keySet()) {
			indices.put(index, data.get(key));
			lexicon.put(index, key);
			index++;
		}
		return new Indexing(indices, lexicon);
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
	
	private static <K, V, E> HashMap<K, V> sortByValues(Map<K, V> map) { 
		List<E> list = new LinkedList(map.entrySet());

	    Collections.sort(list, new Comparator() {
	    	public int compare(Object o1, Object o2) {
	    		return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
	        }
	    });

	    HashMap sortedHashMap = new LinkedHashMap<>();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	    	Map.Entry entry = (Map.Entry) it.next();
	        sortedHashMap.put(entry.getKey(), entry.getValue());
	    } 
	    return sortedHashMap;
	}
}
