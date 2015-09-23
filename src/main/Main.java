package main;

import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;
import utilities.Evaluation;
import languagemodel.*;

public class Main {
	
	public static void main(String[] args) {

		//PathHandler ph = new PathHandler("./rsc/paths.xml");
		//Path p = ph.getFirstPathWithAttributes("zipped hexadecimal probability indexing 5 read");
		//Indexing<Double> index = new HexadecimalIndexing<>(p.getDirectory(), ph.getFirstPathWithAttributes("zipped lexicon").getDirectory(), p.isZipped());
		
		LanguageModel lm = new LanguageModel(5, "./rsc/paths.xml", "fast back-off", true);
		//lm.flipDebug();
		//Evaluation.evaluateLanguageModel(lm, "./rsc/corpora/dewiki_plain_1000k_test.txt");
		//lm.evaluateLanguageModel("./rsc/corpora/dewiki_plain_1000k_test.txt");
		Evaluation.evaluateLanguageModelParallelized(lm, "./rsc/corpora/dewiki_plain_1000k_test.txt", 1, 3);
	}
}