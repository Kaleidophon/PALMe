package main;

import inout.*;
import inout.general.DataLoader;
import inout.indexing.BinaryIndexing;
import inout.indexing.HexadecimalIndexing;
import inout.indexing.Indexing;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Testing {

	public static void main(String[] args) {
		createTestData(2);
		/*int n = 5;
		for (int c = 0; c <= 5; c++) {
			timeIndexing(c, "./rsc/indices/" + n + "/", false, 10);
			timeIndexing(c, "./rsc/indices/" + n + "/", true, 10);
		}*/
	}
	
	private static void createTestData(int n) {
		List<Indexing> indexings = new ArrayList<>();
		System.out.println("Reading frequencies...");
		DataLoader dl = new DataLoader("./rsc/freqs/" + n + "/res.txt");
		Map<String, Integer> freqs = dl.readFrequencies();
		indexings.add(new Indexing<Integer>());
		indexings.add(new BinaryIndexing());
		indexings.add(new HexadecimalIndexing());
		
		for (Indexing indexing : indexings) {
			if (indexing instanceof BinaryIndexing) {
				System.out.println("Create new "  + indexing.getClass().getName() + " for n = " + n);
				indexing = new BinaryIndexing(freqs, "./rsc/indices/", "./rsc/indices/lexicons/lexicon.gz");
				System.out.println(indexing.getClass().getName());
			} else if (indexing instanceof HexadecimalIndexing) {
				System.out.println("Create new "  + indexing.getClass().getName() + " for n = " + n);
				indexing = new HexadecimalIndexing(freqs, "./rsc/indices/", "./rsc/indices/lexicons/lexicon.gz");
				System.out.println(indexing.getClass().getName());
			} else if (indexing instanceof Indexing) {
				System.out.println("Create new "  + indexing.getClass().getName() + " for n = " + n);
				indexing = new Indexing<Integer>(freqs, "./rsc/indices/", "./rsc/indices/lexicons/lexicon.gz");
				System.out.println(indexing.getClass().getName());
			}
			System.out.println("Dumping...");
			indexing.dump("./rsc/indices/", false);
			indexing.dump("./rsc/indices/", true);
			indexing = null;
			break;
			//dl.dumpIndexing(indexing, "./rsc/indices/" + n + "/", true);
		}
	}
	
	private static void timeIndexing(int mode, String IN_PATH, String LEX_PATH, boolean para, int iterations) {
		System.out.println("Path: " + IN_PATH + " | Mode: " + mode);
		DataLoader dl = new DataLoader();
		long[] durations = new long[iterations];
		String experiment_name = "";
		
		switch(mode) {
			case(0): 
				experiment_name = (para) ? "Loading zipped Indexing" : "Loading unzipped Indexing"; 
				break;
			case(1): 
				experiment_name = (para) ? "Loading zipped Binary Indexing" : "Loading unzipped Binary Indexing"; 
				break;
			case(2): 
				experiment_name = (para) ? "Loading zipped Hexadecimal Indexing" : "Loading unzipped Hexadecimal Indexing"; 
				break;
			case(3): 
				experiment_name = (para) ? "Loading validated serialized Indexing" : "Loading unvalidated serizalied Indexing"; 
				break;
			case(4):
				experiment_name = (para) ? "Loading validated serialized Binary Indexing" : "Loading unvalidated serizalied Binary Indexing"; 
				break;
			case(5): 
				experiment_name = (para) ? "Loading validated serialized Hexadecimal Indexing" : "Loading unvalidated Hexadecimal Indexing"; 
				break;
		}
		
		for (int i = 0; i < iterations; i++) {
			long startTime = System.nanoTime();
			switch (mode) {
				case (0):
					Indexing<Integer> indexing1 = new Indexing<>(LEX_PATH, IN_PATH, para);
					break;
				case (2):
					Indexing indexing2 = new BinaryIndexing(LEX_PATH, IN_PATH, para);
					break;
				case (3):
					Indexing indexing3 = new HexadecimalIndexing(LEX_PATH, IN_PATH, para);
					break;
				case (4):
					//Indexing<Integer> indexing4 = dl.loadIndexing(IN_PATH + "index.ser", para);
					break;
				case (5):
					//Indexing indexing5 = dl.loadIndexing(IN_PATH + "bin_index.ser", para);
					break;
				case (6):
					//Indexing indexing6 = dl.loadIndexing(IN_PATH + "hex_index.ser", para);
					break;
			}
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			durations[i] = duration;
		}
		System.out.println(experiment_name + " took " + average(durations) / 1000000000.0 + " seconds on average.");
	}
	
	private static double average(long[] a) {
		long sum = 0;
		for (long e : a) {
			sum += e;
		}
		return sum * 1.0 / a.length;
	}
}
