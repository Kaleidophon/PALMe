package main;

import inout.*;

import java.util.*;

import languagemodel.*;
import smoothing.*;

public class Main {
	
	public static void main(String[] args) {
		
		LanguageModel lm = new LanguageModel(2, "./rsc/", new MaximumFrequencyEstimation(), new DataLoader(), new HexadecimalIndexing(), 1);
		
	}
	
}