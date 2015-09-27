package inout.general;

/**
 * Exception to throw if {@link IO} is in a wrong mode (e.g. it's reading while in writing mode).
 * 
 * @author Dennis Ulmer
 */
public class IOModeException extends RuntimeException {
	
	/**
	 * Constructor with custom message.
	 * 
	 * @param message Custom message.
	 */
	public IOModeException(String message) {
		super(message);
	}
	
	/** Constructor with default message */
	public IOModeException() {
		super("IO is in wrong mode to use this method.");
	}
}
