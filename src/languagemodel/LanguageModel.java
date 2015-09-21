package languagemodel;

import smoothing.*;
import utilities.Toolbox;
import inout.indexing.BiMapLexicon;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.IllegalArgumentException;

public class LanguageModel {
	
	private final int n;
	private Smoothing smoothing;
	private boolean validateState;
	private PathHandler ph;
	private String IN_PATH;
	private boolean debug;
	private boolean normalization;
	private String mode;
	
	public LanguageModel(int n, String IN_PATH, Smoothing smoothing, String mode, boolean normalization) {
		// Constructor to calculate n-gram probabilities based on n-gram frequencies
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.normalization = normalization;
		this.checkIntegrity();
		this.ph = new PathHandler(IN_PATH);
		this.smoothing = smoothing;
		this.validateState = false;
		this.debug = false;
		this.mode = mode;
		if (debug) this.ph.printPaths();
		this.calculate();
	}
	
	public LanguageModel(int n, String IN_PATH, String mode, boolean normalization) {
		// Constructor to load already computated n-gram probabilities
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.debug = false;
		this.mode = mode;
		this.normalization = normalization;
		this.checkIntegrity();
		this.ph = new PathHandler(IN_PATH);
		if (debug) this.ph.printPaths();
	}
	
	public double getSequenceProbability(String seq) {
		// Initializing
		double probability = 1.0;
		long startTime = System.nanoTime();
		List<String> tokens = new ArrayList<>();
		tokens.add("<s>");
		tokens.addAll(Arrays.asList(seq.trim().split(" ")));
		tokens.add("</s>");
		// Convert to array
		String[] tokens_ = new String[tokens.size()];
		tokens_ = tokens.toArray(tokens_);
		
		if (mode.equals("default")) {
			if (tokens.size() < this.getN()) {
				return 0.0;
			}
			Indexing<Double> prob_indexing = this.getProbIndexing(this.getN());
			Map<List<Integer>, Double> probs = (Map<List<Integer>, Double>) prob_indexing.getIndices();
			for (int i = 0; i < tokens_.length - this.getN() + 1; i++) {
				String[] slice = Arrays.copyOfRange(tokens_, i, i + this.getN());
				List<Integer> ids = this.translateToInt(slice, (BiMapLexicon) prob_indexing.getLexicon());
				double prob = (probs.get(ids) == null) ? 0 : probs.get(ids);
				if (debug) Toolbox.printArray(slice); System.out.println("Slice prob is " + prob);
				probability *= prob;
			}
			
		} else if (mode.equals("back-off")) {
			List<WordNGrams> words_ngrams = new ArrayList<>(tokens_.length);
			
			int highest_order = 0;
			// Find all possible ngrams and define highest order ngram
			for (int i = 0; i < tokens_.length; i++) {
				WordNGrams word_ngrams = new WordNGrams(this.n);
				for (int j = 0; j <= i; j++) {
					if (i - j > this.n-1) continue;
					String[] slice = Arrays.copyOfRange(tokens_, j, i+1);
					word_ngrams.set(slice.length-1, slice);
				}
				words_ngrams.add(word_ngrams);
				if (word_ngrams.highestOrder() > highest_order) highest_order = word_ngrams.highestOrder();
			}
			
			// Screen output for debugging
			if (debug) {
				for (WordNGrams word_ngrams : words_ngrams) {
					for (int i = this.n-1; i >= 0; i--) {
						try {
							Toolbox.printArray(word_ngrams.get(i));
						} catch (NullPointerException npe) {
							continue;
						}
					}
					System.out.println("--------");
				}
				System.out.println("Highest order: " + highest_order);
			}
			
			for (int i = highest_order; i >= 1; i--) {
				// Load current probability indexing; start with highest order
				if (debug) System.out.println("\nChecking n-grams of n = " + i);
				Indexing<Double> prob_indexing = this.getProbIndexing(i);
			
				Map<List<Integer>, Double> probs = (Map<List<Integer>, Double>) prob_indexing.getIndices();	
				Stack<WordNGrams> obsoletes = new Stack<>(); // Words with found ngrams will be ignored in future iterations
				WordNGrams word_ngram = null;
				
				for (int j = 0; j < words_ngrams.size(); j++) {
					word_ngram = words_ngrams.get(j);
					try {
						String[] ngram = word_ngram.get(i-1);
						List<Integer> ids = this.translateToInt(ngram, (BiMapLexicon) prob_indexing.getLexicon());
						if (debug) Toolbox.printArray(ngram); 
						if (debug) Toolbox.printList(ids);
						if (probs.containsKey(ids)) {
							if (debug) System.out.println("It's a match!");
							probability *= probs.get(ids);
							obsoletes.push(word_ngram);
						}
					} catch (NullPointerException npe) {}
				}
				
				// Remove obsolete words
				while (!obsoletes.empty()) {
					words_ngrams.remove(obsoletes.pop());
				}
			}
		}
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (debug) {
			System.out.println("Sequence probability for '" + seq + "' is " + probability + ".");
			System.out.println("Calculating sequence probability with " + this.n + "-grams took " + 
								Math.round(duration / 10000000.0) / 100.0 + " s in total.");
		}
		System.out.println(probability);
		probability = Math.pow(probability, (1.0 / tokens.size()));
		System.out.println(probability);
		return probability;
	}
	
