package utilities.eval;

import java.util.Stack;

/**
 * Class used in {@link Evaluation} to evaluate a {@link LanguageModel} parallelized with a corpus.
 * Lines ready for evaluation are then stored with this class in a stack by a {@link CorpusReader}, ready to be
 * picked up by a {@link CorpusEvaluator}.
 * 
 * @author Dennis Ulmer
 */
public class CorpusDepot {

	private Stack<String> contents = new Stack<>();
	private boolean available = false;
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** @return a line from the stack */
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
	
	/** Adds a new line to the stack */
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
	
	/** @return Whether the stack is empty */
	public synchronized boolean hasLeft() {
		return !this.contents.empty();
	}
}
