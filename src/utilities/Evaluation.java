package utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import languagemodel.CorpusDepot;
import languagemodel.CorpusEvaluator;
import languagemodel.CorpusReader;
import languagemodel.LanguageModel;
import inout.general.IO;

public class Evaluation {

	public static double evaluateLanguageModel(LanguageModel lm, String IN_PATH) {
		double perplexity = 0.0;
		long startTime = System.nanoTime();
		int count = 0;
		IO reader = new IO(IN_PATH, "out");
		do {
			if (lm.debug() && count % 995 == 0) System.out.println("Sentence nr. " + count);
			String line = reader.next();
			perplexity += lm.getSequenceProbability(line);
			count++;
		} while(reader.hasNext());
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		perplexity /= count;
		System.out.println("Evaluating the " + lm.getMode() + " Language model with " + lm.getN() + "-grams took " + 
								Math.round(duration / 10000000.0) / 100.0 + " s in total.");
		System.out.println("Average perplexity is " + Math.round(perplexity * 100.0) / 100.0);
		return perplexity;
	}
	
	public static double evaluateLanguageModelParallelized(LanguageModel lm, String IN_PATH, int n_of_producer, int n_of_consumer) {
		double perplexity = 0.0;
		int count = 0;
		long startTime = System.nanoTime();
		List<CorpusReader> producer = new ArrayList<>();
		List<CorpusEvaluator> consumer = new ArrayList<>();
		CorpusDepot cd = new CorpusDepot();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(IN_PATH));
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
		for (int i = 0; i < n_of_producer; i++) {
			producer.add(new CorpusReader(reader, cd));
			System.out.println("Starting producer #" + producer.get(i).getId());
		}
		for (int i = 0; i < n_of_consumer; i++) {
			consumer.add(new CorpusEvaluator(lm, cd));
			System.out.println("Starting consumer #" + consumer.get(i).getId());
		}
		
		for (CorpusEvaluator ce : consumer) {
			try {
				ce.join();
				System.out.println("Consumer #" + ce.getID() + " has finished the job.");
				if (!ce.isRunning()) {
					perplexity += ce.getTotalProb();
					count += ce.getLineCount();
				}
			} catch (InterruptedException ie) {}
			
		}
		perplexity /= count;
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Evaluating the " + lm.getMode() + " Language model with " + lm.getN() + "-grams took " + 
				Math.round(duration / 10000000.0) / 100.0 + " s in total.");
		System.out.println("Average perplexity is " + Math.round(perplexity * 100.0) / 100.0);
		
		return perplexity;
	}
}
