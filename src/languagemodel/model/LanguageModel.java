package languagemodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.IllegalArgumentException;

import utilities.Toolbox;
import inout.indexing.BiMapLexicon;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;
import languagemodel.calc.ProbabilityCalculation;

/**
 * This class serves two purposes. <p>
 * a) Calculate the probability for n-grams. <p>
 * b) Provide an environment to compute the probability of a sequence. <p>
 * <p>
 * Therefore, it has multiple modes: <p>
 * - "default": n-grams are used to compute probability <p>
 * - "fast back-off": If n-gram isn't found, it looks for a suitable (n-1)-gram. It loads every n-gram layer into memory in fast mode <p>
 * - "efficient back-off": If n-gram isn't found, it looks for a suitable (n-1)-gram. It loads only the currently needed n-gram layer into memory <p>
 * in efficient mode (makes it very slow for long sequences)
 * 
 * @author Dennis Ulmer
 */
public class LanguageModel {
	
	private final int n;
	private ProbabilityCalculation prob_calc;
	private boolean validateState;
	private PathHandler ph;
	private String IN_PATH;
	private boolean debug;
	private boolean normalization;
	private String mode;
	private List<Map<List<Integer>, Double>> n_probabilities;
	private BiMapLexicon lex;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/** Constructor to calculate n-gram probabilities based on n-gram frequencies */
	public LanguageModel(int n, String IN_PATH, ProbabilityCalculation prob_calc, String mode, boolean normalization) {
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.normalization = normalization;
		this.mode = mode;
		this.validateState = false;
		this.checkIntegrity();
		this.ph = new PathHandler(IN_PATH);
		this.prob_calc = prob_calc;
		this.debug = false;
		if (debug) this.ph.printPaths();
	}
	
	/** Constructor to load already calculated n-gram probabilities */
	public LanguageModel(int n, String IN_PATH, String mode, boolean normalization) {
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.debug = false;
		this.mode = mode;
		this.normalization = normalization;
		this.checkIntegrity();
		this.ph = new PathHandler(IN_PATH);
		if (debug) this.ph.printPaths();
		if (mode.equals("fast back-off")) { setup(); }
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Calculates n-gram probabilities. */
	public void calculate() {
		this.prob_calc.calculateNgramProbabilities(this.getN(), this.getPathHandler());
	}
	
	/** Calculates n-gram probabilities parallelized */
	public void calculateParallelized(int producer, int consumer) {
		this.prob_calc.calculateNgramProbabilitiesParallelized(this.getN(), this.getPathHandler(), producer, consumer);
	}
	
	/** Returns the probability of a sequence. See class description for an explanation of the different modes. */
	public double getSequenceProbability(String seq) {
		// Initializing
		double probability = 1.0;
		long startTime = System.nanoTime();
		List<String> tokens = new ArrayList<>();
		tokens.addAll(Arrays.asList(seq.trim().split(" ")));
		if (!tokens.contains("<s>") && !tokens.contains("</s")) {
			tokens.add(0, "<s>");
			tokens.add("</s>");
		}
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
				if (this.debug) Toolbox.printArray(slice); System.out.println("Slice prob is " + prob);
				probability *= prob;
			}
		} else if (mode.equals("fast back-off")) {
			for (int i = 0; i <= tokens_.length; i++) {
				for (int j = i - this.getN(); j != i; j++) {
					try {
						String[] slice = Arrays.copyOfRange(tokens_, j, i);
						if(this.debug) Toolbox.printArray(slice);
						List<Integer> ids = this.translateToInt(slice, this.lex);
						Double prob = this.n_probabilities.get(slice.length-1).get(ids);
						if (prob != null) {
							if (this.debug) System.out.println("It's a match!");
							probability *= prob;
							break;
						} else if (slice.length == 1 && prob == null) {
							probability = 0;
							break;
						}
					} catch(IndexOutOfBoundsException ioobe) {
						continue;
					}
				}
			}
		} else if (mode.equals("efficient back-off")) {
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
		if (this.getNormalization()) probability = Math.pow(probability, (1.0 / tokens.size()));
		if (debug) {
			System.out.println("Sequence probability for '" + seq + "' is " + probability + ".");
			System.out.println("Calculating sequence probability with " + this.getMode() + " Language model and " + this.n + "-grams took " + 
								Math.round(duration / 10000000.0) / 100.0 + " s in total.");
		}
		return probability;
	}
	
