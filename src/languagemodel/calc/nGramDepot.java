package languagemodel.calc;

import java.util.List;
import java.util.Map;
import java.util.Stack;

class nGramDepot {

	private Stack<Map.Entry<List<Integer>, Integer>> contents = new Stack<>();
	private boolean available = false;
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
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
	
	public synchronized boolean hasLeft() {
		return !this.contents.empty();
	}
}