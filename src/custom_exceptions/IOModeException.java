package custom_exceptions;

import java.io.*;

public class IOModeException extends Exception{
	
	public IOModeException(String message) {
		super(message);
	}
	
	public IOModeException() {
		super("IO is in wrong mode to use this method.");
	}
}
