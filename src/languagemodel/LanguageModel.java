package languagemodel;

import smoothing.*;
import inout.*;
import inout.general.DataLoader;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.IllegalArgumentException;

public class LanguageModel {
	
	private final int n;
	private int mode;
	private Smoothing smoothing;
	private List<Indexing> indexings;
	private boolean validateState;
	private PathHandler ph;
	private String IN_PATH;
	
	public LanguageModel(int n, String IN_PATH, Smoothing smoothing, Indexing indexing, int mode) {
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.mode = mode;
		this.checkIntegrity();
		this.ph = new PathHandler(IN_PATH);
		
		List<Path> paths = this.ph.getPaths();
		for (Path p : paths) {
			System.out.println(p.toString());
		}
		
		this.smoothing = smoothing;
		this.validateState = false;
		long startTime = System.nanoTime();
		this.indexings = this.collectIndexings(mode, indexing);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Loading Language Model data took " + duration / 1000000000.0 + " in total.");
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
					} else if (indexing instanceof HexadecimalIndexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("raw hexadecimal frequency indexing " + i).get(0).getDirectory();
						indexing = new HexadecimalIndexing(INDEXING_PATH, LEXICON_PATH, false);
					} else if (indexing instanceof Indexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("raw default frequency indexing " + i).get(0).getDirectory();
						indexing = new Indexing(INDEXING_PATH, LEXICON_PATH, false);
					}
					break;
				case (1):
					// Zipped
					if (indexing instanceof BinaryIndexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("zipped binary frequency indexing " + i).get(0).getDirectory();
						indexing = new BinaryIndexing(INDEXING_PATH, LEXICON_PATH, true);
					} else if (indexing instanceof HexadecimalIndexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("zipped hexadecimal frequency indexing " + i).get(0).getDirectory();
						indexing = new HexadecimalIndexing(INDEXING_PATH, LEXICON_PATH, true);
					} else if (indexing instanceof Indexing) {
						String INDEXING_PATH = this.ph.getPathsWithAttributes("zipped default frequency indexing " + i).get(0).getDirectory();
						indexing = new Indexing(INDEXING_PATH, LEXICON_PATH, true);
					}
					break;
			}
			indexings.add(indexing);
		}
		return indexings;
	}
	
	public void calculate() {
		this.smoothing.calculateNgramProbabilities(this.indexings);
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
	
	public Smoothing getSmoothing() {
		return this.smoothing;
	}
	
	public int getN() {
		return this.n;
	}

	public int getMode() {
		return this.mode;
	}
	
	private void checkIntegrity() {
		if (this.getN() < 1) {
			throw new IllegalArgumentException("n must be greater/equals 1");
		}
		System.out.println("'" + this.IN_PATH + "'");
		Pattern r = Pattern.compile("((\\.(\\.?))?/[a-z_\\-\\s0-9\\.]+)+/paths\\.xml");
		Matcher m = r.matcher(this.IN_PATH);
		m.matches();
		if (m.group() == null) {
			throw new IllegalArgumentException("Illegal path");
		}
		if (this.mode != 0 && this.mode != 1) {
			throw new IllegalArgumentException("Illegal mode");
		}
	}
	
}
