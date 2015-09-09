package inout.paths;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import custom_exceptions.UnsetPathAttributeException;

public class PathHandler {
	
	private List<Path> paths;
	
	private List<Set<String>> allowed_arguments;
	private Set<String> format = new HashSet(Arrays.asList(new String[]{"raw", "zipped"}));
	private Set<String> coding = new HashSet(Arrays.asList(new String[]{"default", "binary", "hexadecimal"}));
	private Set<String> type = new HashSet(Arrays.asList(new String[]{"lexicon", "frequency", "probability"}));
	private Set<String> subtype = new HashSet(Arrays.asList(new String[]{"reversed", "indexing"}));
	
	public PathHandler(String PATHFILE_INPATH) {
		PathParser pp = new PathParser(PATHFILE_INPATH);
		this.paths = pp.getPaths();
		try {
			for (Path p : this.paths) {
				p.checkConsistency();
			}
		}
		catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		}
	}
	
	public List<Path> getPaths() {
		return this.paths;
	}
	
	public List<Path> getPathsWithType(String type) {
		List<Path> results = new ArrayList<>();
		for (Path p : this.paths) {
			if (p.getType().equals(type)) {
				results.add(p);
			}
		}
		return results;
	}
	
	public List<Path> getPathsWithSubtype(String subtype) {
		List<Path> results = new ArrayList<>();
		for (Path p : this.paths) {
			if (p.getSubtype().equals(subtype)) {
				results.add(p);
			}
		}
		return results;
	}
	
	public List<Path> getPathsWithDirectory(String dir) {
		List<Path> results = new ArrayList<>();
		for (Path p : this.paths) {
			if (p.getDirectory().equals(dir)) {
				results.add(p);
			}
		}
		return results;
	}
	
	public List<Path> getPathsWithCoding(String coding) {
		List<Path> results = new ArrayList<>();
		for (Path p : this.paths) {
			if (p.getCoding().equals(coding)) {
				results.add(p);
			}
		}
		return results;
	}
	
	public List<Path> getPathsWithExtension(String ext) {
		List<Path> results = new ArrayList<>();
		for (Path p : this.paths) {
			if (p.getExtension().equals(ext)) {
				results.add(p);
			}
		}
		return results;
	}
	
	public List<Path> getPathsWithN(int n) {
		List<Path> results = new ArrayList<>();
		for (Path p : this.paths) {
			try { 
				if (p.getN() == n) {
					results.add(p);
				}
			}
			catch (UnsetPathAttributeException upae) {
				continue;
			}
		}
		return results;
	}
	
	public List<Path> intersection(List<List<Path>> pathlists) {
		Set<Path> intersection = new HashSet<>(pathlists.get(0));

		for (int i = 1; i < pathlists.size(); i++) {
			intersection =  this.getIntersection(intersection, new HashSet<Path>(pathlists.get(i)));
		}
		return new ArrayList<Path>(intersection);
	}
	
	public Set<Path> getIntersection(Set<Path> set1, Set<Path> set2) {
	    boolean set1IsLarger = set1.size() > set2.size();
	    Set<Path> cloneSet = new HashSet<Path>(set1IsLarger ? set2 : set1);
	    cloneSet.retainAll(set1IsLarger ? set1 : set2);
	    return cloneSet;
	}
	
	public List<Path> getPathsWithAttributes(String specification) {
		List<List<Path>> pathlists = new ArrayList<List<Path>>();
		String[] parts = specification.split(" ");
		if (!(parts.length == 4 || parts.length == 5)) {
			throw new IllegalArgumentException("No valid specification: " + specification);
		}
		// Verification of parts arguments
		this.allowed_arguments = new ArrayList<>();
		this.allowed_arguments.add(this.format);
		this.allowed_arguments.add(this.coding);
		this.allowed_arguments.add(this.type);
		this.allowed_arguments.add(this.subtype);
		for (int i = 0; i < parts.length; i++) {
			if (i < 4) {
				if (!this.allowed_arguments.get(i).contains(parts[i])) {
					throw new IllegalArgumentException("Illegal argument: " + parts[i]);
				}
			}
			else {
				if (Integer.parseInt(parts[i]) <= 0) {
					throw new IllegalArgumentException("Illegal argument: " + parts[i]);
				}
			}
		}
		
		// Zipped or raw?
		pathlists.add(this.getPathsWithExtension((parts[0].equals("raw")) ? ".txt" : ".gz"));
		// Coding: None, binary or hexadecimal?
		switch (parts[1]) {
			case ("default"):
				pathlists.add(this.getPathsWithCoding("DEFAULT")); break;
			case ("binary"):
				pathlists.add(this.getPathsWithCoding("BINARY")); break;
			case ("hexadecimal"):
				pathlists.add(this.getPathsWithCoding("HEXADECIMAL")); break;
		}
		// Type: Lexicon, Frequency or Probability?
		switch (parts[2]) {
			case ("lexicon"):
				pathlists.add(this.getPathsWithType("lexicon")); break;
			case ("frequency"):
				pathlists.add(this.getPathsWithType("frequency")); break;
			case ("probability"):
				pathlists.add(this.getPathsWithType("probability")); break;
		}
		// Subtype: Reversed or indexing?
		pathlists.add(this.getPathsWithSubtype(parts[3]));
		// Optional for indexings: n = ?
		if (parts.length == 5) {
			pathlists.add(this.getPathsWithN(Integer.parseInt(parts[4])));
		}

		return this.intersection(pathlists);
	}
	
	
	
	
	
	
}
