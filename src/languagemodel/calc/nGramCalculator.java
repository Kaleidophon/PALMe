package languagemodel.calc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilities.Toolbox;
import inout.indexing.Indexing;

public class nGramCalculator extends Thread {

	int n;
	int total; // for unigrams
	boolean running;
	Indexing<Integer> last_indexing;
	nGramDepot ngd;
	Map<List<Integer>, Double> probs;
	
	public nGramCalculator(int n, Indexing<Integer> last_indexing, nGramDepot ngd) {
		this.n = n;
		this.last_indexing = last_indexing;
		this.ngd = ngd;
		this.probs = new HashMap<>();
		start();
	}
	
	public nGramCalculator(int n, Indexing<Integer> last_indexing, nGramDepot ngd, int total) {
		this.total = total;
		this.n = n;
		this.last_indexing = last_indexing;
		this.ngd = ngd;
		this.probs = new HashMap<>();
		start();
	}
	
	public void run() {
		this.setRunning(true);
		while (this.ngd.hasLeft()) {
			if (this.getN() == 0) {
				// Unigrams
				while (this.ngd.hasLeft()) {
					Map.Entry<List<Integer>, Integer> entry = this.ngd.get();
					int freq = entry.getValue();
					List<Integer> index = entry.getKey();
					double prob = freq * 1.0 / this.total;
					this.probs.put(entry.getKey(), prob);
					//System.out.println("Calculator #" + this.getID() + " | Index : " + index.get(0) + " | Freq: " + freq + " | Prob: " + prob);
				}
			} else {
				// Higher order n-grams
				Map<List<Integer>, Integer> lower_n_freqs = (Map<List<Integer>, Integer>) this.last_indexing.getIndices();
				while (this.ngd.hasLeft()) {
					Map.Entry<List<Integer>, Integer> entry = this.ngd.get();
					int freq = entry.getValue();
					List<Integer> index = entry.getKey();
					List<Integer> lower_index = Toolbox.pop(index);
					int lower_freq = lower_n_freqs.get(lower_index);
					
					double prob = 0.0;
					try {
						prob = freq * 1.0 / lower_freq;
					} catch (NullPointerException npe) {
						prob = 0.0;
					}
					//System.out.println("Calculator #" + this.getID() + " | Index : " + index.get(0) + " | Freq: " + freq + " | Prob: " + prob);
					this.probs.put(index, prob);
				}
			}
		}
		this.setRunning(false);
	}
	
	public void setRunning(boolean r) {
		this.running = r;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public int getN() {
		return this.n;
	}
	
	public Map<List<Integer>, Double> getProbs() {
		return this.probs;
	}
	
	public long getID() {
		return Thread.currentThread().getId();
	}
}
