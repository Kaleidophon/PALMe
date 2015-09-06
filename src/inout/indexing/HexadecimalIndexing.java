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

public class HexadecimalIndexing extends Indexing {

	String prefix = "hex_";
	
	public HexadecimalIndexing(Map<String, Integer> data, String IN_PATH) {
		super(data, IN_PATH);
	}
	
	public HexadecimalIndexing(String IN_PATH, boolean zipped) {
		super(IN_PATH, zipped);
	}
	
	public HexadecimalIndexing() {}
	
	private void setMode() {
		this.mode = "hexadecimal";
	}
}
