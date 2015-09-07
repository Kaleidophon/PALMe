package inout.paths;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public Path() {}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		if (!(type == "lexicon" || type == "frequency" || type == "probability")) {
			throw new IllegalArgumentException("Invalid type");
		}
		this.type = type;
	}
	
	public String getSubtype() {
		return this.subtype;
	}
	
	public void setSubtype(String subtype) {
		if (!(subtype == "raw" || subtype == "reversed" || subtype == "indexing")) {
			throw new IllegalArgumentException("Invalid subtype");
		}
		this.subtype = subtype;
	}
	
	public String getDirectory() {
		return this.directory;
	}
	
	public void setDirectory(String dir) {
		Pattern r = Pattern.compile("((\\.(\\.?))?/[a-z_\\-\\s0-9\\.]+)+\\.(txt|ser|gz)");
		Matcher m = r.matcher(dir);
		if (m.group() == null) {
			throw new IllegalArgumentException("Illegal path");
		}
		this.directory = dir;
	}
	
	public String getExtension() {
		return this.extension;
	}
	
	public void setExtension(String ext) {
		if (!(ext == ".txt" || ext == ".ser" || ext == ".gz")) {
			throw new IllegalArgumentException("Invalid file extension");
		}
		this.extension = ext;
	}
	
	public String getCoding() throws UnsetPathAttributeException {
		if (this.coding == null) {
			throw new UnsetPathAttributeException();
		}
		return this.coding;
	}
	
	public void setCoding(String coding) {
		if (!(this.contains(coding))) {
			throw new IllegalArgumentException("Invalid coding");
		}
		this.coding = coding;
	}
	
	public int getN() throws UnsetPathAttributeException {
		if (this.n == 0) {
			throw new UnsetPathAttributeException();
		}
		return this.n;
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
	
	public void checkConsistency() {
		// TO DO
	}

	@Override
	public String toString() {
		return "Type: " + this.getType() + " | Subtype: " + this.getSubtype() + " | Directory: " + this.getDirectory();
	}
}