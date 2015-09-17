package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

public class Toolbox {
	
	public static <K, V> HashMap<K, V> sortByValues(Map<K, V> map) { 
		List<V> list = new LinkedList(map.entrySet());

	    Collections.sort(list, new Comparator() {
	    	public int compare(Object o1, Object o2) {
	    		return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
	        }
	    });

	    HashMap<K, V> sortedHashMap = new LinkedHashMap<>();
	    for (Iterator<V> it = list.iterator(); it.hasNext();) {
	    	Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
	        sortedHashMap.put(entry.getKey(), entry.getValue());
	    } 
	    return sortedHashMap;
	}

	public static <T> String njoin(String delimiter, List<T> a) {
		StringBuilder sb = new StringBuilder();
		sb.append(a.get(0));
		for (int i = 1; i < a.size(); i++) {
			sb.append(delimiter + a.get(i));
		}
		return sb.toString();
	}
	
	public static <T> String njoin(String delimiter, T[] a) {
		StringBuilder sb = new StringBuilder();
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++) {
			sb.append(delimiter + a[i]);
		}
		return sb.toString();
	}
	
	public static <V extends Number> V intCast(Integer i) {
		return (V) i;
	}
	
	public static <V extends Number> V doubleCast(Double d)  {
		return (V) d;
	}	
	
	public static <T> void printList(List<T> l) {
		System.out.println(listToString(l));
	}
	
	public static <T> void printArray(T[] a) {
		System.out.println(arrayToString(a));
	}
	
	public static <T> String listToString(List<T> l) {
		String out = "{" + l.get(0);
		for (int i = 1; i < l.size(); i++) {
			out += ", " + l.get(i);
		}
		out += "}";
		return out;
	}
	
	public static <T> String arrayToString(T[] a) {
		String out = "{" + a[0];
		for (int i = 1; i < a.length; i++) {
			out += ", " + a[i];
		}
		out += "}";
		return out;
	}
	
	public List<Integer> pop(List<Integer> l) {
		List<Integer> clone = new ArrayList<>(l);
		clone.remove(clone.size()-1);
		return new ArrayList<>(clone);
	}
	
}
