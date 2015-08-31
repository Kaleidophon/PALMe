package io;

import java.util.Map;

public class Indexing {
	Map<Integer, Integer> indices;
	Map<Integer, String> lexicon;
	
	public Indexing(Map<Integer, Integer> indices, Map<Integer, String> lexicon) {
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public Map<Integer, Integer> getIndices() {
		return this.indices;
	}
	public Map<Integer, String> getLexicon() {
		return this.lexicon;
	}
}
