package smoothing;

import java.util.Map;
import java.util.List;

public interface Smoothing {
	
	public Map<String, Double> calculateNgramProbabilities(List<Map<String, Integer>> frequencies);
}