	private Indexing<Double> getProbIndexing(int n) {
		List<List<Path>> pathlists = new ArrayList<>();
		pathlists.add(ph.getPathsWithN(n));
		pathlists.add(ph.getPathsWithType("probability"));
		pathlists.add(ph.getPathsWithTask("read"));
		Path prob_indexing_path = ph.intersection(pathlists).get(0);
		Indexing<Double> prob_indexing = null;
		
		// Load lexicon
		Path lex_path = ph.getFirstPathWithAttributes("zipped lexicon");
		if (lex_path == null) {
			lex_path = ph.getFirstPathWithAttributes("raw lexicon");
		}
		
		// Instantiate Indexing
		switch (prob_indexing_path.getCoding()) {
			case ("HEXADECIMAL"):
				prob_indexing = new HexadecimalIndexing<Double>(prob_indexing_path.getDirectory(), lex_path.getDirectory(), prob_indexing_path.isZipped());
				break;
			case ("BINARY"):
				prob_indexing = new BinaryIndexing<Double>(prob_indexing_path.getDirectory(), lex_path.getDirectory(), prob_indexing_path.isZipped());
				break;
			case ("DEFAULT"):
				prob_indexing = new Indexing<Double>(prob_indexing_path.getDirectory(), lex_path.getDirectory(), prob_indexing_path.isZipped());
				break;
		}
		return prob_indexing;
	}
	
	private void calculate() {
		this.smoothing.calculateNgramProbabilities(this.getN(), this.getPathHandler());
	}
	
	private List<Integer> translateToInt(String[] tokens, BiMapLexicon lex) {
		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < tokens.length; i++) {
			ids.add(lex.getKey(tokens[i]));
		}
		return ids;
	}
	
	public void setValidateState(boolean validateState) {
		this.validateState = validateState;
	}
	
	public Smoothing getSmoothing() {
		return this.smoothing;
	}
	
	public int getN() {
		return this.n;
	}

	public String getMode() {
		return this.mode;
	}
	
	public PathHandler getPathHandler() {
		return this.ph;
	}
	
	public void flipDebug() {
		this.debug = (this.debug) ? false : true;
	}
	
	public void flipNormalization() {
		this.normalization = (this.normalization) ? false : true;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public void setNormalization(boolean norm) {
		this.normalization = norm;
	}
	
	public boolean getNormalization() {
		return this.normalization;
	}
	
	private void checkIntegrity() {
		if (this.getN() < 1) {
			throw new IllegalArgumentException("n must be greater/equals 1");
		}
		if (this.debug) System.out.println("'" + this.IN_PATH + "'");
		Pattern r = Pattern.compile("((\\.(\\.?))?/[a-z_\\-\\s0-9\\.]+)+/paths\\.xml");
		Matcher m = r.matcher(this.IN_PATH);
		m.matches();
		if (m.group() == null) {
			throw new IllegalArgumentException("Illegal path");
		}
		if (!this.mode.equals("back-off") && !this.mode.equals("default")) {
			throw new IllegalArgumentException("Illegal mode");
		}
	}
	
	class WordNGrams {
		
		private int n;
		private List<String[]> ngrams;
		
		public WordNGrams(int n) {
			this.n = n;
			this.ngrams = new ArrayList<>(n);
			for (int i = 0; i < n; i++) {
				ngrams.add(null);
			}
		}
		
		public void set(int index, String[] ngram) {
			this.ngrams.set(index, ngram);
		}
		
		public void add(String[] ngram) {
			this.ngrams.add(ngram);
		}
		
		public String[] get(int index) {
			return ngrams.get(index);
		}
		
		public int highestOrder() {
			return this.ngrams.size();
		}
	}
	
}
