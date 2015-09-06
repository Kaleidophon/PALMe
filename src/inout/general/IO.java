package inout.general;

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
				this.reader = new BufferedReader(new FileReader(FILE_PATH));
			}
			else if(mode == "into") {
				this.writer = new BufferedWriter(new FileWriter(FILE_PATH));
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void next(String line) throws IOModeException, IOException {
		if(MODE != "into") {
			throw new IOModeException();
		}
		this.writer.write(line);
	}
	
	public String next() throws IOModeException, IOException {
		if(MODE != "out") {
			throw new IOModeException();
		}
		String line = this.reader.readLine();
		return line;
	}
	
	public void finish() {
		try {
			if(MODE == "into") {
				this.writer.close();
			}
			else if(MODE == "out") {
				this.reader.close();
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
