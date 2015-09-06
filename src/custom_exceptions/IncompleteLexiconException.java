package custom_exceptions;

public class IncompleteLexiconException extends Exception {

	public IncompleteLexiconException(String message) {
		super(message);
	}
	
	public IncompleteLexiconException() {
		super("Lexicon doesn't contain all necessary keys.");
	}
}
