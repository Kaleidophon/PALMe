package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inout.general.DataLoader;
import inout.indexing.HexadecimalIndexing;
import inout.paths.PathParser;
import inout.paths.PathHandler;
import languagemodel.*;
import smoothing.*;

public class Main {
	
	public static void main(String[] args) {

		LanguageModel lm = new LanguageModel(3, "./rsc/paths.xml", "back-off", false);
		//lm.flipDebug();
		lm.getSequenceProbability("David schreibt keinen schönen Code .");
	}
}