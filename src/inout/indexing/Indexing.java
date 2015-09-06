package inout.indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.StringBuilder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import custom_exceptions.IncompleteLexiconException;

public class Indexing <V extends Number> implements Serializable {
	
	Map<Integer[], V> indices;
	Map<Integer, String> lexicon;
	Map<String, Integer> reversed_lexicon;
	
	BufferedReader reader;
	BufferedWriter writer;
	
	String IN_PATH;
	String mode;
	String prefix = "";
	
	boolean create_lexicons;
	int n;
	
	// ------------------------------------------------- Constructor -------------------------------------------------
	
	public Indexing(Map<String, V> data, String IN_PATH) {
		try {
			create_lexicons = true;
			this.IN_PATH = IN_PATH;
			this.setMode();
			this.createIndices(data, this.getMode());
		}
		catch (IncompleteLexiconException ile) {
			ile.printStackTrace();
		}
	}
	
	public Indexing(String IN_PATH, boolean zipped) {
		this.setMode();
		this.load(IN_PATH, zipped);
		// Take sample to determine n
		Integer[] sample_key = this.getIndices().keySet().iterator().next();
		this.n = sample_key.length;
	}
	
	public Indexing() {}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	public void createIndices(Map<String, V> data, String mode) throws IncompleteLexiconException {
		Map<Integer[], V> indices = new HashMap<>();
		Map<Integer, String> lexicon = new HashMap<>();
		Map<String, Integer> reversed_lexicon = new HashMap<>();
		data = sortByValues(data);
		
		String lexicon_path = this.IN_PATH + "lexicons/";
		
		// Determine whether there is a pre-existing lexicon AND reversed lexicon of same format
		try {
			lexicon = this.readLexicon(lexicon_path, true, mode);
			reversed_lexicon = this.readReversedLexicon(lexicon_path, true, mode);
			this.create_lexicons = false;
		}
		catch (FileNotFoundException fnfe) {
			try {
				lexicon = this.readLexicon(lexicon_path, false, mode);
				reversed_lexicon = this.readReversedLexicon(lexicon_path, false, mode);
				this.create_lexicons = false;
			}
			catch (FileNotFoundException fnfe2) {}
		}
				
		// Take sample to determine n
		String sample_key = data.keySet().iterator().next();
		this.n = sample_key.split(" ").length;
		
		int index = 0;
		for (String key : data.keySet()) {
			key = key.trim();
			Integer[] token_indices = new Integer[n];
			if (n > 1) {
				String[] key_parts = key.split(" ");
				int ti_index = 0;
				for (String token : key_parts) {
					if(!(reversed_lexicon.keySet().contains(token)) && create_lexicons) {
						lexicon.put(index, token);
						reversed_lexicon.put(token, index);
						token_indices[ti_index] = index;
						index++;
					}
					else if (!(reversed_lexicon.keySet().contains(token) && create_lexicons)) {
						throw new IncompleteLexiconException();
					}
					else {
						token_indices[ti_index] = reversed_lexicon.get(token);
					}
					ti_index++;
				}
				indices.put(token_indices, data.get(key + " "));
			}
			else {
				token_indices[0] = index;
				indices.put(token_indices, data.get(key));
				lexicon.put(index, key);
				index++;
			}
		}
		this.indices = indices;
		this.lexicon = lexicon;
		this.reversed_lexicon = reversed_lexicon;
	}
	
	public void dump(String OUTFILE_PATH, boolean zipped) {
		String ext = (zipped) ? ".gz" : ".txt";
		this.writeIndices(this.getIndices(), OUTFILE_PATH + "/" + this.n + "/" + prefix + "indices" + ext, zipped, this.getMode());
		if (this.createLexicons()) {
			this.writeLexicon(this.getLexicon(), OUTFILE_PATH + "lexicons/" + prefix + "lexicon" + ext, zipped, this.getMode());
			this.writeReversedLexicon(this.getReversedLexicon(), OUTFILE_PATH + "lexicons/" + prefix + "lexicon_reversed" + ext, zipped, this.getMode());
		}
	}
	
