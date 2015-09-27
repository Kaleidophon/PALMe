package inout.indexing;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilities.Toolbox;

/**
 * Reader to read a dumped indexing parallelized.
 * 
 * @author Dennis Ulmer
 */
public class IndexReader <V extends Number> implements Runnable {
	
	private boolean running = false;
	private String mode;
	private BufferedReader reader;
	private Thread thread;
	private Map<List<Integer>, V> indices;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/**
	 * Default constructor.
	 * 
	 * @param reader Reader for input file
	 * @param mode Reading mode (default, binary, hexadecimal)
	 */
	public IndexReader(BufferedReader reader, String mode) {
		this.reader = reader;
		this.mode = mode;
		this.indices = new HashMap<>();
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
		
	/** Method reading on line at a time */
	public void run() {
		this.setRunning(true);
		try {
			String current_line = reader.readLine().trim();
			while (true) {
				String[] line_parts = current_line.split("\t");
				String[] string_key_indices = line_parts[0].split(" ");
				List<Integer> key_indices = new ArrayList<>();
				int base = 0;
				switch (this.mode) {
					case ("binary"): base = 2; break;
					case ("hexadecimal"): base = 16; break;
					case ("default"): base = 10; break;
				}
				for (int i = 0; i < string_key_indices.length; i++) {
					key_indices.add(Integer.parseInt(string_key_indices[i], base)); // decode every ID
				}
				try {
					// Value is an integer
					this.indices.put(key_indices, (V) Toolbox.intCast(Integer.parseInt(line_parts[1], base)));
				} catch (NumberFormatException nfe) {
					// Value is a double
					switch (this.mode) {
						case ("binary"): this.indices.put(key_indices, (V) Toolbox.stringToDouble(line_parts[1], 2)); break;
						case ("hexadecimal"): this.indices.put(key_indices, (V) Toolbox.stringToDouble(line_parts[1], 16)); break;
						case ("default"): this.indices.put(key_indices, (V) Toolbox.doubleCast(Double.parseDouble(line_parts[1]))); break;
					}
				}
				try {
					current_line = reader.readLine().trim();
					//System.out.println(current_line);
				} catch (NullPointerException npe) {
					//System.out.println("I am reader nr " + this.getID() + " and I finished my job");
					this.setRunning(false);
					break;
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/** Waiting method */
	public void join() {
		try {
			this.thread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** Set running variable */
	private void setRunning(boolean running) {
		this.running = running;
	}
	
	/** @return Whether reader is still running */
	public boolean isRunning() {
		return this.running;
	}
	
	/** @return All read indices */
	public Map<List<Integer>, V> getIndices() {
		return this.indices;
	}
	
	/** @return Thread ID */
	public long getID() {
		return Thread.currentThread().getId();
	}
}
