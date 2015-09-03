package languagemodel;

import smoothing.*;
import io.*;

import java.util.*;

public class LanguageModel {
	
	final int n;
	final String IN_PATH;
	Smoothing smoothing;
	DataLoader dl;
	private List<Map<String, Integer>> nGramFrequencies;
	private boolean validateState;
	
	public LanguageModel(int n, String IN_PATH, Smoothing smoothing, DataLoader dl, Indexing indexing, int mode) {
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.smoothing = smoothing;
		this.dl = dl;
		this.validateState = false;
		this.nGramFrequencies = this.collectFrequencies(mode, indexing);
	}
	
	public List<Map<String, Integer>> collectFrequencies(int mode, Indexing indexing) {
		List<Map<String, Integer>> nGramFrequencies = new ArrayList<>();
		for(int i = 1; i <= this.n; i++) {
			nGramFrequencies.add(this.reconstructFrequencies(i, mode, indexing));
		}
		return nGramFrequencies;
	}
	
	private Map<String, Integer> reconstructFrequencies(int n, int mode, Indexing indexing) {
		Map<String, Integer> freqs = new HashMap<>();
		String INDEXING_PATH = "";
		
		switch (mode) {
			case (0):
				// Unzipped
				INDEXING_PATH = this.IN_PATH + "indices/" + n + "/";
				if (indexing instanceof BinaryIndexing) {
					indexing = new BinaryIndexing(INDEXING_PATH, false);
				}
				else if (indexing instanceof HexadecimalIndexing) {
					indexing = new HexadecimalIndexing(INDEXING_PATH, false);
				}
				else if (indexing instanceof Indexing) {
					indexing = new Indexing(INDEXING_PATH, false);
				}
					new BinaryIndexing(INDEXING_PATH, false);
				break;
			case (1):
				// Zipped
				INDEXING_PATH = this.IN_PATH + "indices/" + n + "/";
				if (indexing instanceof BinaryIndexing) {
					indexing = new BinaryIndexing(INDEXING_PATH, true);
				}
				else if (indexing instanceof HexadecimalIndexing) {
					indexing = new HexadecimalIndexing(INDEXING_PATH, true);
				}
				else if (indexing instanceof Indexing) {
					indexing = new Indexing(INDEXING_PATH, true);
				}
				indexing = new Indexing(INDEXING_PATH, true);
				break;
			case (2):
				// Serialized
				INDEXING_PATH = this.IN_PATH + "indices/" + n + "/";
				DataLoader dl = new DataLoader();
				if (indexing instanceof BinaryIndexing) {
					INDEXING_PATH += "bin_index.ser";
					indexing = dl.loadIndexing(INDEXING_PATH, true);
				}
				else if (indexing instanceof HexadecimalIndexing) {
					INDEXING_PATH += "hex_index.ser";
					indexing = dl.loadIndexing(INDEXING_PATH, true);	
				}
				else if (indexing instanceof Indexing) {
					INDEXING_PATH += "index.ser";
					indexing = dl.loadIndexing(INDEXING_PATH, true);
				}
				break;
		}
		
		// Retrieving indices and lexicon
		Map<Integer[], Integer> indexing_indices = indexing.getIndices();
		Map<Integer, String> indexing_lexicon = indexing.getLexicon(); 
		
		// Reconstructing frequencies
		for (Integer[] key : indexing_indices.keySet()) {
			String[] tokens = new String[key.length];
			for (int i = 0; i < tokens.length; i++) {
				tokens[i] = indexing_lexicon.get(key[i]);
			}
			freqs.put(this.njoin(" ", tokens), indexing_indices.get(key));
		}
		return freqs;
	}
	
	public void setValidateState(boolean validateState) {
		this.validateState = validateState;
	}
	
	protected <T> String njoin(String delimiter, T[] a) {
		StringBuilder sb = new StringBuilder();
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++) {
			sb.append(delimiter + a[i]);
		}
		return sb.toString();
	}
	
	public List<Map<String, Integer>> getNGramFrequencies() {
		return this.nGramFrequencies;
	}
	
	
}
