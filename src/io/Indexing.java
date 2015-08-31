package io;

import java.util.*;

public class Indexing <V extends Number> {
	Map<Integer, V> indices;
	Map<Integer, String> lexicon;
	
	public Indexing(Map<Integer, V> indices, Map<Integer, String> lexicon) {
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public Indexing(Map<String, V> data) {
		this.createIndices(data);
	}
	
	public void createIndices(Map<String, V> data) {
		Map<Integer, V> indices = new HashMap<>();
		Map<Integer, String> lexicon = new HashMap<>();
		Map<String, V> data_ = sortByValues(data);
		
		int index = 0;
		for (String key : data_.keySet()) {
			indices.put(index, data.get(key));
			lexicon.put(index, key);
			index++;
		}
		this.indices = indices;
		this.lexicon = lexicon;
	}
	
	public Map<Integer, V> getIndices() {
		return this.indices;
	}
	
	public Map<Integer, String> getLexicon() {
		return this.lexicon;
	}
	
	protected static <K, V, E> HashMap<K, V> sortByValues(Map<K, V> map) { 
		List<E> list = new LinkedList(map.entrySet());

	    Collections.sort(list, new Comparator() {
	    	public int compare(Object o1, Object o2) {
	    		return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
	        }
	    });

	    HashMap sortedHashMap = new LinkedHashMap<>();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	    	Map.Entry entry = (Map.Entry) it.next();
	        sortedHashMap.put(entry.getKey(), entry.getValue());
	    } 
	    return sortedHashMap;
	}
}
