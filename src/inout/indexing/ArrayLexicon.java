package inout.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @deprecated Lexicon class for index-generation in {@link Indexing}.
 * It's basically a very simple kind of Map, because the a index with is always incremented by one corresponds to a value.
 * Was deprecated because - even though it is super simple - hashing with BiMap is so much faster!
 * 
 * @author Dennis Ulmer
 */
public class ArrayLexicon implements Lexicon, Iterable {
	
	private String[] entries;
	
	// ------------------------------------------------- Constructor -------------------------------------------------
	
	/**
	 * Constructor to create an instance from a String array.
	 * 
	 * @param entries String array with lexicon entries.
	 */
	public ArrayLexicon(String[] entries) {
		this.entries = entries;
	}
	
	/**
	 * Constructor to create an instance from a List.
	 * 
	 * @param entry_list List with lexicon entries.
	 */
	public ArrayLexicon(List<String> entry_list) {
		this.entries = entry_list.toArray(new String[entry_list.size()]);
	}
	
	/**
	 * Constructor to converte a {@link ListLexicon} into an ArrayLexicon.
	 * Throws @exception IncompleteLexiconException if list is empty.
	 * 
	 * @param listlex {@code ListLexicon} to be converted.
	 */
	public ArrayLexicon(ListLexicon listlex) {
		if (listlex.getLexiconSize() == 0) {
			throw new IncompleteLexiconException("Empty Lexicon");
		}
		this.entries = listlex.getEntries().toArray(new String[listlex.getLexiconSize()]);
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** @return Whether a key is found inside the lexicon */
	public boolean containsKey(int key) {
		if (key < this.getLexiconSize()) {
			return true;
		}
		return false;
	}
	
	/** @return Whether a value is found inside the lexicon */
	public boolean containsValue(String value) {
		return Arrays.asList(this.entries).contains(value);
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
		return new HashSet<String>(Arrays.asList(this.entries));
	}
	
	/** @return An {@code Iterator} over all of the entries. */
	public Iterator<String> iterator() {
		return new ArrayIterator(this.entries);
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** @return All entries as a {@code String} array. */
	public String[] getEntries() {
		return this.entries;
	}
	
	/** @return Value of corresponding key */
	public String getValue(int key) {
		return this.entries[key];
	}
	
	/** @return Key to corresponding value. */
	public int getKey(String value) {
		return Arrays.asList(this.entries).indexOf(value);
	}
	
	/** @return Size of Lexicon. */
	public int getLexiconSize() {
		return this.entries.length;
	}
	
	// ------------------------------------------------ Nested classes -----------------------------------------------
	
	/** Iterator which iterates over all entries of a {@code ArrayLexicon}. */
	public class ArrayIterator implements Iterator {
		private Object array[];
		private int pos = 0;
	
		public ArrayIterator(Object anArray[]) {
			array = anArray;
		}
	
		public boolean hasNext() {
			return pos < array.length;
		}
	
		public Object next() throws NoSuchElementException {
			if (hasNext()) {
				return array[pos++];
		    } else {
		    	throw new NoSuchElementException();
		    }
		}
	
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
