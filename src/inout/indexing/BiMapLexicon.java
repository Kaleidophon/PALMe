package inout.indexing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap; 

public class BiMapLexicon implements Lexicon, Iterable {
	
	private BiMap<Integer, String> entries;

	public BiMapLexicon(String[] entries) {}
	
	public BiMapLexicon(List<String> entry_list) {}
	
	public BiMapLexicon(ListLexicon listlex) {}
	
	public BiMapLexicon(ArrayLexicon arraylex) {}
	
	public Map<Integer, String> getEntries() {
		return this.entries;
	}
	
	public String getValue(int key) {
		return this.entries.get(key);
	}
	
	public int getKey(String value) {
		return this.entries.inverse().get(value);
	}
	
	public int getLexiconSize() {
		return this.entries.size();
	}
	
	public boolean containsKey(int key) {
		return this.entries.containsKey(key);
	}
	
	public boolean containsValue(String value) {
		return this.entries.containsValue(value);
	}
	
	public Set<Integer> keySet() {
		return this.entries.keySet();
	}
	
	public Set<String> valueSet() {
		Set<String> valueSet = new HashSet<>();
		for (Map.Entry<Integer, String> entry : this.entries.entrySet()) {
			valueSet.add(entry.getValue());
		}
		return valueSet;
	}
	
	public Iterator<String> iterator() {
		return this.valueSet().iterator();
	}
}
