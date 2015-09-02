package main;

import io.*;
import java.util.*;
import languagemodel.*;
import smoothing.*;

public class Main {
	
	public static void main(String[] args) {
		
		DataLoader dl = new DataLoader("./rsc/freqs/2/res.txt");
		Indexing<Integer> indexing = new HexadecimalIndexing(dl.readFrequencies());
		//indexing.dump("./rsc/indices/2/", true); 
		dl.dumpIndexing(indexing, "./rsc/indices/2/hex_index.ser", true);
		
		//LanguageModel lm = new LanguageModel(1, "./rsc/", new MaximumFrequencyEstimation(), new DataLoader());
		//System.out.println(lm.getNGramFrequencies().size());
	}
	
}