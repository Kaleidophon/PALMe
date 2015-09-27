package inout.indexing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap; 

/**
 * Lexicon based on Google's BiMap.
 * 
 * @author Dennis Ulmer
 */
public class BiMapLexicon implements Lexicon, Iterable {
	
	private BiMap<Integer, String> entries;
	
	// ------------------------------------------------- Constructor -------------------------------------------------
	
	/** Constructor to create an empty BiMapLexicon */
	public BiMapLexicon() {
		this.entries = HashBiMap.create();
	}

	/**
	 * Constructor to create an instance from a {@code String} array.
	 * 
	 * @param entries String array with lexicon entries.
	 */
	public BiMapLexicon(String[] entries) {
		this.entries = HashBiMap.create();
		for (int i = 0; i < entries.length; i++) {
			this.entries.put(i, entries[i]);
		}
	}
	
	/**
	 * Constructor to create an instance from a List.
	 * 
	 * @param entry_list List with lexicon entries.
	 */
	public BiMapLexicon(List<String> entry_list) {
		this.entries = HashBiMap.create();
		for (int i = 0; i < entry_list.size(); i++) {
			this.entries.put(i, entry_list.get(i));
		}
	}
	
	/**
	 * Constructor to convert a {@link ListLexicon} into a BiMapLexicon.
	 * 
	 * @param listlex {@code ListLexicon} to be converted.
	 */
	public BiMapLexicon(ListLexicon listlex) {
		this.entries = HashBiMap.create();
		List<String> entries = listlex.getEntries();
		for (int i = 0; i < entries.size(); i++) {
			this.entries.put(i, entries.get(i));
		}
	}
	
	/**
	 * Constructor to convert an {@link ArrayLexicon} into a BiMapLexicon.
	 * 
	 * @param listlex {@code ArrayLexicon} to be converted.
	 */
	public BiMapLexicon(ArrayLexicon arraylex) {
		this.entries = HashBiMap.create();
		String[] entries = arraylex.getEntries();
		for (int i = 0; i < entries.length; i++) {
			this.entries.put(i, entries[i]);
		}
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Adding a new Entry to a lexicon. */
	public void put(int key, String value) {
		this.entries.put(key, value);
	}
	
	/** @return Value of corresponding key */
	public String getValue(int key) {
		return this.entries.get(key);
	}
	
	/** @return Key to corresponding value. */
	public int getKey(String value) {
		return this.entries.inverse().get(value);
	}
	
	/** @return Whether a key is found inside the lexicon */
	public boolean containsKey(int key) {
		return this.entries.containsKey(key);
	}
	
	/** @return Whether a value is found inside the lexicon */
	public boolean containsValue(String value) {
		return this.entries.containsValue(value);
	}
	
	/** @return Set containing all keys in this lexicon. */
	public Set<Integer> keySet() {
		return this.entries.keySet();
	}
	
	/** @return Set containing all values in this lexicon. */
	public Set<String> valueSet() {
		Set<String> valueSet = new HashSet<>();
		for (Map.Entry<Integer, String> entry : this.entries.entrySet()) {
			valueSet.add(entry.getValue());
		}
		return valueSet;
	}
	
	/** @return An {@code Iterator} over all of the entries. */
	public Iterator<String> iterator() {
		return this.valueSet().iterator();
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	/** @return All entries as a {@code String} array. */
	public Map<Integer, String> getEntries() {
		return this.entries;
	}
	
	/** @return Size of Lexicon. */
	public int getLexiconSize() {
		return this.entries.size();
	}
}
