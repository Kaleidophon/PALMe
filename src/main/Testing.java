package main;

import inout.general.DataLoader;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;
import utilities.eval.Evaluation;
import languagemodel.calc.MaximumLikelihoodEstimation;
import languagemodel.calc.ProbabilityCalculation;
import languagemodel.model.LanguageModel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class that contains different methods to test and / or time various parts of the whole project.
 * 
 * @author Dennis Ulmer
 */
public class Testing {
	
	// ----------------------------------------------------- Main ----------------------------------------------------
	
	public static void main(String[] args) {
		timeLanguageModelEvaluation(5, "./rsc/paths.xml", "./rsc/corpora/dewiki_plain_1000k_test.txt", 25);
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------

	/** Creates frequency {@link Indexing}-objects for a specific n. */
	public static <V extends Number> void createTestData(int n, String IN_PATH) {
		PathHandler ph = new PathHandler(IN_PATH);
		// Load lexicon
		Path lex_path = ph.getFirstPathWithAttributes("zipped lexicon");
		if (lex_path == null) {
			lex_path = ph.getFirstPathWithAttributes("raw lexicon");
		}	
		
		List<Indexing<V>> indexings = new ArrayList<>();
		System.out.println("Reading frequencies...");
		DataLoader dl = new DataLoader("./rsc/freqs/" + n + "/res.txt");
		Map<String, Integer> freqs = dl.readFrequencies();
		indexings.add(new Indexing<V>());
		indexings.add(new BinaryIndexing<V>());
		indexings.add(new HexadecimalIndexing<V>());
		
		for (Indexing<V> indexing : indexings) {
			String specification = "";
			if (indexing instanceof BinaryIndexing) {
				System.out.println("Create new "  + indexing.getClass().getName() + " for n = " + n);
				indexing = new BinaryIndexing(freqs, "./rsc/indices/", lex_path.getDirectory());
				specification = "binary frequency indexing " + n + " write";
			} else if (indexing instanceof HexadecimalIndexing) {
				System.out.println("Create new "  + indexing.getClass().getName() + " for n = " + n);
				indexing = new HexadecimalIndexing(freqs, "./rsc/indices/", lex_path.getDirectory());
				specification = "hexadecimal frequency indexing " + n + " write";
			} else if (indexing instanceof Indexing) {
				System.out.println("Create new "  + indexing.getClass().getName() + " for n = " + n);
				indexing = new Indexing(freqs, "./rsc/indices/", lex_path.getDirectory());
				specification = "default frequency indexing " + n + " write";
			}
			indexing.dump(ph.getFirstPathWithAttributes("raw " + specification).getDirectory(), false);
			indexing.dump(ph.getFirstPathWithAttributes("zipped " + specification).getDirectory(), true);
			indexing = null;
			//dl.dumpIndexing(indexing, "./rsc/indices/" + n + "/", true);
		}
	}
	
	/** Times the duration of loading an {@link Indexing}-object. */
	public static <V extends Number> void timeIndexingLoading(int n, int mode, String IN_PATH, int iterations, int threads) {
		System.out.println("Path: " + IN_PATH + " | Mode: " + mode);
		PathHandler ph = new PathHandler(IN_PATH);
		// Load lexicon
		Path lex_path = ph.getFirstPathWithAttributes("zipped lexicon");
		if (lex_path == null) {
			lex_path = ph.getFirstPathWithAttributes("raw lexicon");
		}	
		long[] durations = new long[iterations];
		String experiment_name = "";
		String specification = "";
		//{raw / zipped} {default / binary / hexadecimal} {frequency / probability} indexing n {read / write}
		
		switch(mode) {
			case(0): 
				experiment_name = "Timing unzipped Indexing loading"; 
				specification = "raw default frequency indexing " + n + " read";
				break;
			case(1): 
				experiment_name = "Timing unzipped Binary Indexing loading"; 
				specification = "raw binary frequency indexing " + n + " read";
				break;
			case(2): 
				experiment_name = "Timing unzipped Hexadecimal Indexing loading"; 
				specification = "raw hexadecimal frequency indexing " + n + " read";
				break;
			case(3): 
				experiment_name = "Timing zipped Indexing loading"; 
				specification = "zipped default frequency indexing " + n + " read";
				break;
			case(4): 
				experiment_name = "Timing zipped Binary Indexing loading"; 
				specification = "zipped binary frequency indexing " + n + " read";
				break;
			case(5): 
				experiment_name = "Timing zipped Hexadecimal Indexing loading"; 
				specification = "zipped hexadecimal frequency indexing " + n + " read";
				break;
		}
		
		if (threads > 1) experiment_name = experiment_name.substring(0, 6) + " parallelized " + experiment_name.substring(7, experiment_name.length());
		Path p = ph.getFirstPathWithAttributes(specification);
		
		for (int i = 0; i < iterations; i++) {
			System.out.println("Starting iteration #" + (i+1) + "...");
			long startTime = System.nanoTime();
			switch (mode) {
				case (0):
					Indexing<V> indexing1 = null;
					if (threads > 1) {
						indexing1 = new Indexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped(), threads);
					} else {
						indexing1 = new Indexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped());
					}
					break;
				case (1):
					Indexing<V> indexing2 = null;
					if (threads > 1) {
						indexing2 = new BinaryIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped(), threads);
					} else {
						indexing2 = new BinaryIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped());
					}
					break;
				case (2):
					Indexing<V> indexing3 = null;
					if (threads > 1) {
						indexing3 = new HexadecimalIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped(), threads);
					} else {
						indexing3 = new HexadecimalIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped());
					}
					break;
				case (3):
					Indexing<V> indexing4 = null;
					if (threads > 1) {
						indexing4 = new Indexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped(), threads);
					} else {
						indexing4 = new Indexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped());
					}
					break;
				case (4):
					Indexing<V> indexing5 = null;
					if (threads > 1) {
						indexing5 = new BinaryIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped(), threads);
					} else {
						indexing5 = new BinaryIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped());
					}
					break;
				case (5):
					Indexing<V> indexing6 = null;
					if (threads > 1) {
						indexing6 = new HexadecimalIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped(), threads);
					} else {
						indexing6 = new HexadecimalIndexing<>(p.getDirectory(), lex_path.getDirectory(), p.isZipped());
					}
					break;
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			durations[i] = duration;
			System.out.println("Iteration #" + (i+1) + " took " + duration / 1000000000.0 + " s.");
		}
		System.out.println(experiment_name + " took " + average(durations) / 1000000000.0 + " seconds on average.");
	}
	
	/** Times the calculation of n-gram probabilities. */
	public static void timeProbabilityCalculation(int n, String IN_PATH, ProbabilityCalculation prob_calc, int iterations) {
		timeProbabilityCalculation(n, IN_PATH, prob_calc, 0, 0, iterations);
	}
	
	/** Times the parallel calculation of n-gram probabilities. */
	public static void timeProbabilityCalculation(int n, String IN_PATH, ProbabilityCalculation prob_calc, int producer, int consumer, int iterations) {
		long[] durations = new long[iterations];
		String experiment_name = (producer == 0 && consumer == 0) ? "Default " : "Parallelized ";
		experiment_name += "language model probability calculation";
		
		for (int i = 0; i < iterations; i++) {
			System.out.println("Starting iteration #" + (i+1) + "...");
			LanguageModel lm = new LanguageModel(n, IN_PATH, prob_calc, "fast back-off", true);
			long startTime = System.nanoTime();
			if (producer == 0 && consumer == 0) {
				lm.calculate();
			} else {
				lm.calculateParallelized(producer, consumer);
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			durations[i] = duration;
			System.out.println("Iteration #" + (i+1) + " took " + duration / 1000000000.0 + " s.");
			lm = null;
		}
		System.out.println(experiment_name + " took " + average(durations) / 1000000000.0 + " seconds on average.");
	}
	
	public static void timeLanguageModelEvaluation(int n, String IN_PATH, String TEST_INPATH, int iterations) {
		timeLanguageModelEvaluation(n, IN_PATH, TEST_INPATH, 0, 0, iterations);
	}
	
	/** Times the evaluation of a {@link LanguageModel}. */
	public static void timeLanguageModelEvaluation(int n, String IN_PATH, String TEST_INPATH, int producer, int consumer, int iterations) {
		LanguageModel lm = new LanguageModel(n, IN_PATH, "fast back-off", true);
		long[] durations = new long[iterations];
		String experiment_name = (producer == 0 && consumer == 0) ? "Default " : "Parallelized ";
		experiment_name += "language model evaluation";
				
		for (int i = 0; i < iterations; i++) {
			System.out.println("Starting iteration #" + (i+1) + "...");
			long startTime = System.nanoTime();
			if (producer == 0 && consumer == 0) {
				Evaluation.evaluateLanguageModel(lm, TEST_INPATH);
			} else {
				Evaluation.evaluateLanguageModelParallelized(lm, TEST_INPATH, producer, consumer);
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			durations[i] = duration;
			System.out.println("Iteration #" + (i+1) + " took " + duration / 1000000000.0 + " s.");
		}
		System.out.println(experiment_name + " took " + average(durations) / 1000000000.0 + " seconds on average.");
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	/** Returns the average of a {@code List} of {@code Long} */
	private static double average(long[] a) {
		long sum = 0;
		for (long e : a) {
			sum += e;
		}
		return sum * 1.0 / a.length;
	}
}