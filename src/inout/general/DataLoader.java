package inout.general;

import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;

import java.util.*;
import java.io.*;

/** 
 * A class to load data for indexings or load indexings or dump them.
 * 
 * @author Dennis Ulmer
 */
public class DataLoader {
	
	private String INFILE_PATH;
	private IO reader;
	private IO writer;
	
	// ------------------------------------------------- Constructor -------------------------------------------------

	/** Constructor to create an instance of a DataLoader.
	 * 
	 *  @param file_path	Path to input file as string. */
	public DataLoader(String file_path) {
		this.INFILE_PATH = file_path;
	}
	
	/** Simple constructor to create an instance of a DataLoader */
	public DataLoader() {
		this.INFILE_PATH = null;
		this.reader = null;
		this.writer = null;
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Reading word frequencies from the input file. 
	 * 
	 * @return	Returns a {@literal Map<String, Integer>}.*/
	public Map<String, Integer> readFrequencies() {
		Map<String, Integer> freqs = new HashMap<>();	
		try {
			this.reader = new IO(this.getInPath(), "out");
			String current_line = this.reader.next();
			do {
				String[] line_parts = current_line.trim().split("\t");
				String key = current_line.substring(0, current_line.indexOf("\t") + 1).trim(); // Get everything except for the frequency
				int value = Integer.parseInt(line_parts[line_parts.length-1]);
				freqs.put(key, value);
				current_line = this.reader.next();
			} while (this.reader.hasNext());
		}  catch (NullPointerException | IOModeException e) {
			e.printStackTrace();
		}
		return freqs;
	}
	
	/** @deprecated Saves an indexing to a specified path.
	 * 
	 * @param indexing	Indexing to be saved.
	 * @param OUT_PATH	Path indexing is be saved to.
	 * @param validateState	Determines whether the integrity of the indexing is checked. */
	public <V extends Number> void dumpIndexing(Indexing<V> indexing, String OUT_PATH, boolean validateState) {
		String filename = "";
		if (indexing instanceof BinaryIndexing) {
			filename = "bin_index.ser";
		} else if (indexing instanceof HexadecimalIndexing) {
			filename = "hex_index.ser";
		} else if (indexing instanceof Indexing) {
			filename = "index.ser";
		}
		OUT_PATH += filename;
		
		try {
			if (validateState) {
				indexing.validateState();
			}
			FileOutputStream fos = new FileOutputStream(OUT_PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(indexing);
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	/** @deprecated Loads a serialized indexing dumped with {@link dumpIndexing}.
	 * 
	 *  @param IN_PATH	Path to saved indexing.
	 *  @param validateState	Determines whether the integrity of the indexing is checked after loading. 
	 *  @return	Loaded indexing. */
	public <V extends Number> Indexing<V> loadIndexing(String IN_PATH, boolean validateState) {
		Indexing<V> indexing = null;
		try {
			FileInputStream fis = new FileInputStream(IN_PATH);
			ObjectInputStream ois = new ObjectInputStream(fis);
			indexing = (Indexing<V>) ois.readObject();
			ois.close();
			fis.close();
			if (validateState) {
				indexing.validateState();
			}
		} catch(Exception e) {
			e.printStackTrace(); 
		}
		return indexing;
	}
	
	/** @return Input path */
	public String getInPath() {
		return this.INFILE_PATH;
	}
}
