package languagemodel.calc;

import java.util.List;
import java.util.Map;
import java.util.Stack;

class nGramDepot {

	private Stack<Map.Entry<List<Integer>, Integer>> contents = new Stack<>();
	private boolean availiable = false;
	
	public synchronized Map.Entry<List<Integer>, Integer> get() {
		return this.contents.pop();	
	}
	
	public synchronized void add(Map.Entry<List<Integer>, Integer> entry) {
		this.contents.push(entry);
	}
	
	public boolean hasLeft() {
		return !this.contents.empty();
	}
}