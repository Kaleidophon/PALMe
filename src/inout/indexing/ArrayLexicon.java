package inout.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayLexicon implements Lexicon {
	
	private String[] entries;
	
	public ArrayLexicon(String[] entries) {
		this.entries = entries;
	}
	
	public ArrayLexicon(List<String> entry_list) {
		this.entries = entry_list.toArray(new String[entry_list.size()]);
	}
	
	public ArrayLexicon(ListLexicon listlex) {
		if (listlex.getLexiconSize() == 0) {
			throw new IncompleteLexiconException("Empty Lexicon");
		}
		this.entries = listlex.getEntries().toArray(new String[listlex.getLexiconSize()]);
	}
	
	public String[] getEntries() {
		return this.entries;
	}
	
	public String getValue(int key) {
		return this.entries[key];
	}
	
	public int getKey(String value) {
		return Arrays.asList(this.entries).indexOf(value);
	}
	
	public int getLexiconSize() {
		return this.entries.length;
	}
	
	public boolean containsKey(int key) {
		if (key < this.getLexiconSize()) {
			return true;
		}
		return false;
	}
	
	public boolean containsValue(String value) {
		return Arrays.asList(this.entries).contains(value);
	}
	
	public Set<Integer> keySet() {
		List<Integer> key_list = new ArrayList<>();
		for (int i = 0; i < this.getLexiconSize(); i++) {
			key_list.add(i);
		}
		return new HashSet<Integer>(key_list);
	}
	
	public Set<String> valueSet() {
		return new HashSet<String>(Arrays.asList(this.entries));
	}

}
