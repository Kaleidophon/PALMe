package inout.indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BinaryIndexing extends Indexing {
	
	public BinaryIndexing(Map<String, Integer> data, String FREQS_IN_PATH, String LEX_IN_PATH) {
		super(data, FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
	}
	
	public BinaryIndexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped) {
		super(FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
		this.load(FREQS_IN_PATH, LEX_IN_PATH, zipped);
	}
	
	public BinaryIndexing() {
		super();
		this.setPrefix();
		this.setMode();
	}
	
	private void setMode() {
		this.mode = "binary";
	}
	
	private void setPrefix() {
		this.prefix = "bin_";
	}
}
