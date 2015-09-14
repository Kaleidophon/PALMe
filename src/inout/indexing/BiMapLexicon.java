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
	
	public BiMapLexicon() {
		this.entries = HashBiMap.create();
	}

	public BiMapLexicon(String[] entries) {
		this.entries = HashBiMap.create();
		for (int i = 0; i < entries.length; i++) {
			this.entries.put(i, entries[i]);
		}
	}
	
	public BiMapLexicon(List<String> entry_list) {
		this.entries = HashBiMap.create();
		for (int i = 0; i < entry_list.size(); i++) {
			this.entries.put(i, entry_list.get(i));
		}
	}
	
	public BiMapLexicon(ListLexicon listlex) {
		this.entries = HashBiMap.create();
		List<String> entries = listlex.getEntries();
		for (int i = 0; i < entries.size(); i++) {
			this.entries.put(i, entries.get(i));
		}
	}
	
	public BiMapLexicon(ArrayLexicon arraylex) {
		this.entries = HashBiMap.create();
		String[] entries = arraylex.getEntries();
		for (int i = 0; i < entries.length; i++) {
			this.entries.put(i, entries[i]);
		}
	}
	
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
	
	public void put(int key, String value) {
		this.entries.put(key, value);
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
