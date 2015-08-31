package main;

import io.*;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) {
		
		DataLoader dl = new DataLoader("./rsc/freqs/1/res.txt");

		Map<String, Integer> freqs = dl.readFrequencies();
		BinaryIndexing indexing = new BinaryIndexing(freqs);
		//System.out.println(indexing.getIndices());
		dl.writeIndexing(indexing, "./rsc/indices/1/");
	}
}
