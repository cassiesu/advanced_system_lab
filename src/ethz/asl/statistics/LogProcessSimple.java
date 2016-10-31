package ethz.asl.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class LogProcessSimple {
	private static String pjpath = System.getProperty("user.dir");
	private static String logpath = pjpath + "/logs/simple/";
	
	public static void process(String subpath) throws Exception{
		DecimalFormat dcmFmt = new DecimalFormat("0.0000");
		File file = new File(logpath + subpath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		SummaryStatistics stats = new SummaryStatistics();
				
		String str;
		while((str = reader.readLine()) !=null) {
			str = str.trim();
			stats.addValue(Integer.parseInt(str));
			
		}
		double mean = stats.getMean();
		double dev = stats.getStandardDeviation();
		        
		System.out.println(new File(logpath + subpath).getName() + "\t" + dcmFmt.format(mean/1000) + "\t" + dcmFmt.format(dev/1000));	
		        
		reader.close();
				
		
	}
	
	public static void main(String args[]) throws Exception{
//		String subpath = "db-simple-bench.txt";
//		LogProcessSimple.process(subpath);
		
		String subpath = "mw-simple-bench.txt";
		LogProcessSimple.process(subpath);

	}
	
}
