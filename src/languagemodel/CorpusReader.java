package languagemodel;

import java.io.BufferedReader;
import java.io.IOException;

public class CorpusReader extends Thread {

	private BufferedReader reader;
	private CorpusDepot cd;
	
	public CorpusReader(BufferedReader reader, CorpusDepot cd) {
		this.reader = reader;
		this.cd = cd;
		this.start();
	}
	
	public void run() {
		try {
			String current_line = reader.readLine().trim();
			while(true) {
				System.out.println("Producer #" + this.getID() + " pushing " + current_line);
				this.getCorpusDepot().add(current_line);
				try {
					current_line = reader.readLine().trim();
				} catch (NullPointerException npe) {
					break;
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public CorpusDepot getCorpusDepot() {
		return this.cd;
	}
	
	public long getID() {
		return Thread.currentThread().getId();
	}
}
