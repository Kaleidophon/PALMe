package utilities.eval;

import java.util.Stack;

public class CorpusDepot {

	private Stack<String> contents = new Stack<>();
	private boolean availiable = false;
	
	public synchronized String get() {
		return this.contents.pop();	
	}
	
	public synchronized void add(String s) {
		this.contents.push(s);
	}
	
	public synchronized boolean hasLeft() {
		return !this.contents.empty();
	}
}
