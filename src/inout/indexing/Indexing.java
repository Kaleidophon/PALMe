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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import inout.indexing.ArrayLexicon;
import inout.indexing.ListLexicon;
import inout.indexing.BiMapLexicon;
import utilities.Toolbox;

public class Indexing <V extends Number> implements Serializable {
	
	private Map<List<Integer>, V> indices;
	private Lexicon lexicon;
	
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private String FREQS_IN_PATH;
	private String LEX_IN_PATH;
	protected String mode;
	protected String prefix;
	
	private boolean create_lexicons;
	private int n;
	
	// ------------------------------------------------- Constructor -------------------------------------------------
	
	public Indexing(Map<String, V> data, String FREQS_IN_PATH, String LEX_IN_PATH) {
		create_lexicons = true;
		this.FREQS_IN_PATH = FREQS_IN_PATH;
		this.LEX_IN_PATH = LEX_IN_PATH;
		this.setMode();
		this.setPrefix();
		try {
			this.createIndices(data, this.getMode());
		} catch (IncompleteLexiconException ile) {
			ile.printStackTrace();
		}
	}
	
	protected Indexing(String FREQS_IN_PATH, String LEX_IN_PATH) {
		this.LEX_IN_PATH = LEX_IN_PATH;
		this.FREQS_IN_PATH = FREQS_IN_PATH;
	}
	
	protected Indexing(Map<List<Integer>, V> indexed_data, String FREQS_IN_PATH) {
		this.indices = indexed_data;
		this.FREQS_IN_PATH = FREQS_IN_PATH;
	}
	
