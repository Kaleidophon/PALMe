package io;

import java.io.*;
import java.util.*;

public class Indexing <V extends Number> {
	
	Map<Integer, ?> indices;
	Map<Integer, String> lexicon;
	BufferedReader reader;
	BufferedWriter writer;
	
	public Indexing(Map<Integer, V> indices, Map<Integer, String> lexicon) {
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public Indexing(Map<String, V> data) {
		this.createIndices(data);
	}
	
	public Indexing(String IN_PATH) {
		this.load(IN_PATH);
	}
	
	public Indexing() {}
	
	public void createIndices(Map<String, V> data) {
		Map<Integer, V> indices = new HashMap<>();
		Map<Integer, String> lexicon = new HashMap<>();
		Map<String, V> data_ = sortByValues(data);
		
		int index = 0;
		for (String key : data_.keySet()) {
			indices.put(index, data.get(key));
			lexicon.put(index, key);
			index++;
		}
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public Map<Integer, ?> getIndices() {
		return this.indices;
	}
	
	public Map<Integer, String> getLexicon() {
		return this.lexicon;
	}
	
	public void dump(String OUTFILE_PATH) {
		this.writeMap(this.getIndices(), OUTFILE_PATH + "indices.txt");
		this.writeMap(this.getLexicon(), OUTFILE_PATH + "lexicon.txt");
	}
	
	public void load(String IN_PATH) {
		this.indices = this.readIndices(IN_PATH + "indices.txt");
		this.lexicon = this.readLexicon(IN_PATH + "lexicon.txt");
	}
	
	private Map<Integer, Double> readIndices(String INFILE_PATH) {
		Map<Integer, Double> indices = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(INFILE_PATH));
			try {
				String current_line = reader.readLine().trim();
				while (current_line != "") {
					String[] line_parts = current_line.trim().split("\t");
					indices.put(Integer.parseInt(line_parts[0]), Double.parseDouble(line_parts[1]));
					current_line = reader.readLine();
				}
			}
			catch (NullPointerException npe) {}
		}
		catch (IOException ioe) {}
		return indices;
	}
	
	private Map<Integer, String> readLexicon(String INFILE_PATH) {
		Map<Integer, String> lexicon = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(INFILE_PATH));
			try {
				String current_line = reader.readLine().trim();
				while (current_line != "") {
					String[] line_parts = current_line.trim().split("\t");
					lexicon.put(Integer.parseInt(line_parts[0]), line_parts[1]);
					current_line = reader.readLine();
				}
			}
			catch (NullPointerException npe) {}
		}
		catch (IOException ioe) {}
		return lexicon;
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
	
	protected static <K, V, E> HashMap<K, V> sortByValues(Map<K, V> map) { 
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
