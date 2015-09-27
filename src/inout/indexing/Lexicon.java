package inout.indexing;

import java.util.Set;

public interface Lexicon <T> extends Iterable {
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	public String getValue(int key);
	public int getKey(String value);
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	public T getEntries();
	public int getLexiconSize();
	public boolean containsKey(int key);
	public boolean containsValue(String value);
	public Set<Integer> keySet();
	public Set<String> valueSet();
}
