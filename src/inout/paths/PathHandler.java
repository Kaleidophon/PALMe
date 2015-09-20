package inout.paths;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PathHandler {
	
	private List<Path> paths;
	
	public PathHandler(String PATHFILE_INPATH) {
		PathParser pp = new PathParser(PATHFILE_INPATH);
		this.paths = pp.getPaths();
		try {
			for (Path p : this.paths) {
				p.checkConsistency();
			}
		} catch (IllegalArgumentException iae) {
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
			try {
				if (p.getSubtype().equals(subtype)) {
					results.add(p);
				}
			}
			catch (UnsetPathAttributeException upae) {
				continue;
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
			try {
				if (p.getCoding().equals(coding)) {
					results.add(p);
				}
			}
			catch (UnsetPathAttributeException upae) {
				continue;
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
	
	public List<Path> getPathsWithTask(String task) {
		List<Path> results = new ArrayList<>();
		for (Path p : this.paths) {
			try {
				if (p.getTask().equals(task)) {
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
	
	private Set<Path> getIntersection(Set<Path> set1, Set<Path> set2) {
	    boolean set1IsLarger = set1.size() > set2.size();
	    Set<Path> cloneSet = new HashSet<Path>(set1IsLarger ? set2 : set1);
	    cloneSet.retainAll(set1IsLarger ? set1 : set2);
	    return cloneSet;
	}
	
	public Path getFirstPathWithAttributes(String specification) {
		List<Path> paths = this.getPathsWithAttributes(specification);
		if (paths.size() == 0) {
			return null;
		}
		return this.getPathsWithAttributes(specification).get(0);
	}
	
	public List<Path> getPathsWithAttributes(String specification) {
		List<List<Path>> pathlists = new ArrayList<List<Path>>();
		String[] parts = specification.split(" ");
		if (!(parts.length == 2 || parts.length == 6)) {
			throw new IllegalArgumentException("No valid specification: " + specification);
		}
		// Verification of parts arguments
		if (parts.length == 2) {
			if (!(parts[0].equals("raw") || parts[0].equals("zipped"))) {
				throw new IllegalArgumentException("Lexicons must be either raw or zipped");
			} else if (!parts[1].equals("lexicon")) {
				throw new IllegalArgumentException("Two word specifications must be for lexicons");
			}
		} else if (parts.length == 6) {
			if (!(parts[0].equals("raw") || parts[0].equals("zipped"))) {
				throw new IllegalArgumentException("Indexings must be either raw or zipped");
			} else if (!(parts[1].equals("default") || parts[1].equals("binary") || parts[1].equals("hexadecimal"))) {
				throw new IllegalArgumentException("Specification contains invalid coding: " + parts[1]);
			} else if (!(parts[2].equals("frequency") || parts[2].equals("probability"))) {
				throw new IllegalArgumentException("Type must be either frequency or probability, " + parts[2] + " found instead");
			} else if (!parts[3].equals("indexing")) {
				throw new IllegalArgumentException("Six word specifications must be for indexings");
			} else if (Integer.parseInt(parts[4]) < 1) {
				throw new IllegalArgumentException("n must be greater / equal to 1");
			} else if (!parts[5].equals("read") && !parts[5].equals("write")) {
				throw new IllegalArgumentException("Task type must be either read oder write.");
			}
		}
		
		// Zipped or raw?
		pathlists.add(this.getPathsWithExtension((parts[0].equals("raw")) ? ".txt" : ".gz"));
		// Lexicon or Coding: default, binary or hexadecimal?
		switch (parts[1]) {
			case ("default"):
				pathlists.add(this.getPathsWithCoding("DEFAULT")); break;
			case ("binary"):
				pathlists.add(this.getPathsWithCoding("BINARY")); break;
			case ("hexadecimal"):
				pathlists.add(this.getPathsWithCoding("HEXADECIMAL")); break;
			case ("lexicon"):
				pathlists.add(this.getPathsWithType("lexicon")); break;
		}
		if (parts.length == 2) {
			return this.intersection(pathlists);
		}
		// Subype: Lexicon, Frequency or Probability?
		switch (parts[2]) {
			case ("frequency"):
				pathlists.add(this.getPathsWithType("frequency")); break;
			case ("probability"):
				pathlists.add(this.getPathsWithType("probability")); break;
		}
		if (parts.length == 6) {
			pathlists.add(this.getPathsWithSubtype(parts[3]));
			pathlists.add(this.getPathsWithN(Integer.parseInt(parts[4])));
			pathlists.add(this.getPathsWithTask(parts[5]));
		}
		return this.intersection(pathlists);
	}	
	
	public void printPaths() {
		for (Path p : this.getPaths()) {
			System.out.println(p.toString());
		}
	}	
}
