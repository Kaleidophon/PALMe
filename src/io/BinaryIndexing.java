package io;

import java.util.HashMap;
import java.util.Map;

public class BinaryIndexing <K, V> extends Indexing {
	
	Map<String, String> indices;
	Map<String, String> lexicon;

	public BinaryIndexing(Map<String, Integer> data) {
		super(data);
	}
	
	@Override
	public void createIndices(Map<String, Integer> data) {
		Map<String, String> indices = new HashMap<>();
		Map<String, String> lexicon = new HashMap<>();
		Map<String, Integer> data_ = sortByValues(data);
		
		int index = 0;
		for (String key : data_.keySet()) {
			indices.put(Integer.toBinaryString(index), Integer.toBinaryString(data.get(key)));
			lexicon.put(Integer.toBinaryString(index), key);
			index++;
		}
		this.indices = indices;
		this.lexicon = lexicon;
	}
}
