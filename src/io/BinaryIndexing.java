package io;

import java.util.HashMap;
import java.util.Map;

public class BinaryIndexing extends Indexing {
	
	Map<String, String> binary_indices;
	Map<String, String> binary_lexicon;

	public BinaryIndexing(Map<String, Integer> data) {
		this.createBinaryIndices(data);
	}
	
	public void createBinaryIndices(Map<String, Integer> data) {
		Map<String, String> indices = new HashMap<>();
		Map<String, String> lexicon = new HashMap<>();
		Map<String, Integer> data_ = sortByValues(data);
		
		int index = 0;
		for (String key : data_.keySet()) {
			indices.put(Integer.toBinaryString(index), Integer.toBinaryString(data.get(key)));
			lexicon.put(Integer.toBinaryString(index), key);
			index++;
		}
		
		this.binary_indices = indices;
		this.binary_lexicon = lexicon;
	}
	
	public Map<String, String> getIndices() {
		return this.binary_indices;
	}
	
	public Map<String, String> getLexicon() {
		return this.binary_lexicon;
	}
}
