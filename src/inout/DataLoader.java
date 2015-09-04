package inout;

import java.util.*;
import java.io.*;

public class DataLoader {
	
	private String INFILE_PATH;
	BufferedReader reader;
	BufferedWriter writer;

	public DataLoader(String file_path) {
		this.INFILE_PATH = file_path;
	}
	
	public DataLoader() {}
	
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
	
	public void dumpIndexing(Indexing indexing, String OUT_PATH, boolean validateState) {
		String filename = "";
		if (indexing instanceof BinaryIndexing) {
			filename = "bin_index.ser";
		}
		else if (indexing instanceof HexadecimalIndexing) {
			filename = "hex_index.ser";
		}
		else if (indexing instanceof Indexing) {
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
		}
		catch (Exception e) { e.printStackTrace(); }
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
		}
		catch(Exception e) {e.printStackTrace(); }
		return indexing;
	}
	
}
