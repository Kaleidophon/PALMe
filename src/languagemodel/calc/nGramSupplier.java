package languagemodel.calc;

import java.util.List;
import java.util.Map;

import inout.indexing.Indexing;

public class nGramSupplier extends Thread {

	private nGramDepot ngd;
	private Indexing<Integer> current_indexing;
	
	public nGramSupplier(Indexing<Integer> current_indexing, nGramDepot ngd) {
		this.current_indexing = current_indexing;
		this.ngd = ngd;
		start();
	}
	
	public void run() {
		Map<List<Integer>, Integer> freqs = (Map<List<Integer>, Integer>) this.current_indexing.getIndices();
		for (Map.Entry<List<Integer>, Integer> entry : freqs.entrySet()) {
			this.getNGramDepot().add(entry);
		}
	}
	
	public nGramDepot getNGramDepot() {
		return this.ngd;
	}
	
	public long getID() {
		return Thread.currentThread().getId();
	}
}

