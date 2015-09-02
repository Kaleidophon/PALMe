package main;

import io.*;
import java.util.List;
import java.util.ArrayList;

public class Testing {

	public static void main(String[] args) {
		timeIndexing(0, "./rsc/indices/2/", false, 10);
		timeIndexing(0, "./rsc/indices/2/", true, 10);
		timeIndexing(1, "./rsc/indices/2/", false, 10);
		timeIndexing(1, "./rsc/indices/2/", true, 10);
		timeIndexing(2, "./rsc/indices/2/", false, 10);
		timeIndexing(2, "./rsc/indices/2/", true, 10);
		timeIndexing(3, "./rsc/indices/2/", false, 10);
		timeIndexing(3, "./rsc/indices/2/", true, 10);
		timeIndexing(4, "./rsc/indices/2/", false, 10);
		timeIndexing(4, "./rsc/indices/2/", true, 10);
		timeIndexing(5, "./rsc/indices/2/", false, 10);
		timeIndexing(5, "./rsc/indices/2/", true, 10);
	}
	
	private static void timeIndexing(int mode, String IN_PATH, boolean para, int iterations) {
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
					Indexing<Integer> indexing1 = new Indexing<>(IN_PATH, para);
					break;
				case (2):
					Indexing indexing2 = new BinaryIndexing(IN_PATH, para);
					break;
				case (3):
					Indexing indexing3 = new HexadecimalIndexing(IN_PATH, para);
					break;
				case (4):
					Indexing<Integer> indexing4 = dl.loadIndexing(IN_PATH + "index.ser", para);
					break;
				case (5):
					Indexing<Integer> indexing5 = dl.loadIndexing(IN_PATH + "bin_index.ser", para);
					break;
				case (6):
					Indexing<Integer> indexing6 = dl.loadIndexing(IN_PATH + "hex_index.ser", para);
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
