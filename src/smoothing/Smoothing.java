package smoothing;

import inout.indexing.Indexing;

import java.util.Map;
import java.util.List;

public interface Smoothing {
	
	public List<Indexing> calculateNgramProbabilities(List<Indexing> frequencies);
}
