package main;

import inout.general.DataLoader;
import inout.indexing.HexadecimalIndexing;
import inout.paths.PathParser;

import languagemodel.*;
import smoothing.*;

public class Main {
	
	public static void main(String[] args) {
		
		PathParser pp = new PathParser("./rsc/paths.xml");
		//LanguageModel lm = new LanguageModel(2, "./rsc/", new MaximumFrequencyEstimation(), new DataLoader(), new HexadecimalIndexing(), 1);
		
	}
	
}