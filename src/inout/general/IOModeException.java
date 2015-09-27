package inout.general;

import java.io.*;

public class IOModeException extends RuntimeException {
	
	public IOModeException(String message) {
		super(message);
	}
	
	public IOModeException() {
		super("IO is in wrong mode to use this method.");
	}
}
