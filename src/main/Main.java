package main;

import io.*;
import java.util.*;
import languagemodel.*;
import smoothing.*;

public class Main {
	
	public static void main(String[] args) {
		
		DataLoader dl = new DataLoader("./rsc/freqs/5/res.txt");
		Indexing<Integer> indexing = new Indexing(dl.readFrequencies());
		indexing.dump("./rsc/indices/5/", false);
		//dl.dumpIndexing(indexing, "./rsc/indices/5/", true);
		
		//LanguageModel lm = new LanguageModel(1, "./rsc/", new MaximumFrequencyEstimation(), new DataLoader());
		//System.out.println(lm.getNGramFrequencies().size());
	}
	
}