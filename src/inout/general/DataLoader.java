package inout.general;

import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;

import java.util.*;
import java.io.*;

public class DataLoader {
	
	private String INFILE_PATH;
	private IO reader;
	private IO writer;

	public DataLoader(String file_path) {
		this.INFILE_PATH = file_path;
	}
	
	public DataLoader() {}
	
	public Map<String, Integer> readFrequencies() {
		Map<String, Integer> freqs = new HashMap<>();
		
		try {
		this.reader = new IO(this.getInPath(), "out");
		String current_line = this.reader.next();
			do {
				String[] line_parts = current_line.trim().split("\t");
				freqs.put(current_line.replace(line_parts[line_parts.length-1], "").replace("\n", "").replace("\t", ""),
						Integer.parseInt(line_parts[line_parts.length-1]));
				current_line = this.reader.next();
			} while (this.reader.hasNext());
		}  catch (NullPointerException | IOModeException | IOException e) {
			e.printStackTrace();
		}
		return freqs;
	}
	
	public void dumpIndexing(Indexing indexing, String OUT_PATH, boolean validateState) {
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
	
	public Indexing loadIndexing(String IN_PATH, boolean validateState) {
		Indexing indexing = null;
		try {
			FileInputStream fis = new FileInputStream(IN_PATH);
			ObjectInputStream ois = new ObjectInputStream(fis);
			indexing = (Indexing) ois.readObject();
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
	
	public String getInPath() {
		return this.INFILE_PATH;
	}
	
}
