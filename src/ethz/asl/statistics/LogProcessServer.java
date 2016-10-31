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

public class LogProcessServer {
	
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
			if(files[i].isDirectory()) {
				File[] machines = files[i].listFiles();	
				List<List<Integer>> totalRTs = new ArrayList<List<Integer>>();
				List<String> seperators = new ArrayList<String>();
				boolean isFirst = true;
				for(File machine : machines) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(machine),"UTF-8"));
					if(machine.isHidden()) continue;
					int timer = 0;
					String str;
					if("databaseD".equals(files[i].getName()) || "totalTime".equals(files[i].getName()) ) {
						if(isFirst) {
							List<Integer> totalRT = new ArrayList<Integer>();
							while((str = reader.readLine()) != null){
								if(Character.isDigit(str.charAt(4))) {
									totalRT.add(Integer.parseInt(str.substring(4)));
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
								if(Character.isDigit(str.charAt(4))) {
									totalRTs.get(timer).add(Integer.parseInt(str.substring(4)));
								} else if (!str.equals("")){
									timer ++;
									if(timer == seperators.size()) break;
								}
							}
						}
					}else {
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
					}
					
					reader.close();
				}
						
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(files[i].getParent() + "/rt-" + files[i].getName() + "-total.txt")));  
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
	
	public static void CIProcess(String subpath) throws Exception{
		DecimalFormat dcmFmt = new DecimalFormat("0.0000");
		File[] files = (new File(logpath + subpath)).listFiles();
		File stat = new File(logpath + subpath + "/stat");
		if (!stat.exists()) {
			stat.mkdir();
		}
		for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory() && !files[i].getName().startsWith(".")) {
				String[] strs = files[i].getName().split("-");
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(stat.getPath() + "/rt-" + strs[1]+"-stats.txt")  ));  
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
				SummaryStatistics stats = new SummaryStatistics();
				
				String str;
				while((str = reader.readLine()) !=null) {
					if(Character.isDigit(str.charAt(0))) {
						stats.addValue(Integer.parseInt(str));
					}
				}
				double mean = stats.getMean();
				double dev = stats.getStandardDeviation();
		        out.println(dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));
				System.out.println(new File(logpath + subpath).getName() + " rt-" + strs[1] + "\t" + dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));	
		        
				reader.close();
				out.flush();
				out.close();
			}
		}
	}
	
	public static void main(String args[]) throws Exception{
    	
//		String subpath = "stability/server-48";
////		LogProcessServer.rtTotal(subpath);
//    	LogProcessServer.CIProcess(subpath);
    	
		
//    	String op = "server-max";
//    	int[] benchmarks = {8, 16, 32, 64, 96, 128, 160, 200};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "maximum/" + op + "-" + benchmarks[i];
////    		LogProcessServer.rtTotal(subpath);
//        	LogProcessServer.CIProcess(subpath);
//    		
//    	}
    	
    	String op = "server-scale";
    	int[] benchmarks = {11, 12, 13, 14, 15, 21, 22, 23, 24, 25, 31, 32, 33, 34, 35, 41, 42, 43, 44, 45};
    	for(int i=0; i<benchmarks.length; i++) {
    		String subpath = "scalability/server/" + op + "-" + benchmarks[i];
//    		LogProcessServer.rtTotal(subpath);
        	LogProcessServer.CIProcess(subpath);
    		
    	}
    	
//    	String op = "server-msg";
//    	int[] benchmarks = {10, 100, 500, 1000};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "new/" + op + "-" + benchmarks[i];
////    		LogProcessServer.rtTotal(subpath);
//        	LogProcessServer.CIProcess(subpath);
//    		
//    	}
    	
    }

}
