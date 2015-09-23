package languagemodel;

public class CorpusEvaluator extends Thread {

	private LanguageModel lm;
	private CorpusDepot cd;
	private int line_count;
	private double total_prob;
	private boolean running;
	
	public CorpusEvaluator(LanguageModel lm, CorpusDepot cd) {
		this.lm = lm;
		this.cd = cd;
		this.start();
	}
	
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
	
	public int getLineCount() {
		return this.line_count;
	}
	
	public double getTotalProb() {
		return this.total_prob;
	}
	
	public void setRunning(boolean r) {
		this.running = r;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public long getID() {
		return Thread.currentThread().getId();
	}
}
