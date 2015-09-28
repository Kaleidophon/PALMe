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

import inout.indexing.BiMapLexicon;
import utilities.Toolbox;

/**
 * Indexing is a class to store n-gram frequencies and probabilites in a more efficient manner.
 * <p>
 * Normally, it would be stored in a format like
 * <word 1> <word 2> ... <word n>	<value>
 * <p>
 * However, this makes the amount of disk space needed even bigger.
 * Therefore, each word is given a unique ID instead. The ID can then be looked up in a lexicon.
 * 
 * @author Dennis Ulmer
 */
public class Indexing <V extends Number> implements Serializable {
	
	private Map<List<Integer>, V> indices;
	private Lexicon lexicon;
	
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private String FREQS_IN_PATH;
	private String LEX_IN_PATH;
	protected String mode;
	protected String prefix;
	
	private boolean create_lexicons; // Can a lexicon be found or does it have to be created during the creation of the indices?
	private int n;
	
	// ------------------------------------------------- Constructor -------------------------------------------------
	
	/**
	 * Default constructor: Load (frequency) data, create indices and save them.
	 * 
	 * @param data Data in form of {@code Map<Integer, Integer>} or {@code Map<Integer, Double>}
	 * @param FREQS_IN_PATH Path to (frequency) data
	 * @param LEX_IN_PATH Path to lexicon
	 */
	public Indexing(Map<String, V> data, String FREQS_IN_PATH, String LEX_IN_PATH) {
		create_lexicons = true;
		this.FREQS_IN_PATH = FREQS_IN_PATH;
		this.LEX_IN_PATH = LEX_IN_PATH;
		this.setMode();
		this.setPrefix();
		try {
			this.createIndices(data);
		} catch (IncompleteLexiconException ile) {
			ile.printStackTrace();
		}
	}
	
