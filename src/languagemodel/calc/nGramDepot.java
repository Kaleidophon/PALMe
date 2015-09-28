package languagemodel.calc;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Class that stores n-grams for a parallelized probability calculation by {@link MaximumFrequencyEstimation}.
 * N-grams are provided by {@link nGramSupplier} and are used by {@link nGramCalculator}.
 * 
 * @author Dennis Ulmer
 */
class nGramDepot {

	private Stack<Map.Entry<List<Integer>, Integer>> contents = new Stack<>();
	private boolean available = false;
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** @return A {@code Map.Entry} with List of IDs as key and frequency as value */
	public synchronized Map.Entry<List<Integer>, Integer> get() {
		while (!this.available) {
			try {
				wait();
	        } catch (InterruptedException ie) {}
		}
		this.available = false;
		notifyAll();
		return this.contents.pop();	
	}
	
	/** Adds a new {@code Map.Entry} to the stack. */
	public synchronized void add(Map.Entry<List<Integer>, Integer> entry) {
		while (this.available) {
			try {
				wait();
			} catch (InterruptedException ie) {}
		}
		this.contents.push(entry);
		this.available = true;
		notifyAll();
	}
	
	/** @return If stack is empty */
	public synchronized boolean hasLeft() {
		return !this.contents.empty();
	}
}