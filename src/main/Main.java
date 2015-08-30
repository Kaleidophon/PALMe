package main;

import io.*;

public class Main {
	
	public static void main(String[] args) {
//		IO io_in = new IO("./rsc/dewiki_plain_1000k_train.txt", "out");
//		for(int i = 0; i <= 100; i++) {
//			try {
//				System.out.println(io_in.next());
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		DataLoader dl = new DataLoader("./rsc/freqs/1/res.txt");
		System.out.println(dl.readFrequencies());
		
	}
}
