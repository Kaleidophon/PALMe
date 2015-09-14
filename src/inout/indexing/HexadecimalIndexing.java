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

public class HexadecimalIndexing extends Indexing {
	
	public HexadecimalIndexing(Map<String, Integer> data, String FREQS_IN_PATH, String LEX_IN_PATH) {
		super(data, FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
	}
	
	public HexadecimalIndexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped) {
		super(FREQS_IN_PATH, LEX_IN_PATH, zipped);
		this.setPrefix();
	}
	
	public HexadecimalIndexing() {
		super();
	}
	
	private void setMode() {
		this.mode = "hexadecimal";
	}
	
	private void setPrefix() {
		this.prefix = "hex_";
	}
}
