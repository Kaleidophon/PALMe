package languagemodel.calc;

import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.IndexReader;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;
import utilities.Toolbox;
import utilities.eval.CorpusEvaluator;
import utilities.eval.CorpusReader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class MaximumFrequencyEstimation implements ProbabilityCalculation {

	public void calculateNgramProbabilities(int n, PathHandler ph) {
		Indexing<Integer> current_freq_indexing = null;
		Indexing<Integer> last_freq_indexing = null;
		List<Long> times = new ArrayList<>();
		Path lex_path = ph.getFirstPathWithAttributes("zipped lexicon");
		if (lex_path == null) {
			lex_path = ph.getFirstPathWithAttributes("raw lexicon");
		}
		
		for (int i = 0; i < n; i++) {
			System.out.println("Calculating Probabilites for n = " + (i+1) + "...");
			long startTime = System.nanoTime();
			
			current_freq_indexing = this.getFreqIndexing(i+1, ph);
			
			Map<List<Integer>, Integer> freqs = (Map<List<Integer>, Integer>) current_freq_indexing.getIndices();
			Map<List<Integer>, Double> probs = new HashMap<>();
			
			if (i == 0) {
				// Unigrams
				// Determine total
				Set<Integer> values = new HashSet<>(freqs.values());
				Iterator<Integer> iter = values.iterator();
				int total = 0;
				while (iter.hasNext()) {
					total += iter.next();
				}
				for (Map.Entry<List<Integer>, Integer> entry : freqs.entrySet()) {
					List<Integer> index = entry.getKey();
					int freq = entry.getValue();
					double prob = freq * 1.0 / total;
					probs.put(index, prob);
					//System.out.println("Index : " + index.get(0) + " | Freq: " + freq + " | Prob: " + prob);
				}
			} else {
				// Bigrams and higher order
				Map<List<Integer>, Integer> lower_n_freqs = (Map<List<Integer>, Integer>) last_freq_indexing.getIndices();

				for (Map.Entry<List<Integer>, Integer> entry : freqs.entrySet()) {
					List<Integer> index = entry.getKey();
					int freq = entry.getValue();
					List<Integer> lower_index = Toolbox.pop(index);
					int lower_freq = lower_n_freqs.get(lower_index);
					
					double prob = 0.0;
					try {
						prob = freq * 1.0 / lower_freq;
					} catch (NullPointerException npe) {
						prob = 0.0;
					}
					//System.out.println("Index : " + Toolbox.listToString(index) + " | Freq: " + freq + " | Prob: " + prob);
					probs.put(index, prob);
				}
			}
			// Creating new probability indexing
			Indexing<Double> current_prob_indexing = null;
			if (current_freq_indexing instanceof BinaryIndexing) {
				Path out = ph.getFirstPathWithAttributes("zipped binary probability indexing " + (i+1) + " write");
				if (out == null) {
					out = ph.getFirstPathWithAttributes("raw binary probability indexing " + (i+1) + " write");
				}
				current_prob_indexing = new BinaryIndexing<Double>(probs, out.getDirectory(), out.isZipped());				
			} else if (current_freq_indexing instanceof HexadecimalIndexing) {
				Path out = ph.getFirstPathWithAttributes("zipped hexadecimal probability indexing " + (i+1) + " write");
				if (out == null) {
					out = ph.getFirstPathWithAttributes("raw hexadecimal probability indexing " + (i+1) + " write");
				}
				current_prob_indexing = new HexadecimalIndexing<Double>(probs, out.getDirectory(), out.isZipped());	
			} else if (current_freq_indexing instanceof Indexing) {
				Path out = ph.getFirstPathWithAttributes("zipped default probability indexing " + (i+1) + " write");
				if (out == null) {
					out = ph.getFirstPathWithAttributes("raw default probability indexing " + (i+1) + " write");
				}
				current_prob_indexing = new Indexing<Double>(probs, out.getDirectory(), out.isZipped());	
			}
			// Overwrite
			last_freq_indexing = current_freq_indexing;
			current_freq_indexing = null;
			
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			times.add(duration);
			System.out.println("Calculating probabilities for n = " + (i+1) + " took " + duration / 1000000000.0 + " s in total.");
		}
		System.out.print("Total time for all probabilites was " + this.sum(times) / 1000000000.0 + " s in total.\n");
	}
	
	public void calculateNgramProbabilitiesParallelized(int n, PathHandler ph, int n_of_producer, int n_of_consumer) {
		Indexing<Integer> current_freq_indexing = null;
		Indexing<Integer> last_freq_indexing = null;
		List<Long> times = new ArrayList<>();
		Path lex_path = ph.getFirstPathWithAttributes("zipped lexicon");
		if (lex_path == null) {
			lex_path = ph.getFirstPathWithAttributes("raw lexicon");
		}
		
		for (int i = 0; i < n; i++) {
			System.out.println("Calculating Probabilites parallelized for n = " + (i+1) + "...");
			
			Map<List<Integer>, Double> probs = new HashMap<>();
			long startTime = System.nanoTime();
			
			current_freq_indexing = this.getFreqIndexing(i+1, ph);
			
			int total = 0;
			if (i == 0) {
				Map<List<Integer>, Integer> freqs = (Map<List<Integer>, Integer>) current_freq_indexing.getIndices();
				Set<Integer> values = new HashSet<>(freqs.values());
				Iterator<Integer> iter = values.iterator();
				while (iter.hasNext()) {
					total += iter.next();
				}
			}
			
			List<nGramSupplier> producer = new ArrayList<>();
			List<nGramCalculator> consumer = new ArrayList<>();
			nGramDepot ngd = new nGramDepot();
			
			for (int j = 0; j < n_of_producer; j++) {
				producer.add(new nGramSupplier(current_freq_indexing, ngd));
				//System.out.println("Starting producer #" + producer.get(j).getId());
			}
			for (int j = 0; j < n_of_consumer; j++) {
				consumer.add((i == 0) ? new nGramCalculator(i, last_freq_indexing, ngd, total) : new nGramCalculator(i, last_freq_indexing, ngd));
				//System.out.println("Starting consumer #" + consumer.get(j).getId());
			}
			
			for (nGramCalculator nGramCalc : consumer) {
				try {
					nGramCalc.join();
				} catch (InterruptedException ie) {}
				if (!nGramCalc.isRunning()) {
					probs.putAll(nGramCalc.getProbs());
				}
			}
			// Creating new probability indexing
			Indexing<Double> current_prob_indexing = null;
			if (current_freq_indexing instanceof BinaryIndexing) {
				Path out = ph.getFirstPathWithAttributes("zipped binary probability indexing " + (i+1) + " write");
				if (out == null) {
					out = ph.getFirstPathWithAttributes("raw binary probability indexing " + (i+1) + " write");
				}
				current_prob_indexing = new BinaryIndexing<Double>(probs, out.getDirectory(), out.isZipped());				
			} else if (current_freq_indexing instanceof HexadecimalIndexing) {
				Path out = ph.getFirstPathWithAttributes("zipped hexadecimal probability indexing " + (i+1) + " write");
				if (out == null) {
					out = ph.getFirstPathWithAttributes("raw hexadecimal probability indexing " + (i+1) + " write");
				}
				current_prob_indexing = new HexadecimalIndexing<Double>(probs, out.getDirectory(), out.isZipped());	
			} else if (current_freq_indexing instanceof Indexing) {
				Path out = ph.getFirstPathWithAttributes("zipped default probability indexing " + (i+1) + " write");
				if (out == null) {
					out = ph.getFirstPathWithAttributes("raw default probability indexing " + (i+1) + " write");
				}
				current_prob_indexing = new Indexing<Double>(probs, out.getDirectory(), out.isZipped());	
			}
			// Overwrite
			last_freq_indexing = current_freq_indexing;
			current_freq_indexing = null;
						
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			times.add(duration);
			System.out.println("Calculating probabilities for n = " + (i+1) + " took " + duration / 1000000000.0 + " s in total.");
		}	
		System.out.print("Total time for all probabilites was " + this.sum(times) / 1000000000.0 + " s in total.\n");
	}
	
	private Indexing<Integer> getFreqIndexing(int n, PathHandler ph) {
		return this.getFreqIndexing(n, ph, 1);
	}
	
	private Indexing<Integer> getFreqIndexing(int n, PathHandler ph, int threads) {
		List<List<Path>> pathlists = new ArrayList<>();
		pathlists.add(ph.getPathsWithN(n));
		pathlists.add(ph.getPathsWithType("frequency"));
		pathlists.add(ph.getPathsWithTask("read"));
		Path freq_indexing_path = ph.intersection(pathlists).get(0);
		Indexing<Integer> freq_indexing = null;
		
		// Load lexicon
		Path lex_path = ph.getFirstPathWithAttributes("zipped lexicon");
		if (lex_path == null) {
			lex_path = ph.getFirstPathWithAttributes("raw lexicon");
		}
		
		// Instantiate Indexing
		switch (freq_indexing_path.getCoding()) {
			case ("HEXADECIMAL"):
				freq_indexing = new HexadecimalIndexing<Integer>(freq_indexing_path.getDirectory(), lex_path.getDirectory(), freq_indexing_path.isZipped(), threads);
				break;
			case ("BINARY"):
				freq_indexing = new BinaryIndexing<Integer>(freq_indexing_path.getDirectory(), lex_path.getDirectory(), freq_indexing_path.isZipped(), threads);
				break;
			case ("DEFAULT"):
				freq_indexing = new Indexing<Integer>(freq_indexing_path.getDirectory(), lex_path.getDirectory(), freq_indexing_path.isZipped(), threads);
				break;
		}
		return freq_indexing;
	}
	
	public Long sum(List<Long> list) {
	     Long sum = (long) 0; 
	     for (Long l : list)
	         sum = sum + l;
	     return sum;
	}
}