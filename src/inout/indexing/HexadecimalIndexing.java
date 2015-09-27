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
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class HexadecimalIndexing <V extends Number> extends Indexing {
	
	// ------------------------------------------------- Constructors ------------------------------------------------
	
	public HexadecimalIndexing(Map<String, V> data, String FREQS_IN_PATH, String LEX_IN_PATH) {
		super(data, FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
	}
	
	public HexadecimalIndexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped) {
		super(FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
		this.load(FREQS_IN_PATH, LEX_IN_PATH, zipped);
	}
	
	public HexadecimalIndexing(String FREQS_IN_PATH, String LEX_IN_PATH, boolean zipped, int threads) {
		super(FREQS_IN_PATH, LEX_IN_PATH);
		this.setPrefix();
		this.setMode();
		this.loadParallelized(FREQS_IN_PATH, LEX_IN_PATH, zipped, threads);
	}
	
	public HexadecimalIndexing(Map<List<Integer>, V> indexed_data, String FREQS_IN_PATH, boolean zipped) {
		super(indexed_data, FREQS_IN_PATH);
		this.setMode();
		this.setPrefix();
		this.dump(FREQS_IN_PATH, zipped);
	}
	
	public HexadecimalIndexing() {
		super();
	}
	
	// ----------------------------------------------- Getter & Setter -----------------------------------------------
	
	private void setMode() {
		this.mode = "hexadecimal";
	}
	
	private void setPrefix() {
		this.prefix = "hex_";
	}
}
