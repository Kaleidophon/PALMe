package languagemodel.calc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utilities.Toolbox;

/**
 * @deprecated Class used to parallelize the calculation of n-gram probabilities in {@link MaximumFrequencyEstimation}.
 * The idea was to build up a tree, where the depth of a node corresponds to the length of a n-gram. The children
 * of a node thereby share the beginning of their IDs (= words). A tree would then look something like this:
 * <p>
 * 			dog					       house		
 * 		   /   \			          /     \
 *        /		\			         /		 \
 * 	   dog is  dog barks        house on	 house of
 * 	    ...    /        \          ...         ...
 *            /          \                
 *         dog barks at  ....
 * <p>
 * Then, every (pseudo-)root like "dog" or "house" could be assigned to thread, which moves through each subtree and computes
 * every n-gram probability based on the n-gram probability of the parent node. Every node stores the ID and frequency of a phrase.
 * <p>
 * Why doesn't it work?
 * Basically, constructing the tree is madness. Let's assume that adding a new child to a node is an operation with cost 1.
 * For unigrams, you can add each node directly, so O(n) = |V_uni|.
 * But for bigrams, you have to find the right node, so it's O(n) = 2 * |V_bi| (because you have to make two comparisons).
 * Assume that the maximal number of n-grams is the number of (n-1)-grams^2, you have to do |V| + |V|^2 + |V|^3 + ... operations for trigrams and so on.
 * Although the number is lesser in reality, constructing the trees comes at a high cost which outweighs any benefits that could
 * come off parallelization.
 * <p>
 * Maybe it would be feasible with a parallelized construction of the tree on a powerful machine, but that couldn't be tested.
 * 
 * @author Dennis Ulmer
 */
public class MapTreeNode<K extends List, V> {
	
	K key;
	V value;
	List<MapTreeNode<K, V>> children;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/**
	 * Default constructor for a new node with key and value.
	 * 
	 * @param key List of word IDs
	 * @param value Frequency
	 */
	public MapTreeNode(K key, V value) {
		this.key = key;
		this.value = value;
		this.children = new ArrayList<>();
	}
	
	/** Real empty rootnode. */
	public MapTreeNode() {
		// Root
		this.key = (K) new ArrayList<>();
		this.children = new ArrayList<>();
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Adds a new node into the tree */
	public void add(MapTreeNode<K, V> node) {
		//System.out.println("Adding " + Toolbox.listToString(node.getKey()) + "...");
		//System.out.println("Comparing " + Toolbox.listToString(node.getKey()) + " to " + Toolbox.listToString(this.getKey()));
		if (node.getKey().size() - 1 != this.getKey().size()) {
			//System.out.println("We have to go deeper!");
			this.getChildWithKey((K) node.getKey().subList(0, this.getKey().size() + 1)).add(node);
		} else {
			//System.out.println("Added successfully.");
			this.children.add(node);
		}
	}
	
	/** @return Value of this node */
	public V getValue() {
		return this.value;
	}
	
	/** @return Key of this node */
	public K getKey() {
		return this.key;
	}
	
	/** @return the child of this node with a speific key */
	public MapTreeNode<K, V> getChildWithKey(K key) {
		for (MapTreeNode<K, V> child : this.getChildren()) {
			if (child.getKey().equals(key)) {
				return child;
			}
		}
		return null;
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	/** @return all children of this node as a {@code List} */
	public List<MapTreeNode<K, V>> getChildren() {
		return this.children;
	}
	
	/** @return Number of children of this node */
	public int numberOfChildren() {
		return this.children.size();
	}
	
	/** @return Set of all this node's children's keys */
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