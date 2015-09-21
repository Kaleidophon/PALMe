package inout.indexing;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilities.Toolbox;

public class IndexReader <V extends Number> implements Runnable {
	
	private boolean running = false;
	private String mode;
	private BufferedReader reader;
	private Map<List<Integer>, V> indices;
	
	public IndexReader(BufferedReader reader, String mode) {
		this.reader = reader;
		this.mode = mode;
		this.indices = new HashMap<>();
		Thread thread = new Thread(this);
		thread.start();
	}
		
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
					key_indices.add(Integer.parseInt(string_key_indices[i], base));
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
					System.out.println(current_line);
				} catch (NullPointerException npe) { this.setRunning(false); break; }
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public Map<List<Integer>, V> getIndices() {
		return this.indices;
	}
	
	public long getID() {
		return Thread.currentThread().getId();
	}
	
	
}
