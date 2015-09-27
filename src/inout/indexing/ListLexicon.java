package inout.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @deprecated Lexicon class for index-generation in {@link Indexing}.
 * It's basically a very simple kind of Map, because the a index with is always incremented by one corresponds to a value.
 * Was deprecated because - even though it is super simple - hashing with BiMap is so much faster!
 * 
 * @author Dennis Ulmer
 */
public class ListLexicon implements Lexicon, Iterable {
	
	private List<String> entries;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/**
	 * Constructor to create an instance from a List.
	 * 
	 * @param entry_list List with lexicon entries.
	 */
	public ListLexicon(List<String> entries) {
		this.entries = entries;
	}
	
	/**
	 * Constructor to convert a {@link ListLexicon} into an ArrayLexicon.
	 * 
	 * @param listlex {@code ListLexicon} to be converted.
	 */
	public ListLexicon(ArrayLexicon arraylex) {
		this.entries = Arrays.asList(arraylex.getEntries());
	}
	
	/** Constructor top create an empty {@code ListLexicon}.*/
	public ListLexicon() {
		this.entries = new ArrayList<>(100000);
	}
	
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Adds new entry to lexicon */
	public void addEntry(String entry) {
		this.entries.add(entry);
	}
	
	/**
	 * Adds new entry to the lexicon, checking if the key is valid.
	 * Throws @exception IllegalArgumentException if not.
	 * */
	public void put(int key, String value) {
		if (key != this.getLexiconSize()) {
			throw new IllegalArgumentException("This lexicon key cannot be set: " + key);
		} else {
			this.entries.add(value);
		}
	}
	
	/** @return Value of corresponding key */
	public String getValue(int key) {
		return this.entries.get(key);
	}
	
	/** @return Key to corresponding value. */
	public int getKey(String value) {
		return this.entries.indexOf(value);
	}
	
	/** @return Set containing all keys in this lexicon. */
	public Set<Integer> keySet() {
		List<Integer> key_list = new ArrayList<>();
		for (int i = 0; i < this.getLexiconSize(); i++) {
			key_list.add(i);
		}
		return new HashSet<Integer>(key_list);
	}
	
	/** @return Set containing all values in this lexicon. */
	public Set<String> valueSet() {
		return new HashSet<String>(this.entries);
	}
	
	/** @return Whether a key is found inside the lexicon */
	public boolean containsKey(int key) {
		if (key < this.getLexiconSize()) {
			return true;
		}
		return false;
	}
	
	/** @return Whether a value is found inside the lexicon */
	public boolean containsValue(String value) {
		return this.entries.contains(value);
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	/** @return All entries as a {@code List<String>}. */
	public List<String> getEntries() {
		return this.entries;
	}
	
	/** @return Size of Lexicon. */
	public int getLexiconSize() {
		return this.entries.size();
	}
	
	/** @return An {@code Iterator} over all of the entries. */
	public Iterator<String> iterator() {
		return this.entries.iterator();
	}
}
