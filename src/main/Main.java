package main;


import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;
import languagemodel.*;

public class Main {
	
	public static void main(String[] args) {
		
		PathHandler ph = new PathHandler("./rsc/paths.xml");
		Path testp = ph.getFirstPathWithAttributes("zipped hexadecimal probability indexing 1 read");
		Path lexp = ph.getFirstPathWithAttributes("zipped lexicon");
		Indexing<Double> index = new HexadecimalIndexing<>(testp.getDirectory(), lexp.getDirectory(), testp.isZipped());

		//LanguageModel lm = new LanguageModel(5, "./rsc/paths.xml", "fast back-off", true);
		//lm.flipDebug();
		//System.out.println(lm.evaluateLanguageModel("./rsc/corpora/dewiki_plain_1000k_test.txt"));
	}
}