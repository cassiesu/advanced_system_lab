package ethz.asl.statistics;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class LogProcessSingle {
	
	private static double calcMeanCI(SummaryStatistics stats, double level) {
        try {
            // Create T Distribution with N-1 degrees of freedom
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            // Calculate critical value
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
            // Calculate confidence interval
            return critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }
	
	private static String pjpath = System.getProperty("user.dir");
	private static String logpath = pjpath + "/logs/";
	
	public static void rtTotal(String subpath) throws Exception{
		File[] files = (new File(logpath + subpath)).listFiles();	
		System.out.println(logpath + subpath);
		for(int i=0;i<files.length;i++){
			if(files[i].isDirectory() && files[i].getName().equals("rt")) {
				File[] clients = files[i].listFiles();		//clients
				List<List<Integer>> totalRTs = new ArrayList<List<Integer>>();
				List<String> seperators = new ArrayList<String>();
				boolean isFirst = true;
				for(File client : clients) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(client),"UTF-8"));
					if(client.isHidden()) continue;
					int timer = 0;
					String str;
					if(isFirst) {
						List<Integer> totalRT = new ArrayList<Integer>();
						while((str = reader.readLine()) != null){
							if(Character.isDigit(str.charAt(0))) {
								totalRT.add(Integer.parseInt(str));
							} else if (!str.equals("")){
								seperators.add(str);
								totalRTs.add(totalRT);
								totalRT = new ArrayList<Integer>();
							}
						}
						System.out.println("length1: " + totalRTs.size());
						System.out.println("length2: " + seperators.size());
						isFirst = false;
					} else {
						while((str = reader.readLine()) != null){
							if(Character.isDigit(str.charAt(0))) {
								totalRTs.get(timer).add(Integer.parseInt(str));
							} else if (!str.equals("")){
								timer ++;
								if(timer == seperators.size()) break;
							}
						}
					}
					reader.close();
				}
						
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(files[i].getParent() + "/rt-total.txt")));  
				for(int k=0; k<seperators.size(); k++) {
					List<Integer> list = totalRTs.get(k);
					for(Integer ele : list) {
						out.println(ele);
					}
					out.println(seperators.get(k));
				}
				out.flush();
				out.close();
			
			}
        }   
	}
	
	public static void tpTotal(String subpath) throws Exception{
		File[] files = (new File(logpath + subpath)).listFiles();
		for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory() && files[i].getName().equals("rt-total.txt")) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(files[i].getParent() + "/tp-total.txt")));  
				
				int count = 0;
				String str;
				while((str = reader.readLine()) != null){
					if(Character.isDigit(str.charAt(0))) {
						count ++;
					} else {
						out.println(count);
						System.out.println(count);
						count = 0;
					}
				}
				reader.close();
				out.flush();
				out.close();
			
			}

        }   
	}
	
	public static void CIProcess(String subpath) throws Exception{
		DecimalFormat dcmFmt = new DecimalFormat("0.0000");
		File[] files = (new File(logpath + subpath)).listFiles();
		File stat = new File(logpath + subpath + "/stat");
		if (!stat.exists()) {
			stat.mkdir();
		}
		for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory()) {
				if(files[i].getName().equals("rt-total.txt")) {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(stat.getPath() + "/rt-total-stats.txt")));  
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
					SummaryStatistics stats = new SummaryStatistics();
					String str;
					double ci = 0;
					double mean = 0;
					double lower = 0;
					double upper = 0;
					int count = 0;
					List<Double> data = new ArrayList<Double>();
					while((str = reader.readLine()) !=null) {
						if(Character.isDigit(str.charAt(0))) {
							data.add(Double.parseDouble(str));
						}
					}
					Random random = new Random();
					while(true) {
						if(data.size() == 0) break;
						int index = random.nextInt(data.size());
						stats.addValue(data.get(index));
						mean = stats.getMean();
						ci = LogProcessSingle.calcMeanCI(stats, 0.95);
						lower = mean - ci;
				        upper = mean + ci;
						if(lower > mean*0.90 && upper < mean*1.10 && count>20000)	break;
						data.remove(index);
						count++;
					}
					
					double dev = stats.getStandardDeviation();
			        out.println(dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));
					System.out.println(new File(logpath + subpath).getName() + " rt" + "\t" + dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));
					
					reader.close();
					out.flush();
					out.close();
				} else if (files[i].getName().equals("tp-total.txt")) {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(stat.getPath() + "/tp-total-stats.txt")));  
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
					SummaryStatistics stats = new SummaryStatistics();
					String str;
					double ci = 0;
					double mean = 0;
					double lower = 0;
					double upper = 0;
					int count = 0;
					while((str = reader.readLine()) !=null) {
						stats.addValue(Double.parseDouble(str));
//						mean = stats.getMean();
//						ci = LogProcess.calcMeanCI(stats, 0.95);
//						lower = mean - ci;
//				        upper = mean + ci;
//						if(lower > mean*0.95 && upper < mean*1.05) break;
						count++;
					}
//					System.out.println("tp:" + count);
					
					mean = stats.getMean();
					double dev = stats.getStandardDeviation();
			        out.println(dcmFmt.format(mean) + "\t" + dcmFmt.format(mean - dev) + "\t" + dcmFmt.format(mean + dev));
			        System.out.println(new File(logpath + subpath).getName() + " tp" + "\t" + dcmFmt.format(mean) + "\t" + dcmFmt.format(mean - dev) + "\t" + dcmFmt.format(mean + dev));
					
					reader.close();
					out.flush();
					out.close();
				}
			}
		}
	}
	
    public static void main(String args[]) throws Exception{
//    	String op = "dbsend";
//    	int[] benchmarks = {4, 8, 16, 32, 64};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "perf_db/" + op + "_5/" + op + "-" + benchmarks[i];
//    		LogProcessSingle.rtTotal(subpath);
//    		LogProcessSingle.tpTotal(subpath);
//        	LogProcessSingle.CIProcess(subpath);
//    		
//    	}
    	
    	String op = "mwfrontend";
    	int[] benchmarks = {4, 8, 16, 32, 64, 128};
    	for(int i=0; i<benchmarks.length; i++) {
    		String subpath = "perf_mw/" + op + "-" + benchmarks[i];
//    		LogProcessSingle.rtTotal(subpath);
//    		LogProcessSingle.tpTotal(subpath);
        	LogProcessSingle.CIProcess(subpath);
    		
    	}
    	
    }

}
