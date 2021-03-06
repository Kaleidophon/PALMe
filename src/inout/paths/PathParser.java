package inout.paths;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;

import inout.general.IO;
import inout.general.IOModeException;

/**
 * Parses a {@code .xml}-file with {@link Path}-descriptions into {@link Path} objects.
 * 
 * @author Dennis Ulmer
 */
public class PathParser {
	
	private IO reader;
	private List<Path> paths;
	private Set<String> demanding_keywords = new HashSet<>(Arrays.asList(new String[]{"type", "subtype", "directory"}));
	private Set<String> non_demanding_keywords = new HashSet<>(Arrays.asList(new String[]{"path"}));
	private Set<String> keywords;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/**
	 * Default constructor.
	 * 
	 * @param PATHFILE_INPATH Path to .xml-file.
	 */
	public PathParser(String PATHFILE_INPATH) {
		this.keywords = new HashSet<>(this.demanding_keywords);
		this.keywords.addAll(this.non_demanding_keywords);
		this.reader = new IO(PATHFILE_INPATH, "out");
		try {
			this.paths = this.parsePaths();
		} catch (XMLParseException xpe) {
			xpe.printStackTrace();
		}
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Parses the {@code .xml}-file.
	 *  @return {@code List} containing {@link Path}-objects.
	 */
	private List<Path> parsePaths() throws XMLParseException {
		List<Path> paths = new ArrayList<>();
		try {
			// Preparations
			LinkedList<String> openNodes = new LinkedList<>();
			
			// Possible Variables
			Path path;
			String type = "";
			String subtype = "";
			String directory = "";
			String coding = "";
			String task = "";
			int n = 0;
			String enclosed_text = "";
			
			do {
				String line = this.reader.next();
				// Ignore header
				if (line.startsWith("<?xml version=")) {
					continue;
				}
				// Actual Parsing
				if (this.countOccurrences(line, "<") != this.countOccurrences(line, ">")) {
					throw new XMLParseException("Incomplete tags in document.");
				}
				List<String> tags = this.findMatches(line, "<.*?>");
				for (String current_tag : tags) {
					String current_keyword = this.getKeyword(current_tag);
					
					if (!(this.keywords.contains(current_keyword) || this.keywords.contains(current_keyword.replace("/", "")))) {
						throw new XMLParseException("Invalid keyword: " + current_keyword);
					}
					
					if (this.contains(current_tag, "/")) {
						// Closing tag
						if (this.demanding_keywords.contains(current_keyword.replace("/", "")) && enclosed_text.equals("")) {
							// Extract text
							enclosed_text = line.substring(0, line.indexOf(current_tag)).trim();
							line = line.replaceFirst(enclosed_text, "");
						}
						
						// Preparing wrapping up
						String last_tag = openNodes.removeLast();
						String last_keyword = this.getKeyword(last_tag);
						
						if (!(last_keyword.equals(current_keyword.replace("/", "")))) {
							throw new XMLParseException("Tags are not properly nested.");
						}
						
						if (last_keyword.equals("path")) {
							path = (subtype.equals("")) ? new Path(type, directory) : new Path(type, subtype, directory, coding, task);
							if (subtype.equals("indexing")) {
								path.setN(n);
								n = 0;
							}
							paths.add(path);
							// Reset variables
							path = null;
							type = "";
							subtype = "";
							directory = "";
							coding = "";
							task = "";
							enclosed_text = "";
						} else if (last_keyword.equals("type")) {
							type = enclosed_text; 
							enclosed_text = "";
						} else if (last_keyword.equals("subtype")) {
							// Dealing with attributes
							if (this.hasAttributes(last_tag)) {
								Map<String, String> attributes = this.extractAttributes(last_tag);
								if (attributes.containsKey("n")) {
									n = Integer.parseInt(attributes.get("n"));
								}
								if (attributes.containsKey("coding")) {
									coding = attributes.get("coding");
								}
							}
							subtype = enclosed_text;
							enclosed_text = "";
						} else if (last_keyword.equals("directory")) {
							if (this.hasAttributes(last_tag)) {
								Map<String, String> attributes = this.extractAttributes(last_tag);
								if (attributes.containsKey("task")) {
									task = attributes.get("task");
								}
							}
							directory = enclosed_text;
							enclosed_text = "";
						}
					} else {
						// Opening tag
						openNodes.add(current_tag);
					}
					// Reduce line
					line = line.replaceFirst(current_tag, "");
				}
				if (line.trim().length() > 0) {
					enclosed_text = line.trim();
				}	
			} while(this.reader.hasNext());
		} catch (IOModeException iome) {
			iome.printStackTrace();
		}
		return paths;
	}
	
	/** Extracts attribues from a {@code .xml} tag */
	private Map<String, String> extractAttributes(String tag) {
		tag = tag.replace("<", "").replace(">", "").replace("\"", "");
		Map<String, String> attributes = new HashMap<>();
		String[] parts = tag.split(" ");
		for (String part : parts) {
			if (this.contains(part, "=")) {
				attributes.put(part.split("=")[0], part.split("=")[1]);
			}
		}
		return attributes;
	}
	
	// ---------------------------------------------- Additional  methods --------------------------------------------
	
	/** Counts occurrences of a {@code String} within another {@code String}. */
	private int countOccurrences(String s, String target) {
		return (s.length() - s.replace(target, "").length()) / target.length();
	}
	
	/** Finds all matches of a {@code regex} pattern within a {@code String} */
	private List<String> findMatches(String s, String pattern) {
		List<String> matches = new ArrayList<>();
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(s);
		while (m.find()) {
			matches.add(m.group());
		}
		return matches;	
	}
	
	/** @return Whether a {@code String} contains another {@code String} */
	private boolean contains(String s, String target) {
		for (int i = 0; i < s.length() - target.length() + 1; i++) {
			if (s.substring(i, i + target.length()).equals(target)) {
				return true;
			}
		}
		return false;
	}
	
	/** @return If a {@code .xml}-tag has attributes */
	private boolean hasAttributes(String tag) {
		return this.contains(tag, "=");
	}
	
	/** @return The keyword of {@code .xml}-tag */
	private String getKeyword(String tag) {
		String keyword;
		if (tag.indexOf(" ") == -1) {
			keyword = tag.substring(tag.indexOf("<") + 1, tag.indexOf(">"));
		} else {
			keyword = tag.substring(tag.indexOf("<") + 1, tag.indexOf(" "));
		}
		return keyword;
	}

	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** Sets keywords to be recognized by the {@code PathParser}. */
	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}
	
	/** Sets the demanding keywords to be recognized by the {@code PathParser}. */
	public void setDemandingKeywords(Set<String> keywords) {
		this.demanding_keywords = keywords;
	}
	
	/** Sets the non-demanding keywords to be recognized by the {@code PathParser}. */
	public void setNonDemandingKeywords(Set<String> keywords) {
		this.non_demanding_keywords = keywords;
	}
	
	/** @return A list of all parsed {@link Path}-objects. */
	public List<Path> getPaths() {
		return this.paths;
	}
}
