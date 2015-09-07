package inout.paths;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import custom_exceptions.UnsetPathAttributeException;

public class Path {
	
	private String type;
	private String subtype;
	private String directory;
	private String extension;
	private String coding;
	private int n;
	
	private enum Codings { DEFAULT, BINARY, HEXADECIMAL }
	
	public Path(String type, String subtype, String directory, String coding) {
		this.type = type;
		this.subtype = subtype;
		this.directory = directory;
		this.coding = coding;
		this.extension = directory.substring(directory.lastIndexOf("."));
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
	
	public String getCoding() {
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
	
	public int getN() {
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
		if (this.type.equals("lexicon")) {
			if (!(this.n == 0)) {
				throw new IllegalArgumentException("Lexicon doesn't have n attribute");
			}
		}
		else if (this.type.equals("indexing")) {
			if (this.n == 0) {
				throw new IllegalArgumentException("Indexing needs an n attribute");
			}
		}
	}

	@Override
	public String toString() {
		String res = "Type: " + this.getType() + " | Subtype: " + this.getSubtype() + " | Directory: " + this.getDirectory() + " | Coding: " + this.getCoding();
		try {
			res = (this.getSubtype().equals("indexing")) ? res += " | n: " + this.getN() : res;
		}
		catch (UnsetPathAttributeException uae) {
			uae.printStackTrace();
		}
		return res;
	}
}