package inout.paths;

public class UnsetPathAttributeException extends RuntimeException {

	/** Constructor with custom message */
	public UnsetPathAttributeException(String message) {
		super(message);
	}
	
	/** Constructor with default message */
	public UnsetPathAttributeException() {
		super("This Attribute isn't set, maybe this path type doesn't require it?");
	}
}
