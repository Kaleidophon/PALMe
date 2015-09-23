package languagemodel;

import java.util.Stack;

public class CorpusDepot {

	private Stack<String> contents = new Stack<>();
	private boolean availiable = false;
	
	public synchronized String get() {
		while (!this.isAvailable()) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		this.setAvailable(false);
		notifyAll();
		return this.contents.pop();	
	}
	
	public synchronized void add(String s) {
		while (this.isAvailable()) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		this.contents.push(s);
		this.setAvailable(true);
		notifyAll();
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
