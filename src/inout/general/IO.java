package inout.general;

import java.io.*;
import custom_exceptions.IOModeException;

public class IO {
	
	final String FILE_PATH;
	final String MODE;
	BufferedReader reader;
	BufferedWriter writer;
	String next_line;
	String current_line;
	
	public IO(String file_path, String mode) {
		FILE_PATH = file_path;
		MODE = mode;
		
		try {
			if(mode == "out") {
				this.reader = new BufferedReader(new FileReader(FILE_PATH));
				this.current_line = reader.readLine();
				this.next_line = reader.readLine();
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
		String tmp = this.current_line;
		this.current_line = this.next_line;
		this.next_line = this.reader.readLine();
		return tmp.trim();
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
	
	public boolean hasNext() {
		boolean res = (this.current_line == null) ? false : true;
		return res;
	}

}
