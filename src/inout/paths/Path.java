package inout.paths;

import custom_exceptions.UnsetPathAttributeException;

public class Path {
	
	String type;
	String subtype;
	String directory;
	String extension;
	String coding;
	int n;
	
	private enum Codings { DEFAULT, BINARY, HEXADECIMAL }
	
	public Path(String type, String subtype, String directory) {
		this.type = type;
		this.subtype = subtype;
		this.directory = directory;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getSubtype() {
		return this.subtype;
	}
	
	public String getDirectory() {
		return this.directory;
	}
	
	public String getExtension() {
		return this.extension;
	}
	
	public String getCoding() throws UnsetPathAttributeException {
		if (this.coding == null) {
			throw new UnsetPathAttributeException();
		}
		return this.coding;
	}
	
	public int getN() throws UnsetPathAttributeException {
		if (this.n == 0) {
			throw new UnsetPathAttributeException();
		}
		return this.n;
	}
	
	public void setExtension(String ext) {
		this.extension = ext;
	}
	
	public void setCoding(String coding) {
		if (!(this.contains(coding))) {
			throw new IllegalArgumentException("Invalid coding.");
		}
		this.coding = coding;
	}
	
	public void setN(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("n must be greater than / equal to 1");
		}
		this.n = n;
	}
	
	private boolean contains(String test) {
	    for (Codings c : Codings.values()) {
	        if (c.name().equals(test)) {
	            return true;
	        }
	    }
	    return false;
	}
}