	/** Returns the probability of a sequence, calculated parallelized.
	 * See class description for an explanation of the different modes. */
	public synchronized double getSequenceProbabilityParallelized(String seq) {
		// Initializing
		double probability = 1.0;
		long startTime = System.nanoTime();
		List<String> tokens = new ArrayList<>();
		tokens.addAll(Arrays.asList(seq.trim().split(" ")));
		if (!tokens.contains("<s>") && !tokens.contains("</s")) {
			tokens.add(0, "<s>");
			tokens.add("</s>");
		}
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
				if (this.debug) Toolbox.printArray(slice); System.out.println("Slice prob is " + prob);
				probability *= prob;
			}
		} else if (mode.equals("fast back-off")) {
			for (int i = 0; i <= tokens_.length; i++) {
				for (int j = i - this.getN(); j != i; j++) {
					try {
						String[] slice = Arrays.copyOfRange(tokens_, j, i);
						if(this.debug) Toolbox.printArray(slice);
						List<Integer> ids = this.translateToInt(slice, this.lex);
						Double prob = this.n_probabilities.get(slice.length-1).get(ids);
						if (prob != null) {
							if (this.debug) System.out.println("It's a match!");
							probability *= prob;
							break;
						} else if (slice.length == 1 && prob == null) {
							probability = 0;
							break;
						}
					} catch(IndexOutOfBoundsException ioobe) {
						continue;
					}
				}
			}
		} 
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (this.getNormalization()) probability = Math.pow(probability, (1.0 / tokens.size()));
		if (debug) {
			System.out.println("Sequence probability for '" + seq + "' is " + probability + ".");
			System.out.println("Calculating sequence probability with " + this.getMode() + " Language model and " + this.n + "-grams took " + 
								Math.round(duration / 10000000.0) / 100.0 + " s in total.");
		}
		return probability;
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	/** @return A probability {@link Indexing} with n-grams */
	private Indexing<Double> getProbIndexing(int n) {
		return this.getProbIndexing(n, 1);
	}
	
	/** @return A probability {@link Indexing} with n-grams. Loading is parallelized */
	private Indexing<Double> getProbIndexing(int n, int threads) {
		List<List<Path>> pathlists = new ArrayList<>();
		pathlists.add(this.ph.getPathsWithN(n));
		pathlists.add(this.ph.getPathsWithType("probability"));
		pathlists.add(this.ph.getPathsWithTask("read"));
		Path prob_indexing_path = this.ph.intersection(pathlists).get(0);
		Indexing<Double> prob_indexing = null;
		
		// Load lexicon
		Path lex_path = ph.getFirstPathWithAttributes("zipped lexicon");
		if (lex_path == null) {
			lex_path = ph.getFirstPathWithAttributes("raw lexicon");
		}
		
		// Instantiate Indexing
		switch (prob_indexing_path.getCoding()) {
			case ("HEXADECIMAL"):
				prob_indexing = new HexadecimalIndexing<Double>(prob_indexing_path.getDirectory(), lex_path.getDirectory(), prob_indexing_path.isZipped(), threads);
				break;
			case ("BINARY"):
				prob_indexing = new BinaryIndexing<Double>(prob_indexing_path.getDirectory(), lex_path.getDirectory(), prob_indexing_path.isZipped(), threads);
				break;
			case ("DEFAULT"):
				prob_indexing = new Indexing<Double>(prob_indexing_path.getDirectory(), lex_path.getDirectory(), prob_indexing_path.isZipped(), threads);
				break;
		}
		return prob_indexing;
	}
	
	/** Loads all n-gram layers. Only evoked in "fast back-off" mode. */
	private void setup() {
		long startTime = System.nanoTime();
		this.n_probabilities = new ArrayList<>();
		this.lex = (BiMapLexicon) this.getProbIndexing(1).getLexicon();
		
		for (int i = 0; i < this.getN() - 1; i++) {
			this.n_probabilities.add((Map<List<Integer>, Double>) this.getProbIndexing(i+1).getIndices());
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (debug) System.out.println("Setting up language model took " + Math.round(duration / 10000000.0) / 100.0 + " s in total.");
	}
	
	/** Translates a list of tokens to their corresponding IDs. */
	private List<Integer> translateToInt(String[] tokens, BiMapLexicon lex) {
		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < tokens.length; i++) {
			ids.add(lex.getKey(tokens[i]));
		}
		return ids;
	}
	
	/** Change debug mode */
	public void flipDebug() {
		this.debug = (this.debug) ? false : true;
	}
	
	/** Change normalization mode */
	public void flipNormalization() {
		this.normalization = (this.normalization) ? false : true;
	}
	
	/** Checks all language model parameters. */
	private void checkIntegrity() {
		if (this.getN() < 1) {
			throw new IllegalArgumentException("n must be greater/equals 1");
		}
		if (this.debug) System.out.println("'" + this.IN_PATH + "'");
		Pattern r = Pattern.compile("((\\.(\\.?))?/[a-z_\\-\\s0-9\\.]+)+/.*paths\\.xml");
		Matcher m = r.matcher(this.IN_PATH);
		m.matches();
		if (m.group() == null) {
			throw new IllegalArgumentException("Illegal path");
		}
		if (!(this.mode.equals("fast back-off") || this.mode.equals("default") || this.mode.equals("efficient back-off"))) {
			throw new IllegalArgumentException("Illegal mode");
		}
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** Set if language model parameters should be checked.*/
	public void setValidateState(boolean validateState) {
		this.validateState = validateState;
	}
	
	/** @return {@link ProbabilityCalculation} function. */
	public ProbabilityCalculation getProbabilityCalculation() {
		return this.prob_calc;
	}
	
	/** @return highest n-gram order */
	public int getN() {
		return this.n;
	}

	/** @return Current language model mode */
	public String getMode() {
		return this.mode;
	}
	
	/** @return Current {@link PathHandler} */
	public PathHandler getPathHandler() {
		return this.ph;
	}
	
	/** Set debug mode. */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/** Set normalization */
	public void setNormalization(boolean norm) {
		this.normalization = norm;
	}
	
	/** @return Whether normalization is on or off */
	public boolean getNormalization() {
		return this.normalization;
	}
	
	/** @return Whether debug mode is on or off */
	public boolean debug() {
		return this.debug;
	}
	
	// ------------------------------------------------ Nested classes -----------------------------------------------
	
	/**
	 * Class to store all possible n-grams for a word in a sequence for the "efficient back-off" mode of {@link LanguageModel}.
	 * <p>
	 * Imagine a sequence like "<s> The dog bites the postman . <s>"
	 * A {@code WordNGrams}-object for postman with n = 4 would then contain
	 * {"dog bites the postman", "bites the postman", "the postman", "postman"}.
	 * */
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
