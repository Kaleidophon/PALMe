package inout.general;

import java.io.*;

/**
 * Simple class for both file reading and writing.
 * 
 * @author Dennis Ulmer
 */
public class IO {
	
	private final String FILE_PATH;
	private final String MODE;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String next_line; // Only used in reading mode
	private String current_line; // Only used in reading mode
	
	/**
	 * Simple constructor.
	 * 
	 * @param file_path File to be read or written into.
	 * @param mode Whether class functions as a writer or a reader.
	 */
	public IO(String file_path, String mode) {
		FILE_PATH = file_path;
		MODE = mode;
		try {
			if (mode.equals("out")) {
				this.reader = new BufferedReader(new FileReader(FILE_PATH));
				this.current_line = reader.readLine();
				this.next_line = reader.readLine();
			} else if (mode == "into") {
				this.writer = new BufferedWriter(new FileWriter(FILE_PATH));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Writes next line in writing mode.
	 * Throws @exception {@link IOModeExpcetion} otherwise.
	 * Also throws @exception IOException.
	 * 
	 * @param line Line to be written.
	 */
	public void next(String line) {
		if (!this.MODE.equals("into")) {
			throw new IOModeException();
		}
		try {
			this.writer.write(line);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Reads the next line in reading mode.
	 * Throws @exception {@link IOModeExpcetion} otherwise.
	 * Also throws @exception IOException.
	 * 
	 * @return Next line in the file.
	 */
	public String next() {
		if (!this.MODE.equals("out")) {
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

	/**
	 * Closing open streams.
	 * Throws @exception IOException.
	 */
	public void finish() {
		try {
			if (!this.MODE.equals("into")) {
				this.writer.close();
			} else if (MODE.equals("out")) {
				this.reader.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * @return Whether there is a line left in a file to be read. 
	 */
	public boolean hasNext() {
		return (this.current_line == null) ? false : true;
	}

}
