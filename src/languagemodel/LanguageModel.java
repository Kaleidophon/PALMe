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
	
	public LanguageModel(int n, String IN_PATH, Smoothing smoothing, DataLoader dl) {
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.smoothing = smoothing;
		this.dl = dl;
		this.validateState = false;
		this.nGramFrequencies = this.collectFrequencies();
	}
	
	public List<Map<String, Integer>> collectFrequencies() {
		List<Map<String, Integer>> nGramFrequencies = new ArrayList<>();
		for(int i = 1; i <= this.n; i++) {
			nGramFrequencies.add(this.reconstructFrequencies(i));
		}
		return nGramFrequencies;
	}
	
	private Map<String, Integer> reconstructFrequencies(int n) {
		Map<String, Integer> freqs = new HashMap<>();
		String INDEXING_PATH = this.IN_PATH + "indices/" + n + "/index.ser";
		
		// Retrieving indices and lexicon
		Indexing indexing = dl.loadIndexing(INDEXING_PATH, false);
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
