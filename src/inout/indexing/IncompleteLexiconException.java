package inout.indexing;

/**
 * Exception to be thrown while creating indices in the {@link Indexing} class
 * when a Lexicon was loaded before and an ID is being looked up that doesn't exist in the lexicon.
 * 
 * @author Dennis Ulmer
 */
public class IncompleteLexiconException extends RuntimeException {

	public IncompleteLexiconException(String message) {
		super(message);
	}
	
	public IncompleteLexiconException() {
		super("Lexicon doesn't contain all necessary keys.");
	}
}
