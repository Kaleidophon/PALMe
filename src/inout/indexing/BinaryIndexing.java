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

import custom_exceptions.IncompleteLexiconException;

public class BinaryIndexing extends Indexing {
	
	String prefix = "bin_";
	
	public BinaryIndexing(Map<String, Integer> data, String IN_PATH) {
		super(data, IN_PATH);
	}
	
	public BinaryIndexing(String IN_PATH, boolean zipped) {
		super(IN_PATH, zipped);
	}
	
	public BinaryIndexing() {}
	
	private void setMode() {
		this.mode = "binary";
	}
}
