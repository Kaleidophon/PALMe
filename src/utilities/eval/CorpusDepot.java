package utilities.eval;

import java.util.Stack;

public class CorpusDepot {

	private Stack<String> contents = new Stack<>();
	private boolean available = false;
	
	public synchronized String get() {
		while (!this.available) {
			try {
				wait();
			} catch (InterruptedException ie) {}
		}
		this.available = false;
		notifyAll();
		return this.contents.pop();	
	}
	
	public synchronized void add(String s) {
		while (this.available) {
			try {
				wait();
			} catch (InterruptedException ie) {}
		}
		this.contents.push(s);
		this.available = true;
		notifyAll();
	}
	
	public synchronized boolean hasLeft() {
		return !this.contents.empty();
	}
}
