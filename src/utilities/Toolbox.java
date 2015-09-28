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

/**
 * Class to provide some often needed methods used by other classes.
 * 
 * @author Dennis Ulmer
 */
public class Toolbox {
	
	// -------------------------------------------------- Conversions ------------------------------------------------
	
	/** Cast a generic Number type to an integer */
	public static <V extends Number> V intCast(Integer i) {
		return (V) i;
	}
	
	/** Cast a generic Number type to a double */
	public static <V extends Number> V doubleCast(Double d)  {
		return (V) d;
	}	
	
	/** Convert a string into a double */
	public static Double stringToDouble(String input, int base) {
		if (base == 10) {
			return Double.parseDouble(input);
		}
		return Double.longBitsToDouble(Long.parseLong(input, base));
	}
	
	/** Convert a double into a Hex-String */
	public static String doubleToHex(Double d) {
		return Long.toHexString(Double.doubleToRawLongBits(d));
	}
	
	/** Convert a double into a binary String */
	public static String doubleToBinary(Double d) {
		return Long.toBinaryString(Double.doubleToRawLongBits(d));
	}
	
	// -------------------------------------------------- String methods ---------------------------------------------
	
	/** Convert a {@code List} to a String. */
	public static <T> String listToString(List<T> l) {
		if (l.size() == 0) {
			return "{}";
		}
		String out = "{" + l.get(0);
		for (int i = 1; i < l.size(); i++) {
			out += ", " + l.get(i);
		}
		out += "}";
		return out;
	}
	
	/** Convert an array tp a String. */
	public static <T> String arrayToString(T[] a) {
		if (a.length == 0) {
			return "{}";
		}
		String out = "{" + a[0];
		for (int i = 1; i < a.length; i++) {
			out += ", " + a[i];
		}
		out += "}";
		return out;
	}
	
	/** Joins all elements of a {@code List} into a String, divided by a custom delimiter */
	public static <T> String njoin(String delimiter, List<T> a) {
		StringBuilder sb = new StringBuilder();
		sb.append(a.get(0));
		for (int i = 1; i < a.size(); i++) {
			sb.append(delimiter + a.get(i));
		}
		return sb.toString();
	}
	
	/** Joins all elements of an array into a String, divided by a custom delimiter */
	public static <T> String njoin(String delimiter, T[] a) {
		StringBuilder sb = new StringBuilder();
		sb.append(a[0]);
		for (int i = 1; i < a.length; i++) {
			sb.append(delimiter + a[i]);
		}
		return sb.toString();
	}
	
	// -------------------------------------------------- Screen output ----------------------------------------------
	
	/** Prints a String representation of a {@code List} */
	public static <T> void printList(List<T> l) {
		System.out.println(listToString(l));
	}
	
	/** Prints a String representation of an array */
	public static <T> void printArray(T[] a) {
		System.out.println(arrayToString(a));
	}
	
	// -------------------------------------------- Operations on data structures ------------------------------------
	
	/** Sorts a HashMap by values. */
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
	
	/** Removes the Last element from a list. */
	public static List<Integer> pop(List<Integer> l) {
		List<Integer> clone = new ArrayList<>(l);
		clone.remove(clone.size()-1);
		return new ArrayList<>(clone);
	}
	
}
