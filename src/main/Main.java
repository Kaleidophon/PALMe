package main;

import io.*;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) {
		
		DataLoader dl = new DataLoader("./rsc/freqs/1/res.txt");

		//Map<String, Integer> freqs = dl.readFrequencies();
		Indexing indexing = new BinaryIndexing("./rsc/indices/1/");
		System.out.println(indexing.getIndices());
		//indexing.dump("./rsc/indices/1/");
	}
}