	/**
	 * Constructor to load an Indexing from already computed data.
	 * 
	 * @param FREQS_IN_PATH Path to data
	 * @param LEX_IN_PATH Path to lexicon
	 * @param zipped Is the data in an archive?
	 */
	public Indexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped) {
		this(FREQS_IN_PATH, LEX_IN_PATH);
		this.setMode();
		this.setPrefix();
		this.load(FREQS_IN_PATH, LEX_IN_PATH, zipped);
	}
	
	/**
	 * Constructor to load an Indexing from already computed data in a parallelized manner.
	 * 
	 * @param FREQS_IN_PATH Path to data
	 * @param LEX_IN_PATH Path to lexicon
	 * @param zipped Is the data in an archive?
	 * @param threads Number of threads to load data
	 */
	public Indexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped, int threads) {
		this(FREQS_IN_PATH, LEX_IN_PATH);
		this.setMode();
		this.setPrefix();
		this.loadParallelized(FREQS_IN_PATH, LEX_IN_PATH, zipped, threads);
	}
	
	/** Constructor to dump already indexed data */
	public Indexing(Map<List<Integer>, V> indexed_data, String FREQS_IN_PATH, boolean zipped) {
		this(indexed_data, FREQS_IN_PATH);
		this.setMode();
		this.setPrefix();
		this.dump(this.FREQS_IN_PATH, zipped);
	}
	
	/** Dummy constructor */
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
	
	/** Constructor corresponding to the one loading; also called by subclasses */
	protected Indexing(String FREQS_IN_PATH, String LEX_IN_PATH) {
		this.LEX_IN_PATH = LEX_IN_PATH;
		this.FREQS_IN_PATH = FREQS_IN_PATH;
	}
	
	/** Constructor corresponding to the one dumpinh; also called by subclasses */
	protected Indexing(Map<List<Integer>, V> indexed_data, String FREQS_IN_PATH) {
		this.indices = indexed_data;
		this.FREQS_IN_PATH = FREQS_IN_PATH;
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/**
	 * Creates indices (= IDs) from frequencies stored in a {@code Map<String, Integer>}.
	 * 
	 * @param data Frequency data.
	 * @throws IncompleteLexiconException thrown if lexicon is loaded and a key lookup fails
	 */
	public void createIndices(Map<String, V> data) throws IncompleteLexiconException {
		Map<List<Integer>, V> indices = new HashMap<>();
		BiMapLexicon lexicon = null;
		// data = Toolbox.sortByValues(data);
		this.create_lexicons = true;
		
		// Determine whether there is a pre-existing lexicon AND reversed lexicon of same format
		try {
			lexicon = new BiMapLexicon(this.readLexicon(this.LEX_IN_PATH, true));
			this.create_lexicons = false;
		} catch (IOException | NullPointerException fnfe) {
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
	
	/**
	 * Dumps the data.
	 * 
	 * @param OUTFILE_PATH Path where data is to be stored.
	 * @param zipped Should data be zipped?
	 */
	public void dump(String OUTFILE_PATH, boolean zipped) {
		this.writeIndices(this.indices, OUTFILE_PATH, zipped, this.getMode());
	}
	
	/**
	 * Loads indexing data.
	 * 
	 * @param FREQS_IN_PATH Path to data
	 * @param LEX_IN_PATH Path to lexicon
	 * @param zipped Is the data in an archive?
	 */
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
	
	/**
	 * Loads indexing data with multiple threads
	 * 
	 * @param FREQS_IN_PATH Path to data
	 * @param LEX_IN_PATH Path to lexicon
	 * @param zipped Is the data in an archive?
	 * @param threads Number of threads
	 */
	public void loadParallelized(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped, int threads) {
		try {
			this.indices = this.readIndicesParallelized(FREQS_IN_PATH, zipped, this.getMode(), threads);
			this.lexicon = new BiMapLexicon(this.readLexicon(LEX_IN_PATH, zipped));
		} catch(IOException fnfe) {
			fnfe.printStackTrace();
		}
		// Take sample to determine n
		List<Integer> sample_key = this.getIndices().keySet().iterator().next();
		this.n = sample_key.size();
	}
	
	/**
	 * Checks integrity of the current indexing.
	 * 
	 * @throws IllegalArgumentException
	 */
	public void validateState() throws IllegalArgumentException {
		this.validateIndices();
		this.validateLexicon();
	}
	
	/** Checks integrity of the current lexicon. */
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
	
	/** Checks integrity of the current indices */
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
	
	/**
	 * Reads indices from an existing file.
	 * 
	 * @param INFILE_PATH Path to file
	 * @param zipped Is the data in an archive?
	 * @param mode Mode to read data (default, binary, hexadecimal)
	 * @return A {@code Map<List<Integer>, Integer} or {@code Map<List<Integer>, Double} with the data
	 * @throws FileNotFoundException
	 */
	protected Map<List<Integer>, V> readIndices(String INFILE_PATH, boolean zipped, String mode) throws FileNotFoundException {
		
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

	/**
	 * Reads indices from an existing file.
	 * 
	 * @param INFILE_PATH Path to file
	 * @param zipped Is the data in an archive?
	 * @param mode Mode to read data (default, binary, hexadecimal)
	 * @param threads Number of threads
	 * @return A {@code Map<List<Integer>, Integer} or {@code Map<List<Integer>, Double} with the data
	 * @throws FileNotFoundException
	 */
	protected Map<List<Integer>, V> readIndicesParallelized(String INFILE_PATH, boolean zipped, String mode, int threads) throws FileNotFoundException {
		Map<List<Integer>, V> indices = new HashMap<>();
		List<IndexReader<V>> index_readers = new ArrayList<>();
		try {
			BufferedReader reader;
			if (zipped) {
				GZIPInputStream gis = new GZIPInputStream(new FileInputStream(INFILE_PATH));
				reader = new BufferedReader(new InputStreamReader(gis));
			} else {
				reader = new BufferedReader(new FileReader(INFILE_PATH));
			}
			// Adding readers
			for (int i = 0; i < threads; i++) {
				index_readers.add(new IndexReader<V>(reader, mode));
			}
			for (IndexReader<V> index_reader : index_readers) {
				index_reader.join(); // Wait for completion
				if (!index_reader.isRunning()) {
					indices.putAll(index_reader.getIndices());
				}
			}	
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return indices;
	}
	
	/**
	 * Reads lexicon from an existing file. 
	 * 
	 * @param INFILE_PATH Path to file
	 * @param zipped Is the data in an archive?
	 * @return Lexicon as {@code List<String>}
	 * @throws IOException
	 */
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
	
	/**
	 * Writes indices into a file.
	 * 
	 * @param data Data to be written as a {@code Map<List<Integer>, Integer>} or {@code Map<List<Integer>, Double>}
	 * @param OUTFILE_PATH Directory where indices are to be written.
	 * @param zipped Should the file be in an archive?
	 * @param mode Mode the data is to be written in (default, binary, hexadecimal)
	 */
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
						// Convert IDs
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
					// Convert value
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
	
	/**
	 * Write lexicon into a file.
	 * 
	 * @param lexicon {@code Lexicon} to be written
	 * @param OUTFILE_PATH Directory where the lexicon is to be written.
	 * @param zipped Should the file be an archive?
	 */
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
	
	/** @return Indices as a {@code Map<List<Integer>, Integer>} or {@code Map<List<Integer>, Double>} */
	public Map<List<Integer>, ? extends Number> getIndices() {
		return this.indices;
	}
	
	/** @return Current {@code Lexicon} */
	public Lexicon getLexicon() {
		return this.lexicon;
	}
	
	/** @return Whether lexicons have to be created */
	public boolean createLexicons() {
		return this.create_lexicons;
	}
	
	/** @return Order of n-grams */
	public int getN() {
		return this.n;
	}
	
	/** @return Mode (default, binary, hexadecimal) */
	public String getMode() {
		return this.mode;
	}
	
	/** Sets mode to default for indexing writing and loading. */
	private void setMode() {
		this.mode = "default";
	}
	
	/** @return Prefix for file writing */
	public String getPrefix() {
		return this.prefix;
	}
	
	/** Sets file prefix to default for indexing writing. */
	private void setPrefix() {
		this.prefix = "";
	}	
}
