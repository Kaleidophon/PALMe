package inout.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ListLexicon implements Lexicon, Iterable {
	
	private List<String> entries;
	
	public ListLexicon(List<String> entries) {
		this.entries = entries;
	}
	
	public ListLexicon(ArrayLexicon arraylex) {
		this.entries = Arrays.asList(arraylex.getEntries());
	}
	
	public ListLexicon() {
		this.entries = new ArrayList<>(100000);
	}
	
	public List<String> getEntries() {
		return this.entries;
	}
	
	public void addEntry(String entry) {
		this.entries.add(entry);
	}
	
	public String getValue(int key) {
		return this.entries.get(key);
	}
	
	public int getKey(String value) {
		return this.entries.indexOf(value);
	}
	
	public int getLexiconSize() {
		return this.entries.size();
	}
	
	public boolean containsKey(int key) {
		if (key < this.getLexiconSize()) {
			return true;
		}
		return false;
	}
	
	public boolean containsValue(String value) {
		return this.entries.contains(value);
	}
	
	public Set<Integer> keySet() {
		List<Integer> key_list = new ArrayList<>();
		for (int i = 0; i < this.getLexiconSize(); i++) {
			key_list.add(i);
		}
		return new HashSet<Integer>(key_list);
	}
	
	public Set<String> valueSet() {
		return new HashSet<String>(this.entries);
	}
	
	public void put(int key, String value) {
		if (key != this.getLexiconSize()) {
			throw new IllegalArgumentException("This lexicon key cannot be set: " + key);
		} else {
			this.entries.add(value);
		}
	}
	
	public Iterator<String> iterator() {
		return this.entries.iterator();
	}
}
