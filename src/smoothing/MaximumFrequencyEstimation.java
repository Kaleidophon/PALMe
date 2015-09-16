package smoothing;

import inout.indexing.Indexing;

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
		List<Map<Integer[], Double>> nGramProbabilities = new ArrayList<>();
		
		if (nGramFrequencies.size() == 1) {
			// Unigrams
			Map<Integer[], Integer> freqs = nGramFrequencies.get(0).getIndices();
			Map<Integer[], Double> probs = new HashMap<>();
			
			// Determine total
			Set<Integer> values = new HashSet<>(freqs.values());
			Iterator<Integer> iter = values.iterator();
			int total = 0;
			while (iter.hasNext()) {
				total += iter.next();
			}
			
			for (Map.Entry<Integer[], Integer> entry : freqs.entrySet()) {
				Integer[] index = entry.getKey();
				int freq = entry.getValue();
				double prob = freq*1.0 / total;
				probs.put(index, prob);
				System.out.println("Index : " + index[0] + " | Freq: " + freq + " | Prob: " + prob);
			}
			nGramProbabilities.add(probs);
			
		}
		else {
			// Bigrams and higher order
		}
		
		return nGramProbabilities_indexed;
	}
	
	
}
