package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BinaryIndexing extends Indexing {
	
	String prefix = "bin_";
	
	public BinaryIndexing(Map<String, Integer> data) {
		this.createIndices(data);
	}
	
	public BinaryIndexing(String IN_PATH, boolean zipped) {
		super(IN_PATH, zipped);
	}
	
	public BinaryIndexing() {
		super();
	}

	public void dump(String OUTFILE_PATH, boolean zipped) {
		String ext = (zipped) ? ".gz" : ".txt";
		this.writeBinaryIndicesMap(this.getIndices(), OUTFILE_PATH + prefix + "indices" + ext, zipped);
		this.writeBinaryLexiconMap(this.getLexicon(), OUTFILE_PATH + prefix + "lexicon" + ext, zipped);
	}
	
	public void load(String IN_PATH, boolean zipped) {
		String ext = (zipped) ? ".gz" : ".txt";
		this.indices = this.readBinaryIndices(IN_PATH + prefix + "indices" + ext, zipped);
		this.lexicon = this.readBinaryLexicon(IN_PATH + prefix + "lexicon" + ext, zipped);
	}
	
	public void writeBinaryIndicesMap(Map<Integer[], Integer> indices, String OUTFILE_PATH, boolean zipped) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			}
			else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (Integer[] key : indices.keySet()) {
				String[] binary_key = new String[key.length];
				for (int i = 0; i < key.length; i++) {
					binary_key[i] = Integer.toBinaryString(key[i]);
				}
				if (zipped) {
					writer.append(this.njoin(" ", binary_key) + "\t" + Integer.toBinaryString(indices.get(key)) + "\n");
				}
				else {
					writer.write(this.njoin(" ", binary_key) + "\t" + Integer.toBinaryString(indices.get(key)) + "\n");
				}
			}
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public void writeBinaryLexiconMap(Map<Integer, String> lexicon, String OUTFILE_PATH, boolean zipped) {
		try {
			BufferedWriter writer;
			if (zipped) {
				GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(OUTFILE_PATH));
				writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			}
			else {
				writer = new BufferedWriter(new FileWriter(OUTFILE_PATH));
			}
			for (int key : lexicon.keySet()) {
				if (zipped) {
					writer.append(Integer.toBinaryString(key) + "\t" + lexicon.get(key) + "\n");
				}
				else {
					writer.write(Integer.toBinaryString(key) + "\t" + lexicon.get(key) + "\n");
				}
			}
			writer.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	private Map<Integer[], Integer> readBinaryIndices(String INFILE_PATH, boolean zipped) {
		Map<Integer[], Integer> indices = new HashMap<>();
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
					String[] string_key_indices = line_parts[0].split(" ");
					Integer[] key_indices = new Integer[string_key_indices.length];
					for (int i = 0; i < string_key_indices.length; i++) {
						key_indices[i] = Integer.parseInt(string_key_indices[i], 2);
					}
					indices.put(key_indices, Integer.parseInt(line_parts[1], 2));
					current_line = reader.readLine();
				}
			}
			catch (NullPointerException npe) {}
		}
		catch (IOException ioe) {}
		return indices;
	}
	
	private Map<Integer, String> readBinaryLexicon(String INFILE_PATH, boolean zipped) {
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
					lexicon.put(Integer.parseInt(line_parts[0], 2), line_parts[1]);
					current_line = reader.readLine();
				}
			}
			catch (NullPointerException npe) {}
		}
		catch (IOException ioe) {}
		return lexicon;
	}

}
