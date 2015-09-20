package languagemodel;

import smoothing.*;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.IllegalArgumentException;

public class LanguageModel {
	
	private final int n;
	private int mode;
	private Smoothing smoothing;
	private boolean validateState;
	private PathHandler ph;
	private String IN_PATH;
	
	public LanguageModel(int n, String IN_PATH, Smoothing smoothing) {
		this.n = n;
		this.IN_PATH = IN_PATH;
		this.mode = mode;
		this.checkIntegrity();
		this.ph = new PathHandler(IN_PATH);
		
		List<Path> paths = this.ph.getPaths();
		for (Path p : paths) {
			System.out.println(p.toString());
		}
		
		this.smoothing = smoothing;
		this.validateState = false;
	}
	
	public void calculate() {
		this.smoothing.calculateNgramProbabilities(this.getN(), this.getPathHandler());
	}
	
	public void setValidateState(boolean validateState) {
		this.validateState = validateState;
	}
	
	public Smoothing getSmoothing() {
		return this.smoothing;
	}
	
	public int getN() {
		return this.n;
	}

	public int getMode() {
		return this.mode;
	}
	
	public PathHandler getPathHandler() {
		return this.ph;
	}
	
	private void checkIntegrity() {
		if (this.getN() < 1) {
			throw new IllegalArgumentException("n must be greater/equals 1");
		}
		System.out.println("'" + this.IN_PATH + "'");
		Pattern r = Pattern.compile("((\\.(\\.?))?/[a-z_\\-\\s0-9\\.]+)+/paths\\.xml");
		Matcher m = r.matcher(this.IN_PATH);
		m.matches();
		if (m.group() == null) {
			throw new IllegalArgumentException("Illegal path");
		}
		if (this.mode != 0 && this.mode != 1) {
			throw new IllegalArgumentException("Illegal mode");
		}
	}
	
}
