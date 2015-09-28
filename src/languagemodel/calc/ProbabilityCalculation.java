package languagemodel.calc;

import inout.paths.PathHandler;

/**
 * Blueprint for a probability calculation, where a {@link PathHandler} with the task description is used
 * to calculate the n-gram probability. 
 * <p>
 * A class of implementing this interface should provide (at least) two kinds of its main calculation method:
 * One sequential and one parallel.
 * 
 * @author Dennis Ulmer
 */
public interface ProbabilityCalculation {
	
	public void calculateNgramProbabilities(int n, PathHandler ph);
	public void calculateNgramProbabilitiesParallelized(int n, PathHandler ph, int producer, int consumer);
}
