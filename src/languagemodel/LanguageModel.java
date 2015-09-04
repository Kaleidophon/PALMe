package languagemodel;

import smoothing.*;
import inout.*;

import java.util.*;

public class LanguageModel {
	
	final int n;
	final String IN_PATH;
	Smoothing smoothing;
	DataLoader dl;
	List<Indexing> indexings;
	//private List<Map<String, Integer>> nGramFrequencies;
	private boolean validateState;
	
	public LanguageModel(int n, String IN_PATH, Smoothing smoothing, DataLoader dl, Indexing indexing, int mode) {
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.smoothing = smoothing;
		this.dl = dl;
		this.validateState = false;
		long startTime = System.nanoTime();
		this.indexings = this.collectIndexings(mode, indexing);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Loading Language Model data took " + duration / 1000000000.0 + " seconds on average.");
	}
	
	public List<Indexing> collectIndexings(int mode, Indexing indexing) {
		List<Indexing> indexings = new ArrayList<>();
		String INDEXING_PATH = "";
		
		for(int i = 1; i <= this.n; i++) {
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
			indexings.add(indexing);
		}
		return indexings;
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
	
	public List<Indexing> getIndexings() {
		return this.indexings;
	}
	
}
