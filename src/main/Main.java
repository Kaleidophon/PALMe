package main;

import inout.general.DataLoader;
import inout.indexing.HexadecimalIndexing;
import inout.paths.PathParser;
import inout.paths.PathHandler;

import languagemodel.*;
import smoothing.*;

public class Main {
	
	public static void main(String[] args) {
		
		PathHandler ph = new PathHandler("./rsc/paths.xml");
		System.out.println(ph.getPaths());
		System.out.println(ph.getPathsWithAttributes("raw binary lexicon indexing 3"));
		//LanguageModel lm = new LanguageModel(2, "./rsc/", new MaximumFrequencyEstimation(), new DataLoader(), new HexadecimalIndexing(), 1);
		
	}
	
}