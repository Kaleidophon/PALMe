package languagemodel.calc;

import java.util.List;
import java.util.Map;

import inout.indexing.Indexing;

/**
 * Used in {@link MaximumFrequencyEstimation} to calculate n-gram probabilities parallelized.
 * This class acts as a producer and puts available n-grams onto a stack in {@link nGramDepot}.
 * 
 * @author Dennis Ulmer
 */
public class nGramSupplier extends Thread {

	private nGramDepot ngd;
	private Indexing<Integer> current_indexing;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/**
	 * Default constructor.
	 * 
	 * @param current_indexing Current indexing with n-grams
	 * @param ngd {@link nGramDepot}-object.
	 */
	public nGramSupplier(Indexing<Integer> current_indexing, nGramDepot ngd) {
		this.current_indexing = current_indexing;
		this.ngd = ngd;
		start();
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Method putting all n-grams onto the stack */
	public void run() {
		Map<List<Integer>, Integer> freqs = (Map<List<Integer>, Integer>) this.current_indexing.getIndices();
		for (Map.Entry<List<Integer>, Integer> entry : freqs.entrySet()) {
			this.getNGramDepot().add(entry);
		}
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** @return Used {@link nGramDepot}-object */
	public nGramDepot getNGramDepot() {
		return this.ngd;
	}
	
	/** @return Current thread ID */
	public long getID() {
		return Thread.currentThread().getId();
	}
}