	public Indexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped) {
		this(FREQS_IN_PATH, LEX_IN_PATH);
		this.setMode();
		this.setPrefix();
		this.load(FREQS_IN_PATH, LEX_IN_PATH, zipped);
	}
	
	public Indexing(Map<List<Integer>, V> indexed_data, String FREQS_IN_PATH, boolean zipped) {
		this(indexed_data, FREQS_IN_PATH);
		this.setMode();
		this.setPrefix();
		this.dump(this.FREQS_IN_PATH, zipped);
	}
	
	public Indexing() {
		// Making this constructor intentionally relatively useless
		this.indices = null;
		this.lexicon = null;
		this.reader = null;
		this.writer = null;
		this.FREQS_IN_PATH = null;
		this.LEX_IN_PATH = null;
		this.mode = null;
		this.prefix = null;
		this.n = 0;
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	public void createIndices(Map<String, V> data, String mode) throws IncompleteLexiconException {
		Map<List<Integer>, V> indices = new HashMap<>();
		BiMapLexicon lexicon = null;
		data = Toolbox.sortByValues(data);
		this.create_lexicons = true;
		
		// Determine whether there is a pre-existing lexicon AND reversed lexicon of same format
		try {
			lexicon = new BiMapLexicon(this.readLexicon(this.LEX_IN_PATH, true));
			this.create_lexicons = false;
		} catch (IOException |  NullPointerException fnfe) {
			try {
				lexicon = new BiMapLexicon(this.readLexicon(this.LEX_IN_PATH, false));
				this.create_lexicons = false;
			} catch (IOException | NullPointerException fnfe2) {
				lexicon = new BiMapLexicon();
			}
		}		
		// Take sample to determine n
		String sample_key = data.keySet().iterator().next();
		this.n = sample_key.split(" ").length;
		int total = data.keySet().size();
		System.out.println("Key set size: " + total);
		
		int index = 0;
		int c = 1;
		//boolean debug = false;
		Iterator<Entry<String, V>> iter = data.entrySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next().getKey().trim();
			if (c % 250000 == 0) {
				System.out.println((c + 1) * 1.0 / total * 100.0 + " % complete.");
			}
			if (key.equals("")) {
				continue;
			}
			List<Integer> token_indices = new ArrayList<Integer>(this.n);
			if (this.n > 1) {
				String[] key_parts = key.split(" ");
				for (int i = 0; i < key_parts.length; i++) {
					String token = key_parts[i];
					boolean lexiconContainsValue = lexicon.containsValue(token);
					if(!lexiconContainsValue) {
						if (this.create_lexicons) {
							// If you are currently creating lexicons and there is an unseen value, add it
							lexicon.put(index, token);
							token_indices.add(index);
							index++;
						} else {
							// If you are not creating a lexicon and there is an unseen value, the lexicon is incomplete
							throw new IncompleteLexiconException("Lexicon doesn't contain value: '" + token + "'");
						}
					} else {
						token_indices.add(lexicon.getKey(token));
					}
				}
				indices.put(token_indices, data.get(key));
			} else {
				if (this.create_lexicons) {
					token_indices.add(index);
					lexicon.put(index, key);
					index++;
				} else {
					token_indices.add(lexicon.getKey(key));
				}
				indices.put(token_indices, data.get(key));
			}
			c++;
		}
		if (this.create_lexicons) {
			String NEW_LEX_PATH = this.LEX_IN_PATH.substring(0, this.LEX_IN_PATH.lastIndexOf("/") + 1);
			this.writeLexicon(lexicon, NEW_LEX_PATH + "lexicon.txt", false);
			this.writeLexicon(lexicon, NEW_LEX_PATH + "lexicon.gz", true);
		}
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public void dump(String OUTFILE_PATH, boolean zipped) {
		String ext = (zipped) ? ".gz" : ".txt";
		this.writeIndices(this.indices, OUTFILE_PATH, zipped, this.getMode());
	}
	
	public void load(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped) {
		try {
			this.indices = this.readIndices(FREQS_IN_PATH, zipped, this.getMode());
			this.lexicon = new BiMapLexicon(this.readLexicon(LEX_IN_PATH, zipped));
		} catch(IOException fnfe) {
			fnfe.printStackTrace();
		}
		// Take sample to determine n
		List<Integer> sample_key = this.getIndices().keySet().iterator().next();
		this.n = sample_key.size();
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
			String value = this.lexicon.getValue(key);
			if (key < 0) {
				throw new IllegalArgumentException("Invalid Key: " + key);
			} else if (!(value.length() > 0) || value == null) {
				throw new IllegalArgumentException("Invalid Value: " + value);
			}
		}
	}
	
	private void validateIndices() {
		Set<List<Integer>> keys = this.indices.keySet();
		if (!(keys.size() > 0)) {
			throw new IllegalArgumentException("The Lexicon is empty.");
		}
		for (List<Integer> key : keys) {
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
	
	protected Map<List<Integer>, V> readIndices(String INFILE_PATH, boolean zipped, String mode) throws FileNotFoundException{
		Map<List<Integer>, V> indices = new HashMap<>();
		try {
			BufferedReader reader;
			if (zipped) {
				GZIPInputStream gis = new GZIPInputStream(new FileInputStream(INFILE_PATH));
				reader = new BufferedReader(new InputStreamReader(gis));
			} else {
				reader = new BufferedReader(new FileReader(INFILE_PATH));
			}
			try {
				String current_line = reader.readLine().trim();
				while (current_line != null) {
					String[] line_parts = current_line.split("\t");
					String[] string_key_indices = line_parts[0].split(" ");
					List<Integer> key_indices = new ArrayList<>();
					int base = 0;
					switch (mode) {
						case ("binary"): base = 2; break;
						case ("hexadecimal"): base = 16; break;
						case ("default"): base = 10; break;
					}
					for (int i = 0; i < string_key_indices.length; i++) {
						key_indices.add(Integer.parseInt(string_key_indices[i], base));
					}
					try {
						// Value is an integer
						indices.put(key_indices, (V) Toolbox.intCast(Integer.parseInt(line_parts[1], base)));
					} catch (NumberFormatException nfe) {
						// Value is a double
						switch (mode) {
							case ("binary"): indices.put(key_indices, (V) Toolbox.stringToDouble(line_parts[1], 2)); break;
							case ("hexadecimal"): indices.put(key_indices, (V) Toolbox.stringToDouble(line_parts[1], 16)); break;
							case ("default"): indices.put(key_indices, (V) Toolbox.doubleCast(Double.parseDouble(line_parts[1]))); break;
						}
					}
					current_line = reader.readLine().trim();
				}
			} catch (NullPointerException npe) {}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return indices;
	}
	
	protected List<String> readLexicon(String INFILE_PATH, boolean zipped) throws IOException {
		List<String> lexicon_entries = new ArrayList<>();
		BufferedReader reader;
		if (zipped) {
			GZIPInputStream gis = new GZIPInputStream(new FileInputStream(INFILE_PATH));
			reader = new BufferedReader(new InputStreamReader(gis));
		} else {
			reader = new BufferedReader(new FileReader(INFILE_PATH));
		}
		String current_line = reader.readLine().trim();
		while (current_line != null) {
			lexicon_entries.add(current_line);
			current_line = reader.readLine();
		}
		return lexicon_entries;
	}
	
	protected void writeIndices(Map<List<Integer>, V> data, String OUTFILE_PATH, boolean zipped, String mode) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			} else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (Map.Entry<List<Integer>, V> entry : data.entrySet()) {
				List<Integer> key = entry.getKey();
				String[] new_key = new String[key.size()];
				for (int i = 0; i < key.size(); i++) {
					switch (mode) {
						case ("binary"): 
							new_key[i] = Integer.toBinaryString(key.get(i)); 
							break;
						case ("hexadecimal"): 
							new_key[i] = Integer.toHexString(key.get(i));
							break;
						case ("default"):
							new_key[i] = "" + key.get(i);
							break;
					}
				}
				V value = data.get(key);
				String new_value = "";
				switch (mode) {
					case ("binary"):
						new_value = (value instanceof Double) ? Toolbox.doubleToBinary((Double) value) : Integer.toBinaryString((Integer) value);
						break;
					case ("hexadecimal"):
						new_value = (value instanceof Double) ? Toolbox.doubleToHex((Double) value) : Integer.toHexString((Integer) value);
						break;
					case ("default"):
						new_value = "" + value;
						break;
				}
				String line = Toolbox.njoin(" ", new_key) + "\t" + new_value + "\n";
				if (zipped) {
					writer.append(line);
				} else {
					writer.write(line);
				}
			}
			writer.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	protected void writeLexicon(Lexicon lexicon, String OUTFILE_PATH, boolean zipped) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			} else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (int i = 0; i < lexicon.getLexiconSize(); i++) {
				String entry = lexicon.getValue(i);
				String line = entry.trim() + "\n";
				if (zipped) {
					writer.append(line);
				} else {
					writer.write(line);
				}
			}
			writer.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
		
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	public Map<List<Integer>, ? extends Number> getIndices() {
		return this.indices;
	}
	
	public Lexicon getLexicon() {
		return this.lexicon;
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
	
	public String getPrefix() {
		return this.prefix;
	}
	
	private void setPrefix() {
		this.prefix = "";
	}
}
