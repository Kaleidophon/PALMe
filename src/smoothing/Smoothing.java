package smoothing;

import inout.indexing.Indexing;
import inout.paths.Path;
import inout.paths.PathHandler;

import java.util.Map;
import java.util.List;

public interface Smoothing {
	
	public void calculateNgramProbabilities(int n, PathHandler ph);
}
