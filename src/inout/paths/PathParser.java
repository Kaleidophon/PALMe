package inout.paths;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.IOException;
import java.util.regex.*;

import custom_exceptions.IOModeException;
import custom_exceptions.XMLParseException;
import inout.general.IO;

public class PathParser {
	
	private String PATHFILE_INPATH;
	private IO reader;
	private List<Path> paths;
	private String[] keywords = {"path", "type", "subtype"};
	
	public PathParser(String PATHFILE_INPATH) {
		this.PATHFILE_INPATH = PATHFILE_INPATH;
		this.reader = new IO(PATHFILE_INPATH, "out");
		try {
			this.paths = this.parsePaths();
		}
		catch (XMLParseException xpe) {
			xpe.printStackTrace();
		}
	}
	
	public List<Path> getPaths() {
		List<Path> paths = new ArrayList<>();
		return paths;
	}
	
	private List<Path> parsePaths() throws XMLParseException {
		List<Path> paths = new ArrayList<>();
		try {
			// Preparations
			List<String> openNodes = new LinkedList<>();
			String line = this.reader.next().trim();
			while(line != null) {
				System.out.println(line);
				if (line.startsWith("<?xml version=")) {
					line = this.reader.next().trim();
					continue;
				}
				// Actual Parsing
				if (this.countOccurrences(line, "<") != this.countOccurrences(line, ">")) {
					throw new XMLParseException("Incomplete tags in document.");
				}
				System.out.println("" + this.countOccurrencesOfPattern(line, "<.*?>"));
				
				// Preparation for next iteration
				line = this.reader.next().trim();
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (IOModeException iome) {
			iome.printStackTrace();
		}
		return paths;
	}
	
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}
	
	private int countOccurrences(String s, String target) {
		return (s.length() - s.replace(target, "").length()) / target.length();
	}
	
	private int countOccurrencesOfPattern(String s, String pattern) {
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(s);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}
}
