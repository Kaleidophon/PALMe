package custom_exceptions;

public class UnsetPathAttributeException extends Exception {

	public UnsetPathAttributeException(String message) {
		super(message);
	}
	
	public UnsetPathAttributeException() {
		super("This Attribute isn't set, maybe this path type doesn't require it?");
	}
}
