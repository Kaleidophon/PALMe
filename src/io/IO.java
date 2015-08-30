package io;

import java.io.*;
import custom_exceptions.IOModeException;

public class IO {
	
	final String FILE_PATH;
	final String MODE;
	BufferedReader reader;
	BufferedWriter writer;
	
	public IO(String file_path, String mode) {
		FILE_PATH = file_path;
		MODE = mode;
		
		try {
			if(mode == "out") {
				reader = new BufferedReader(new FileReader(FILE_PATH));
			}
			else if(mode == "in") {
				writer = new BufferedWriter(new FileWriter(FILE_PATH));
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void next(String line) throws IOModeException, IOException {
		if(MODE != "in") {
			throw new IOModeException();
		}
		writer.write(line);
	}
	
	public String next() throws IOModeException, IOException {
		if(MODE != "out") {
			throw new IOModeException();
		}
		return reader.readLine();
	}
	
	public void finish() {
		try {
			if(MODE == "in") {
				writer.close();
			}
			else if(MODE == "out") {
				reader.close();
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
