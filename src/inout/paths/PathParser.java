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
	private String[] keywords = {"path", "type", "subtype", "directory"};
	
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
			LinkedList<String> openNodes = new LinkedList<>();
			String line = this.reader.next();
			while(this.reader.hasNext()) {
				System.out.println(line);
				if (line.startsWith("<?xml version=")) {
					line = this.reader.next();
					continue;
				}
				// Actual Parsing
				if (this.countOccurrences(line, "<") != this.countOccurrences(line, ">")) {
					throw new XMLParseException("Incomplete tags in document.");
				}
				for (String match : this.findMatches(line, "<.*?>")) {
					String current_keyword = this.getKeyword(match);
					if (!(this.contains(this.keywords, current_keyword) || this.contains(this.keywords, current_keyword.replace("/", "")))) {
						throw new XMLParseException("Invalid keyword: " + current_keyword);
					}
					
					if (current_keyword.contains("/")) {
						if (openNodes.peekLast().equals(current_keyword.replace("/", ""))) {
							switch (openNodes.removeLast()) {
								case ("path"):
									break;
								case ("type"):
									break;
								case ("subtype"):
									break;
								case ("directory"):
									break;
							}
						}
						else {
							throw new XMLParseException("Tags are not properly nested.");
						}
					}
					else {
						openNodes.add(current_keyword);
					}
					this.pAL(openNodes);
				}
				
				// Preparation for next iteration
				line = this.reader.next();
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
	
	private List<String> findMatches(String s, String pattern) {
		List<String> matches = new ArrayList<>();
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(s);
		while (m.find()) {
			matches.add(m.group());
		}
		return matches;	
	}
	
	private <T> void pAL(List<T> al) {
		if (al.size() == 0) {
			System.out.println("{}");
			return;
		}
		String out = "{" + al.get(0);
		for (int i = 1; i < al.size(); i++) {
			out += ", " + al.get(i);
		}
		out += "}";
		System.out.println(out);
	}
	
	private String getKeyword(String tag) {
		String keyword;
		if (tag.indexOf(" ") == -1) {
			keyword = tag.substring(tag.indexOf("<") + 1, tag.indexOf(">"));
		}
		else {
			keyword = tag.substring(tag.indexOf("<") + 1, tag.indexOf(" "));
		}
		return keyword;
	}
	
	private <T> boolean contains(T[] array, T target) {
		for (T element : array) {
			if (element == target || element.equals(target)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean contains(String s, String target) {
		for (int i = 0; i < s.length() - target.length() + 1; i++) {
			if (s.substring(i, i + target.length()).equals(target)) {
				return true;
			}
		}
		return false;
	}
	
	
}
