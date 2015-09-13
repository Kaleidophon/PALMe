package inout.indexing;

import java.util.List;
import java.util.Set;

public interface Lexicon <T> {
	
	public T getEntries();
	public String getValue(int key);
	public int getKey(String value);
	public int getLexiconSize();
	public boolean containsKey(int key);
	public boolean containsValue(String value);
	public Set<Integer> keySet();
	public Set<String> valueSet();
}
