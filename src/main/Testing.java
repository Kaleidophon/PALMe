package main;

import io.*;

public class Testing {

	public static void main(String[] args) {
		timeIndexing(0, "./rsc/indices/", true);
		timeIndexing(1, "./rsc/indices/", false);
		timeIndexing(1, "./rsc/indices/", true);
	}
	
	private static void timeIndexing(int mode, String IN_PATH, boolean validateState) {
		System.out.println("Path: " + IN_PATH + " | Mode: " + mode + " | Validate State? " + validateState);
		DataLoader dl = new DataLoader();
		long startTime = System.nanoTime();
		switch (mode) {
			case (0):
				Indexing<Integer> indexing1 = new Indexing<>(IN_PATH);
				break;
			case (1):
				Indexing indexing2 = new BinaryIndexing(IN_PATH);
				break;
			case (2):
				Indexing<Integer> indexing3 = dl.loadIndexing(IN_PATH + "index.ser", validateState);
				break;
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("It took " + duration / 1000000.0 + " milliseconds.");
	}
}
