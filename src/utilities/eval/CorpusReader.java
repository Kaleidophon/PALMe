package utilities.eval;

import java.io.BufferedReader;
import java.io.IOException;

/** 
 * Class to read a line from a corpus and add it to the stack of {@link CorpusDepot}.
 * 
 * @author Dennis Ulmer
 */
public class CorpusReader extends Thread {

	private BufferedReader reader;
	private CorpusDepot cd;
	
	// ------------------------------------------------- Constructors ------------------------------------------------

	/** Defautl constructor */
	public CorpusReader(BufferedReader reader, CorpusDepot cd) {
		this.reader = reader;
		this.cd = cd;
		this.start();
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Main method to read a single line at a time */
	public void run() {
		try {
			String current_line = reader.readLine().trim();
			while(true) {
				//System.out.println("Producer #" + this.getID() + " pushing " + current_line);
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
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** @return Current {@link CorpusDepot} */
	public CorpusDepot getCorpusDepot() {
		return this.cd;
	}
	
	/** @return current thread ID */
	public long getID() {
		return Thread.currentThread().getId();
	}
}
