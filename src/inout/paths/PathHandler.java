package inout.paths;

import java.util.List;
import java.util.ArrayList;
import custom_exceptions.UnsetPathAttributeException;

public class PathHandler {
	
	private List<Path> paths;
	
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
	
	
	
	
	
	
}
