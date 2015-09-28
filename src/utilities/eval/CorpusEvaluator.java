package utilities.eval;

import languagemodel.model.LanguageModel;

/**
 * Calculates the probability of a corpus line retrieved from {@link CorpusDepot}, s.t. the perplexity of {@link LanguageModel} can be computed
 * in parallel by {@link Evaluation}.
 * 
 * @author Dennis Ulmer
 */
public class CorpusEvaluator extends Thread {

	private LanguageModel lm;
	private CorpusDepot cd;
	private int line_count;
	private double total_prob;
	private boolean running;
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	/** Default constructor */
	public CorpusEvaluator(LanguageModel lm, CorpusDepot cd) {
		this.lm = lm;
		this.cd = cd;
		this.start();
	}
	
	// ------------------------------------------------- Main methods ------------------------------------------------
	
	/** Main method to compute the probability of a single line */
	public void run() {
		this.setRunning(true);
		String line = "";
		int line_count = 0;
		double total_prob = 0.0;
		
		while (cd.hasLeft()) {
			line = cd.get();
			//System.out.println("Consumer #" + this.getID() + " analyzing " + line);
			line_count++;
			double prob = lm.getSequenceProbability(line);
			total_prob += prob;
			//System.out.println("Total: " + total_prob + " | Count: " + line_count);
		}
		this.line_count = line_count;
		this.total_prob = total_prob;
		this.setRunning(false);
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	/** Return the number of lines processed by this object */
	public int getLineCount() {
		return this.line_count;
	}
	
	/** Get the sum of all probabilities computed by this object */
	public double getTotalProb() {
		return this.total_prob;
	}
	
	/** Set the running variable. */
	public void setRunning(boolean r) {
		this.running = r;
	}
	
	/** @return Whether this thread is still running */
	public boolean isRunning() {
		return this.running;
	}
	
	/** @return Current thread ID */
	public long getID() {
		return Thread.currentThread().getId();
	}
}
