package main;

import io.*;

public class Testing {

	public static void main(String[] args) {
		timeIndexing(0, "./rsc/indices/2/", false);
		timeIndexing(0, "./rsc/indices/2/", true);
		//timeIndexing(2, "./rsc/indices/5/", false);
		//timeIndexing(2, "./rsc/indices/5/", true);
	}
	
	private static void timeIndexing(int mode, String IN_PATH, boolean para) {
		System.out.println("Path: " + IN_PATH + " | Mode: " + mode + " | Para (ValidateState / Zipped)? " + para);
		DataLoader dl = new DataLoader();
		long startTime = System.nanoTime();
		switch (mode) {
			case (0):
				Indexing<Integer> indexing1 = new Indexing<>(IN_PATH, para);
				break;
			case (1):
				Indexing indexing2 = new BinaryIndexing(IN_PATH, para);
				break;
			case (2):
				Indexing<Integer> indexing3 = dl.loadIndexing(IN_PATH + "index.ser", para);
				break;
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("It took " + duration / 1000000.0 + " milliseconds.");
	}
}
