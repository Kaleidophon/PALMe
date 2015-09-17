package smoothing;

import inout.indexing.Indexing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MaximumFrequencyEstimation implements Smoothing {

	public List<Indexing> calculateNgramProbabilities(List<Indexing> nGramFrequencies) {
		List<Indexing> nGramProbabilities_indexed = new ArrayList<>();
		List<Map<List<Integer>, Double>> nGramProbabilities = new ArrayList<>();
		
		for (int i = 0; i < nGramFrequencies.size(); i++) {
			System.out.println("Calculating Probabilites for n = " + (i+1));
			Map<List<Integer>, Integer> freqs = nGramFrequencies.get(i).getIndices();
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
				Map<List<Integer>, Integer> lower_n_freqs = nGramFrequencies.get(i-1).getIndices();

				for (Map.Entry<List<Integer>, Integer> entry : freqs.entrySet()) {
					List<Integer> index = entry.getKey();
					int freq = entry.getValue();
					List<Integer> lower_index = this.pop(index);
					int lower_freq = lower_n_freqs.get(lower_index);
					
					double prob = 0.0;
					try {
						prob = freq * 1.0 / lower_freq;
					} catch (NullPointerException npe) {
						prob = 0.0;
					}
					System.out.println("Index : " + this.lString(index) + " | Freq: " + freq + " | Prob: " + prob);
					probs.put(index, prob);
				}
			}
			nGramProbabilities.add(probs);
		}
		return nGramProbabilities_indexed;
	}
	
	private List<Integer> pop(List<Integer> l) {
		List<Integer> clone = new ArrayList<>(l);
		clone.remove(clone.size()-1);
		return new ArrayList<>(clone);
	}
	
	private <T> String lString(List<T> l) {
		String out = "{" + l.get(0);
		for (int i = 1; i < l.size(); i++) {
			out += ", " + l.get(i);
		}
		out += "}";
		return out;
	}
	
	
}
