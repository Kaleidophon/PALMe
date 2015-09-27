package languagemodel.calc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utilities.Toolbox;

public class MapTreeNode<K extends List, V> {
	
	K key;
	V value;
	List<MapTreeNode<K, V>> children;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	public MapTreeNode(K key, V value) {
		this.key = key;
		this.value = value;
		this.children = new ArrayList<>();
	}
	
	public MapTreeNode() {
		// Root
		this.key = (K) new ArrayList<>();
		this.children = new ArrayList<>();
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	public void add(MapTreeNode<K, V> node) {
		System.out.println("Adding " + Toolbox.listToString(node.getKey()) + "...");
		System.out.println("Comparing " + Toolbox.listToString(node.getKey()) + " to " + Toolbox.listToString(this.getKey()));
		if (node.getKey().size() - 1 != this.getKey().size()) {
			System.out.println("We have to go deeper!");
			this.getChildWithKey((K) node.getKey().subList(0, this.getKey().size() + 1)).add(node);
		} else {
			System.out.println("Added successfully.");
			this.children.add(node);
		}
	}
	
	public V getValue() {
		return this.value;
	}
	
	public K getKey() {
		return this.key;
	}
	
	public MapTreeNode<K, V> getChildWithKey(K key) {
		for (MapTreeNode<K, V> child : this.getChildren()) {
			if (child.getKey().equals(key)) {
				return child;
			}
		}
		return null;
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	public List<MapTreeNode<K, V>> getChildren() {
		return this.children;
	}
	
	public int numberOfChildren() {
		return this.children.size();
	}
	
	public Set<K> childrenKeySet() {
		Set<K> keys = new HashSet<>();
		if (this.numberOfChildren() == 0) {
			return null;
		}
		for(MapTreeNode<K, V> child : this.getChildren()) {
			keys.add(child.getKey());
			Set<K> children_of_child_keys = child.childrenKeySet();
			if (children_of_child_keys != null) {
				for (K key : children_of_child_keys) {
					keys.add(key);
				}
			}
		}
		return keys;
	}
}