	public void load(String IN_PATH, boolean zipped) {
		String ext = (zipped) ? ".gz" : ".txt";
		try {
			this.indices = this.readIndices(IN_PATH + prefix + "indices" + ext, zipped, this.getMode());
			this.lexicon = this.readLexicon(IN_PATH + "lexicons/" + prefix + "lexicon" + ext, zipped, this.getMode());
			this.reversed_lexicon = this.readReversedLexicon(IN_PATH + "lexicons/" + prefix + "lexicon_reversed" + ext, zipped, this.getMode());
		}
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
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
	
	// ----------------------------------------------- Reading & Writing ---------------------------------------------
	
	protected Map<Integer[], V> readIndices(String INFILE_PATH, boolean zipped, String mode) throws FileNotFoundException{
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
					if (mode == "binary") {
						for (int i = 0; i < string_key_indices.length; i++) {
							key_indices[i] = Integer.parseInt(string_key_indices[i], 2);
						}
						indices.put(key_indices, this.intCast(Integer.parseInt(line_parts[1], 2)));
					}
					else if (mode == "hexadecimal") {
						for (int i = 0; i < string_key_indices.length; i++) {
							key_indices[i] = Integer.parseInt(string_key_indices[i], 16);
						}
						indices.put(key_indices, this.intCast(Integer.parseInt(line_parts[1], 16)));
					}
					else if (mode == "default") {
						for (int i = 0; i < string_key_indices.length; i++) {
							key_indices[i] = Integer.parseInt(string_key_indices[i]);
						}
						try {
							indices.put(key_indices, this.intCast(Integer.parseInt(line_parts[1])));
						}
						catch (NumberFormatException nfe) {
							indices.put(key_indices, this.doubleCast(Double.parseDouble(line_parts[1])));
						}
					}
					current_line = reader.readLine().trim();
				}
			}
			catch (NullPointerException npe) { }
		}
		catch (IOException ioe) { ioe.printStackTrace(); }
		return indices;
	}
	
	protected Map<Integer, String> readLexicon(String INFILE_PATH, boolean zipped, String mode) throws FileNotFoundException {
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
					switch (mode) {
						case ("binary"):
							lexicon.put(Integer.parseInt(line_parts[0], 2), line_parts[1]);
							break;
						case ("hexadecimal"):
							lexicon.put(Integer.parseInt(line_parts[0], 16), line_parts[1]);
							break;
						default:
							lexicon.put(Integer.parseInt(line_parts[0]), line_parts[1]);
							break;
					}
					current_line = reader.readLine();
				}
			}
			catch (NullPointerException npe) {}
		}
		catch (IOException ioe) {}
		return lexicon;
	}
	
	protected Map<String, Integer> readReversedLexicon(String INFILE_PATH, boolean zipped, String mode) throws FileNotFoundException {
		Map<String, Integer> reversed_lexicon = new HashMap<>();
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
					switch (mode) {
					case ("binary"):
						reversed_lexicon.put(line_parts[0], Integer.parseInt(line_parts[1], 2));
						break;
					case ("hexadecimal"):
						reversed_lexicon.put(line_parts[0], Integer.parseInt(line_parts[1], 16));
						break;
					default:
						reversed_lexicon.put(line_parts[0], Integer.parseInt(line_parts[1]));
						break;
				}
					current_line = reader.readLine();
				}
			}
			catch (NullPointerException npe) {}
		}
		catch (IOException ioe) {}
		return reversed_lexicon;
	}
	
	protected <V> void writeIndices(Map<Integer[], V> data, String OUTFILE_PATH, boolean zipped, String mode) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			}
			else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (Integer[] key : data.keySet()) {
				String[] new_key = new String[key.length];
				switch (mode) {
					case ("binary"):
						String[] binary_key = new String[key.length];
						for (int i = 0; i < key.length; i++) {
							binary_key[i] = Integer.toBinaryString(key[i]);
						}
						new_key = binary_key;
						break;
					case ("hexadecimal"):
						String[] hexadecimal_key = new String[key.length];
						for (int i = 0; i < key.length; i++) {
							hexadecimal_key[i] = Integer.toHexString(key[i]);
						}
						new_key = hexadecimal_key;
						break;
					default:
						for (int i = 0; i < key.length; i++) {
							new_key[i] = "" + key[i];
						}
				}
				if (zipped) {
					writer.append(this.njoin(" ", new_key) + "\t" + data.get(key) + "\n");
				}
				else {
					writer.write(this.njoin(" ", new_key) + "\t" + data.get(key) + "\n");
				}
			}
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	protected void writeLexicon(Map<Integer, String> data, String OUTFILE_PATH, boolean zipped, String mode) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			}
			else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (Integer key : data.keySet()) {
				String line = "";
				switch(mode) {
					case ("binary"):
						line = Integer.toBinaryString(key) + "\t" + lexicon.get(key) + "\n";
						break;
					case ("hexadecimal"):
						line = Integer.toHexString(key) + "\t" + lexicon.get(key) + "\n";
						break;
					default:
						line = key + "\t" + data.get(key) + "\n";
						break;
				}
				if (zipped) {
					writer.append(line);
				}
				else {
					writer.write(line);
				}
			}
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	protected void writeReversedLexicon(Map<String, Integer> data, String OUTFILE_PATH, boolean zipped, String mode) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			}
			else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (String key : data.keySet()) {
				String line = "";
				switch(mode) {
					case ("binary"):
						line = key + "\t" + Integer.toBinaryString(data.get(key)) + "\n";
						break;
					case ("hexadecimal"):
						line = key + "\t" + Integer.toHexString(data.get(key)) + "\n";
						break;
					default:
						line = key + "\t" + data.get(key) + "\n";
						break;
				}
				if (zipped) {
					writer.append(line);
				}
				else {
					writer.write(line);
				}
			}
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	public Map<Integer[], ? extends Number> getIndices() {
		return this.indices;
	}
	
	public Map<Integer, String> getLexicon() {
		return this.lexicon;
	}
	
	public Map<String, Integer> getReversedLexicon() {
		return this.reversed_lexicon;
	}
	
	public boolean createLexicons() {
		return this.create_lexicons;
	}
	
	public int getN() {
		return this.n;
	}
	
	public String getMode() {
		return this.mode;
	}
	
	private void setMode() {
		this.mode = "default";
	}
	
	// ------------------------------------------------ Other methods- -----------------------------------------------
	
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
	
	protected <T> String njoin(String delimiter, T[] a) {
		StringBuilder sb = new StringBuilder();
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++) {
			sb.append(delimiter + a[i]);
		}
		return sb.toString();
	}
	
	private V intCast(Integer i) {
		return (V) i;
	}
	
	private V doubleCast(Double d)  {
		return (V) d;
	}	
}
