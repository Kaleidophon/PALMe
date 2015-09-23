package main;


import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;
import languagemodel.*;

public class Main {
	
	public static void main(String[] args) {

		LanguageModel lm = new LanguageModel(5, "./rsc/paths.xml", "fast back-off", true);
		lm.flipDebug();
		
		//System.out.println(lm.evaluateLanguageModel("./rsc/corpora/dewiki_plain_1000k_test.txt"));
	}
}