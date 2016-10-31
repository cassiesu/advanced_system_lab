package ethz.asl.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class BenchLog {
	
	private static File basedir = new File(System.getProperty("user.home"), "benchmark");
	private BufferedWriter bw;
	
	public void appendSeperator(String s) {
		StringBuilder builder = new StringBuilder(s);
		builder.append("\t");
		builder.append(new Date().toString());
		builder.append("\t");
		builder.append(s);
		builder.append('\n');
		try {
			this.bw.write(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void appendRecord(String s) {
		try {
			this.bw.write(s+"\n");
		} catch (IOException e) {
		}		
	}
	
	public BenchLog(String subdir, String filename) {	
		File benchdir = new File(basedir, subdir);
		if (!benchdir.exists()) {
			benchdir.mkdirs();
		}
		try {
			this.bw = new BufferedWriter(new FileWriter(new File(benchdir, filename)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void flush() {
		try {
			this.bw.flush();
		} catch (IOException e) {
		}
	}
	
	public void close() {
		try {
			this.bw.close();
		} catch (IOException e) {
		}
	}
	
}
