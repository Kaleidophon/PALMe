package inout.general;

import java.io.*;

public class IO {
	
	private final String FILE_PATH;
	private final String MODE;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String next_line;
	private String current_line;
	
	public IO(String file_path, String mode) {
		FILE_PATH = file_path;
		MODE = mode;
		
		try {
			if(mode == "out") {
				this.reader = new BufferedReader(new FileReader(FILE_PATH));
				this.current_line = reader.readLine();
				this.next_line = reader.readLine();
			} else if(mode == "into") {
				this.writer = new BufferedWriter(new FileWriter(FILE_PATH));
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void next(String line) {
		if(MODE != "into") {
			throw new IOModeException();
		}
		try {
			this.writer.write(line);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
	}
	
	public String next() {
		if(MODE != "out") {
			throw new IOModeException();
		}
		String tmp = this.current_line;
		this.current_line = this.next_line;
		try {
			this.next_line = this.reader.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
		return tmp.trim();
	}

	public void finish() {
		try {
			if(MODE == "into") {
				this.writer.close();
			} else if(MODE == "out") {
				this.reader.close();
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public boolean hasNext() {
		boolean res = (this.current_line == null) ? false : true;
		return res;
	}

}
