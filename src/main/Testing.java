package main;

import inout.general.DataLoader;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import utilities.eval.Evaluation;
import languagemodel.calc.ProbabilityCalculation;
import languagemodel.model.LanguageModel;

public class Testing {
	
	public static void main(String[] args) {
		createTestData(2, "./rsc/create_freqs_paths.xml");
	}
	
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
	
	public static <V extends Number> void timeIndexingLoading(int mode, String IN_PATH, String LEX_PATH, boolean zipped, int iterations, int threads) {
		System.out.println("Path: " + IN_PATH + " | Mode: " + mode);
		DataLoader dl = new DataLoader();
		long[] durations = new long[iterations];
		String experiment_name = "";
		
		switch(mode) {
			case(0): 
				experiment_name = (zipped) ? "Timing zipped Indexing loading" : "Timing unzipped Indexing loading"; 
				break;
			case(1): 
				experiment_name = (zipped) ? "Timing zipped Binary Indexing loading" : "Timing unzipped Binary Indexing loading"; 
				break;
			case(2): 
				experiment_name = (zipped) ? "Timing zipped Hexadecimal Indexing loading" : "Timing unzipped Hexadecimal Indexing loading"; 
				break;
		}
		
		if (threads > 1) experiment_name = experiment_name.substring(0, 6) + " parallelized" + experiment_name.substring(7, experiment_name.length());
		
		for (int i = 0; i < iterations; i++) {
			System.out.println("Starting iteration #" + (i+1) + "...");
			long startTime = System.nanoTime();
			switch (mode) {
				case (0):
					Indexing<V> indexing1 = null;
					if (threads > 1) {
						indexing1 = new Indexing<>(LEX_PATH, IN_PATH, zipped, threads);
					} else {
						indexing1 = new Indexing<>(LEX_PATH, IN_PATH, zipped);
					}
					break;
				case (2):
					Indexing<V> indexing2 = null;
					if (threads > 1) {
						indexing2 = new BinaryIndexing<>(LEX_PATH, IN_PATH, zipped, threads);
					} else {
						indexing2 = new BinaryIndexing<>(LEX_PATH, IN_PATH, zipped);
					}
					break;
				case (3):
					Indexing<V> indexing3 = null;
					if (threads > 1) {
						indexing3 = new HexadecimalIndexing<>(LEX_PATH, IN_PATH, zipped, threads);
					} else {
						indexing3 = new HexadecimalIndexing<>(LEX_PATH, IN_PATH, zipped);
					}
					break;
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			durations[i] = duration;
			System.out.println("Iteration #" + (i+1) + " took " + duration + " s.");
		}
		System.out.println(experiment_name + " took " + average(durations) / 1000000000.0 + " seconds on average.");
	}
	
	public static void timeProbabilityCalculation(int n, String IN_PATH, ProbabilityCalculation prob_calc, int iterations) {
		testProbabilityCalculationTime(n, IN_PATH, prob_calc, 0, 0, iterations);
	}
	
	public static void testProbabilityCalculationTime(int n, String IN_PATH, ProbabilityCalculation prob_calc, int producer, int consumer, int iterations) {
		LanguageModel lm = new LanguageModel(n, IN_PATH, prob_calc, "fast back-off", true);
		long[] durations = new long[iterations];
		String experiment_name = (producer == 0 && consumer == 0) ? "Default " : "Parallelized ";
		experiment_name += "language model probability calculation";
		
		for (int i = 0; i < iterations; i++) {
			System.out.println("Starting iteration #" + (i+1) + "...");
			long startTime = System.nanoTime();
			if (producer == 0 && consumer == 0) {
				lm.calculate();
			} else {
				lm.calculateParallelized(producer, consumer);
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			durations[i] = duration;
			System.out.println("Iteration #" + (i+1) + " took " + duration + " s.");
		}
		System.out.println(experiment_name + " took " + average(durations) / 1000000000.0 + " seconds on average.");
	}
	
	public static void timeLanguageModelEvaluation(int n, String IN_PATH, String mode, String TEST_INPATH, int producer, int consumer, int iterations) {
		LanguageModel lm = new LanguageModel(n, IN_PATH,"fast back-off", true);
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
			System.out.println("Iteration #" + (i+1) + " took " + duration + " s.");
		}
		System.out.println(experiment_name + " took " + average(durations) / 1000000000.0 + " seconds on average.");
	}
	
	private static double average(long[] a) {
		long sum = 0;
		for (long e : a) {
			sum += e;
		}
		return sum * 1.0 / a.length;
	}
}
