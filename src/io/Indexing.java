package io;

import java.io.*;
import java.util.*;
import java.lang.StringBuilder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Indexing <V extends Number> implements Serializable {
	
	Map<Integer[], V> indices;
	Map<Integer, String> lexicon;
	BufferedReader reader;
	BufferedWriter writer;
	
	public Indexing(Map<Integer[], V> indices, Map<Integer, String> lexicon) {
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public Indexing(Map<String, V> data) {
		this.createIndices(data);
	}
	
	public Indexing(String IN_PATH, boolean zipped) {
		this.load(IN_PATH, zipped);
	}
	
	public Indexing() {}
	
	public void createIndices(Map<String, V> data) {
		Map<Integer[], V> indices = new HashMap<>();
		Map<Integer, String> lexicon = new HashMap<>();
		Map<String, Integer> reversed_lexicon = new HashMap<>();
		data = sortByValues(data);
		
		String sample_key = data.keySet().iterator().next();
		int n = sample_key.split(" ").length;
		
		int index = 0;
		for (String key : data.keySet()) {
			key = key.trim();
			Integer[] token_indices = new Integer[n];
			if (n > 1) {
				String[] key_parts = key.split(" ");
				int ti_index = 0;
				for (String token : key_parts) {
					if(!(reversed_lexicon.keySet().contains(token))) {
						lexicon.put(index, token);
						reversed_lexicon.put(token, index);
						token_indices[ti_index] = index;
						index++;
					}
					else {
						token_indices[ti_index] = reversed_lexicon.get(token);
					}
					ti_index++;
				}
				indices.put(token_indices, data.get(key + " "));
			}
			else {
				token_indices[0] = index;;
				indices.put(token_indices, data.get(key));
				lexicon.put(index, key);
				index++;
			}
		}
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public Map<Integer[], ? extends Number> getIndices() {
		return this.indices;
	}
	
	public Map<Integer, String> getLexicon() {
		return this.lexicon;
	}
	
	public void dump(String OUTFILE_PATH, boolean zipped) {
		String ext = (zipped) ? ".gz" : ".txt";
		this.writeArrayMap(this.getIndices(), OUTFILE_PATH + "indices" + ext, zipped);
		this.writeMap(this.getLexicon(), OUTFILE_PATH + "lexicon" + ext, zipped);
	}
	
	public void load(String IN_PATH, boolean zipped) {
		String ext = (zipped) ? ".gz" : ".txt";
		this.indices = this.readIndices(IN_PATH + "indices" + ext, zipped);
		this.lexicon = this.readLexicon(IN_PATH + "lexicon" + ext, zipped);
	}
	
	private Map<Integer[], V> readIndices(String INFILE_PATH, boolean zipped) {
		Map<Integer[], V> indices = new HashMap<>();
		try {
			BufferedReader reader;
			if (zipped) {
				GZIPInputStream gis = new GZIPInputStream(new FileInputStream(INFILE_PATH));
				reader = new BufferedReader(new InputStreamReader(gis));
			}
			else {
				reader = new BufferedReader(new FileReader(INFILE_PATH));
			}
			try {
				String current_line = reader.readLine().trim();
				while (current_line != "") {
					String[] line_parts = current_line.split("\t");
					String[] string_key_indices = line_parts[0].split(" ");
					Integer[] key_indices = new Integer[string_key_indices.length];
					for (int i = 0; i < string_key_indices.length; i++) {
						key_indices[i] = Integer.parseInt(string_key_indices[i]);
					}
					try {
						indices.put(key_indices, this.intCast(Integer.parseInt(line_parts[1])));
					}
					catch (NumberFormatException nfe) {
						indices.put(key_indices, this.doubleCast(Double.parseDouble(line_parts[1])));
					}
					current_line = reader.readLine().trim();
				}
			}
			catch (NullPointerException npe) { }
		}
		catch (IOException ioe) { ioe.printStackTrace(); }
		return indices;
	}
	
	private Map<Integer, String> readLexicon(String INFILE_PATH, boolean zipped) {
		Map<Integer, String> lexicon = new HashMap<>();
		try {
			BufferedReader reader;
			if (zipped) {
				GZIPInputStream gis = new GZIPInputStream(new FileInputStream(INFILE_PATH));
				reader = new BufferedReader(new InputStreamReader(gis));
			}
			else {
				reader = new BufferedReader(new FileReader(INFILE_PATH));
			}
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
	
	public <K, V> void writeMap(Map<K, V> data, String OUTFILE_PATH, boolean zipped) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			}
			else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (K key : data.keySet()) {
				if (zipped) {
					writer.append(key + "\t" + data.get(key) + "\n");
				}
				else {
					writer.write(key + "\t" + data.get(key) + "\n");
				}
			}
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public <K, V> void writeArrayMap(Map<K[], V> data, String OUTFILE_PATH, boolean zipped) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			}
			else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (K[] key : data.keySet()) {
				if (zipped) {
					writer.append(this.njoin(" ", key) + "\t" + data.get(key) + "\n");
				}
				else {
					writer.write(this.njoin(" ", key) + "\t" + data.get(key) + "\n");
				}
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
	
	public void validateState() throws IllegalArgumentException {
		this.validateIndices();
		this.validateLexicon();
	}
	
	private void validateLexicon() {
		Set<Integer> keys = this.lexicon.keySet();
		if (!(keys.size() > 0)) {
			throw new IllegalArgumentException("The Lexicon is empty.");
		}
		for (int key : keys) {
			String value = this.lexicon.get(key);
			if (key < 0) {
				throw new IllegalArgumentException("Invalid Key: " + key);
			}
			else if (!(value.length() > 0) || value == null) {
				throw new IllegalArgumentException("Invalid Value: " + value);
			}
		}
	}
	
	private void validateIndices() {
		Set<Integer[]> keys = this.indices.keySet();
		if (!(keys.size() > 0)) {
			throw new IllegalArgumentException("The Lexicon is empty.");
		}
		for (Integer[] key : keys) {
			V value = this.indices.get(key);
			for (int index : key) {
				if (index < 0) {
					throw new IllegalArgumentException("Invalid Index: " + index);
				}
			}
			if (value.doubleValue() <= 0) {
				throw new IllegalArgumentException("Invalid Value: " + value);
			}
		}
	}
	
	private V doubleCast(Double d)  {
		return (V) d;
	}
	
	private V intCast(Integer i) {
		return (V) i;
	}
	
	protected <T> String njoin(String delimiter, T[] a) {
		StringBuilder sb = new StringBuilder();
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++) {
			sb.append(delimiter + a[i]);
		}
		return sb.toString();
	}
	
	private <T> String pA(T[] a) {
		StringBuilder sb = new StringBuilder("[");
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++) {
			sb.append(", " + a[i].toString());
		}
		sb.append("]");
		return sb.toString();
	}
	
	public <K, V> void pAH(Map<K[], V> map) {
		for (K[] key : map.keySet()) {
			System.out.println(this.pA(key) + " : " + map.get(key));
		}
	}
}
