package main;

import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;
import utilities.eval.Evaluation;
import languagemodel.*;
import languagemodel.calc.MaximumFrequencyEstimation;
import languagemodel.model.LanguageModel;

public class Main {
	
	public static void main(String[] args) {

		//PathHandler ph = new PathHandler("./rsc/patahs.xml");
		//Path p = ph.getFirstPathWithAttributes("zipped hexadecimal probability indexing 5 read");
		//Indexing<Double> index = new HexadecimalIndexing<>(p.getDirectory(), ph.getFirstPathWithAttributes("zipped lexicon").getDirectory(), p.isZipped());
		
		// Calculation
		LanguageModel lm = new LanguageModel(2, "./rsc/paths.xml", new MaximumFrequencyEstimation(), "fast back-off", true);
		lm.calculate();
		lm.calculateParallelized(2, 2);
		
		// Evaluation
		//LanguageModel lm = new LanguageModel(3, "./rsc/paths.xml", "fast back-off", true);
		//lm.flipDebug();
		//Evaluation.evaluateLanguageModel(lm, "./rsc/corpora/dewiki_plain_1000k_test.txt");
		//lm.evaluateLanguageModel("./rsc/corpora/dewiki_plain_1000k_test.txt");
		//Evaluation.evaluateLanguageModelParallelized(lm, "./rsc/corpora/dewiki_plain_1000k_test.txt", 1, 3);
	}
}