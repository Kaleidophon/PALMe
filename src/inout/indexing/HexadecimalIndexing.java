package inout.indexing;

import java.util.List;
import java.util.Map;

/**
 * Variant of {@link Indexing} class, where the IDs of the words and the frequency are stored in hexadecimal.
 * 
 * @author Dennis Ulmer
 */
public class HexadecimalIndexing <V extends Number> extends Indexing {
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/**
	 * Default constructor: Load (frequency) data, create indices and save them.
	 * 
	 * @param data Data in form of {@code Map<Integer, Integer>} or {@code Map<Integer, Double>}
	 * @param FREQS_IN_PATH Path to (frequency) data
	 * @param LEX_IN_PATH Path to lexicon
	 */
	public HexadecimalIndexing(Map<String, V> data, String FREQS_IN_PATH, String LEX_IN_PATH) {
		super(data, FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
	}
	
	/**
	 * Constructor to load an Indexing from already computed data.
	 * 
	 * @param FREQS_IN_PATH Path to data
	 * @param LEX_IN_PATH Path to lexicon
	 * @param zipped Is the data in an archive?
	 */
	public HexadecimalIndexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped) {
		super(FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
		this.load(FREQS_IN_PATH, LEX_IN_PATH, zipped);
	}
	
	/**
	 * Constructor to load an Indexing from already computed data in a parallelized manner.
	 * 
	 * @param FREQS_IN_PATH Path to data
	 * @param LEX_IN_PATH Path to lexicon
	 * @param zipped Is the data in an archive?
	 * @param threads Number of threads to load data
	 */
	public HexadecimalIndexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped, int threads) {
		super(FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
		this.loadParallelized(FREQS_IN_PATH, LEX_IN_PATH, zipped, threads);
	}
	
	/**
	 * Constructor to dump already indexed data
	 * 
	 * @param indexed_data Data in form of {@code Map<Integer, Integer>} or {@code Map<Integer, Double>}
	 * @param FREQS_IN_PATH Path where data is to be dumped
	 * @param zipped Should the data be zipped?
	 */
	public HexadecimalIndexing(Map<List<Integer>, V> indexed_data, String FREQS_IN_PATH, boolean zipped) {
		super(indexed_data, FREQS_IN_PATH);
		this.setMode();
		this.setPrefix();
		this.dump(FREQS_IN_PATH, zipped);
	}
	
	/** Dummy constructor */
	public HexadecimalIndexing() {
		super();
		this.setPrefix();
		this.setMode();
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** Sets mode to hexadecimal for indexing writing and loading. */
	private void setMode() {
		this.mode = "hexadecimal";
	}
	
	/** Sets file prefix to hexadecimal for indexing writing. */
	private void setPrefix() {
		this.prefix = "hex_";
	}
}
