package languagemodel;

import smoothing.*;
import inout.*;
import inout.general.DataLoader;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;

import java.util.*;

public class LanguageModel {
	
	private final int n;
	private Smoothing smoothing;
	private DataLoader dl;
	private List<Indexing> indexings;
	private boolean validateState;
	private PathHandler ph;
	
	public LanguageModel(int n, String IN_PATH, Smoothing smoothing, DataLoader dl, Indexing indexing, int mode) {
		this.n = n;
		this.ph = new PathHandler(IN_PATH);
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
		String LEXICON_PATH = this.ph.getPathsWithAttributes((mode == 0) ? "raw" : "zipped" + " lexicon").get(0).getDirectory();
		
		for(int i = 1; i <= this.n; i++) {
			switch (mode) {
				case (0):
					// Unzipped
					if (indexing instanceof BinaryIndexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("raw binary frequency indexing " + i).get(0).getDirectory();
						indexing = new BinaryIndexing(INDEXING_PATH, LEXICON_PATH, false);
					}
					else if (indexing instanceof HexadecimalIndexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("raw hexadecimal frequency indexing " + i).get(0).getDirectory();
						indexing = new HexadecimalIndexing(INDEXING_PATH, LEXICON_PATH, false);
					}
					else if (indexing instanceof Indexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("raw default frequency indexing " + i).get(0).getDirectory();
						indexing = new Indexing(INDEXING_PATH, LEXICON_PATH, false);
					}
					break;
				case (1):
					// Zipped
					if (indexing instanceof BinaryIndexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("zipped binary frequency indexing " + i).get(0).getDirectory();
						indexing = new BinaryIndexing(INDEXING_PATH, LEXICON_PATH, true);
					}
					else if (indexing instanceof HexadecimalIndexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("zipped hexadecimal frequency indexing " + i).get(0).getDirectory();
						indexing = new HexadecimalIndexing(INDEXING_PATH, LEXICON_PATH, true);
					}
					else if (indexing instanceof Indexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("zipped default frequency indexing " + i).get(0).getDirectory();
						indexing = new Indexing(INDEXING_PATH, LEXICON_PATH, true);
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
