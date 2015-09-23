package languagemodel;

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
	
	public boolean isAvailable() {
		return this.availiable;
	}
	
	public void setAvailable(boolean a) {
		this.availiable = a;
	}
	
	public boolean hasLeft() {
		return !this.contents.empty();
	}
	
	public long getID() {
		return Thread.currentThread().getId();
	}
}
