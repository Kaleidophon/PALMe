package main;

import inout.general.DataLoader;
import inout.indexing.HexadecimalIndexing;
import inout.paths.PathParser;
import inout.paths.PathHandler;

import languagemodel.*;
import smoothing.*;

public class Main {
	
	public static void main(String[] args) {
		
		LanguageModel lm = new LanguageModel(2, "./rsc/paths.xml", new MaximumFrequencyEstimation(), new DataLoader(), new HexadecimalIndexing(), 1);
		
	}
	
}