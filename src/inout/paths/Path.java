package inout.paths;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code Path} is an object create by the {@link PathParser} which stores all necessary information 
 * about an {@link Indexing} / {@link Lexicon}, which is the details about the data (frequency or probability),
 * the directory, the extension (is it an archive?), the coding (default, hexadecimal or binary) and the task
 * (is it to be read or to be written?).
 * 
 * @author Dennis Ulmer
 */
public class Path {
	
	private String type;
	private String subtype;
	private String directory;
	private String extension;
	private String coding;
	private String task;
	private int n;
	
	private enum Codings { DEFAULT, BINARY, HEXADECIMAL }
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/**
	 * Default constructor for a Indexing Path.
	 * 
	 * @param type Here equal to Indexing.
	 * @param subtype Frequency or probability
	 * @param directory Directory to file
	 * @param coding Default, hexadecimal or binary?
	 * @param task Write or Read
	 */
	public Path(String type, String subtype, String directory, String coding, String task) {
		this(type, directory);
		this.subtype = subtype;
		this.coding = coding;
		this.task = task;
	}
	
	/**
	 * Default constructor for a Lexicon Path.
	 * 
	 * @param type Here equal to Lexicon.
	 * @param directory Directory to file
	 */
	public Path(String type, String directory) {
		this.type = type;
		this.directory = directory;
		this.extension = directory.substring(directory.lastIndexOf("."));
	}
	
	/** Dummy constructor. */
	public Path() {
		this.type = null;
		this.subtype = null;
		this.directory = null;
		this.extension = null;
		this.coding = null;
		this.task = null;
		this.n = 0;
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	/** Checks integrity of the current Path */
	public void checkConsistency() {
		if (this.type.equals("lexicon")) {
			if (this.n != 0) {
				throw new IllegalArgumentException("Lexicon doesn't have n attribute");
			} else if (this.coding != null) {
				throw new IllegalArgumentException("Lexicon doesn't have a coding");
			} else if (this.subtype != null) {
				throw new IllegalArgumentException("Lexicon doesn't have a subtype");
			}
		} else if (this.type.equals("indexing")) {
			if (this.n == 0) {
				throw new IllegalArgumentException("Indexing needs an n attribute");
			} else if (!task.equals("read") && !task.equals("write")) {
				throw new IllegalArgumentException("Illegal task type: " + this.getTask());
			}
		} else if (this.type.equals("probability")) {
			if (this.n == 0) {
				throw new IllegalArgumentException("Probability needs an n attribute");
			} else if (!task.equals("read") && !task.equals("write")) {
				throw new IllegalArgumentException("Illegal task type: " + this.getTask());
			}
		}
	}
	
	/** @return If an enumeration contains a {@code String}. */
	private boolean contains(String test) {
	    for (Codings c : Codings.values()) {
	        if (c.name().equals(test)) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public String toString() {
		String res = "Type: " + this.getType();
		if (!this.getType().equals("lexicon")) {
			res += " | Subtype: indexing";
		}
		res += " | Directory: " + this.getDirectory();
		if (!this.getType().equals("lexicon")) {
			res +=" | Coding: " + this.getCoding() + " | n: " + this.getN() + " | Task: " + this.getTask();
		}
		return res;
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** @return Current {@code Path} type */
	public String getType() {
		return this.type;
	}
	
	/** Set current {@code Path} type. */
	public void setType(String type) {
		if (!(type.equals("lexicon") || type.equals("frequency") || type.equals("probability"))) {
			throw new IllegalArgumentException("Invalid type");
		}
		this.type = type;
	}
	
	/** @return Current {@code Path} subtype */
	public String getSubtype() {
		if (this.subtype == null) {
			throw new UnsetPathAttributeException();
		}
		return this.subtype;
	}
	
	/** Set current {@code Path} subtype. */
	public void setSubtype(String subtype) {
		if (!(subtype.equals("raw") || subtype.equals("indexing"))) {
			throw new IllegalArgumentException("Invalid subtype");
		} else if (this.getType().equals("lexicon")) {
			throw new IllegalArgumentException("Lexicon doesn't have a subtype");
		} else if ((this.getType().equals("frequency") || this.getType().equals("probability")) && !subtype.equals("indexing")) {
			throw new IllegalArgumentException("Frequency and Probability must have subtype indexing");
		}
		this.subtype = subtype;
	}
	
	/** @return Current {@code Path} directory */
	public String getDirectory() {
		return this.directory;
	}
	
	/** Set current {@code Path} directory. */
	public void setDirectory(String dir) {
		Pattern r = Pattern.compile("((\\.(\\.?))?/[a-z_\\-\\s0-9\\.]+)+\\.(txt|ser|gz)");
		Matcher m = r.matcher(dir);
		if (m.group() == null) {
			throw new IllegalArgumentException("Illegal path");
		}
		this.directory = dir;
	}
	
	/** @return Current {@code Path} extension */
	public String getExtension() {
		return this.extension;
	}
	
	/** Set current {@code Path} extension. */
	public void setExtension(String ext) {
		if (!(ext.equals(".txt") || ext.equals(".ser") || ext.equals(".gz"))) {
			throw new IllegalArgumentException("Invalid file extension");
		}
		this.extension = ext;
	}
	
	/** @return Current {@code Path} coding (default, binary, hexadecimal) */
	public String getCoding() {
		if (this.coding == null) {
			throw new UnsetPathAttributeException();
		}
		return this.coding;
	}
	
	/** Set current {@code Path} coding */
	public void setCoding(String coding) {
		if (!(this.contains(coding))) {
			throw new IllegalArgumentException("Invalid coding");
		}
		this.coding = coding;
	}
	
	/** @return Current {@code Path} n-gram oder */
	public int getN() {
		if (this.n == 0) {
			throw new UnsetPathAttributeException();
		}
		return this.n;
	}
	
	/** Set current {@code Path} n-gram oder */
	public void setN(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("n must be greater than / equal to 1");
		}
		this.n = n;
	}
	
	/** @return Current {@code Path} task (read or write) */
	public String getTask() {
		if (this.task == null) {
			throw new UnsetPathAttributeException();
		}
		return this.task;
	}
	
	/** Set current {@code Path} task */
	public void setTask(String task) {
		if (!(task.equals("read") || task.equals("write"))) {
			throw new IllegalArgumentException("Invalid task type");
		}
		this.task = task;
	}
	
	/** @return Whether current {@code Path} is an archive */
	public boolean isZipped() {
		return this.getExtension().equals(".gz");
	}
}