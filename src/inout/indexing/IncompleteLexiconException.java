package inout.indexing;

public class IncompleteLexiconException extends RuntimeException {

	public IncompleteLexiconException(String message) {
		super(message);
	}
	
	public IncompleteLexiconException() {
		super("Lexicon doesn't contain all necessary keys.");
	}
}
