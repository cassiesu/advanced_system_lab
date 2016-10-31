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

public class LogProcessMultiple {
	
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
	
	public static void rtTotalCategory(String subpath) throws Exception{
		File[] files = (new File(logpath + subpath)).listFiles();	
		System.out.println(logpath + subpath);
		for(int i=0;i<files.length;i++){
			if(files[i].isDirectory()) {
				String op = files[i].getName().split("-")[0];
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
						
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(files[i].getParent() + "/rt-" + op + "-total.txt")));  
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
	
	public static void tpGeneratorCategory(String subpath) throws Exception{
		File[] files = (new File(logpath + subpath)).listFiles();
		for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory()) {
				String[] strs = files[i].getName().split("-");
				if(strs.length == 3) {
					if("rt".equals(strs[0]) && "total.txt".equals(strs[2])) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(files[i].getParent() + "/tp-" + strs[1] + "-total.txt")));  
						
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
				String[] strs = files[i].getName().split("-");
				if("rt".equals(strs[0])) {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(stat.getPath() + "/rt-" + (strs[1].equals("total.txt")?"total-stats.txt":strs[1]+"-stats.txt")  )));  
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
						ci = LogProcessMultiple.calcMeanCI(stats, 0.95);
						lower = mean - ci;
				        upper = mean + ci;
						if(lower > mean*0.90 && upper < mean*1.10 && count>30000)	break;
						data.remove(index);
						count++;
					}
					
					double dev = stats.getStandardDeviation();
			        out.println(dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));
					if(strs[1].equals("total.txt")) {
						System.out.println(new File(logpath + subpath).getName() + " rt-" + strs[1] + "\t" + dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));	
					}
//					System.out.println(new File(logpath + subpath).getName() + " rt-" + strs[1] + "\t" + dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));	
			        
					reader.close();
					out.flush();
					out.close();
				} else if ("tp".equals(strs[0])) {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(stat.getPath() + "/tp-" + (strs[1].equals("total.txt")?"total-stats.txt":strs[1]+"-stats.txt") )));  
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
			        if(strs[1].equals("total.txt")){
			        	System.out.println(new File(logpath + subpath).getName() + " tp-" + strs[1] + "\t" + dcmFmt.format(mean) + "\t" + dcmFmt.format(mean - dev) + "\t" + dcmFmt.format(mean + dev));
			        }
//			        System.out.println(new File(logpath + subpath).getName() + " tp-" + strs[1] + "\t" + dcmFmt.format(mean) + "\t" + dcmFmt.format(mean - dev) + "\t" + dcmFmt.format(mean + dev));
			        
			        
					reader.close();
					out.flush();
					out.close();
				}
			}
		}
	}
	
	public static void TPtimeline(String subpath, int time) throws Exception {
		DecimalFormat dcmFmt = new DecimalFormat("0.0000");
		File[] files = (new File(logpath + subpath)).listFiles();
		File stat = new File(logpath + subpath + "/stat");
		if (!stat.exists()) {
			stat.mkdir();
		}
		for(int i=0;i<files.length;i++){
			if("tp-total.txt".equals(files[i].getName())) {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(stat.getPath() + "/tp-plot.txt") ));  
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
				SummaryStatistics stats = new SummaryStatistics();
				String str;
				double mean = 0;
				double dev = 0;
				int count = 0;
				while((str = reader.readLine()) !=null) {
					stats.addValue(Double.parseDouble(str));
					count++;
					if(count == time) {
						count = 0;
						mean = stats.getMean();
						dev = stats.getStandardDeviation();
						out.println(dcmFmt.format(mean) + "\t" + dcmFmt.format(mean - dev) + "\t" + dcmFmt.format(mean + dev));
						stats.clear();
					}
				}
		        
				reader.close();
				out.flush();
				out.close();
			}
			
		}
		
	}
	
	public static void RTtimeline(String subpath, int time) throws Exception {
		DecimalFormat dcmFmt = new DecimalFormat("0.0000");
		File[] files = (new File(logpath + subpath)).listFiles();
		File stat = new File(logpath + subpath + "/stat");
		if (!stat.exists()) {
			stat.mkdir();
		}
		for(int i=0;i<files.length;i++){
			if("rt-total.txt".equals(files[i].getName())) {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(stat.getPath() + "/rt-plot.txt")  ));  
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
				SummaryStatistics stats = new SummaryStatistics();
				String str;
				int count = 0;
				double mean = 0;
				double dev = 0;
				while((str = reader.readLine()) !=null) {
					if(Character.isDigit(str.charAt(0))) {
						stats.addValue(Double.parseDouble(str));
					}else {
						count ++;
						if(count == time) {
							count = 0;
							mean = stats.getMean();
							dev = stats.getStandardDeviation();
							out.println(dcmFmt.format(mean/1000) + "\t" + dcmFmt.format((mean - dev)/1000) + "\t" + dcmFmt.format((mean + dev)/1000));
							stats.clear();
						}
					}
				}
				 
				reader.close();
				out.flush();
				out.close();
			} 
		}
			
	}
	
    public static void main(String args[]) throws Exception{
    	
//    	String op = "dbload";
//    	int[] benchmarks = {4, 8, 16, 20, 24, 28, 32};
////    	int[] benchmarks = {64};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "perf_db/" + op + "_new/" + op + "-" + benchmarks[i];
////    		LogProcessMultiple.rtTotalCategory(subpath);
////    		LogProcessMultiple.tpGeneratorCategory(subpath);
////    		LogProcessMultiple.rtTotal(subpath);
////    		LogProcessMultiple.tpTotal(subpath);
//        	LogProcessMultiple.CIProcess(subpath);
//    		
//    	}
    	
    	
//		String subpath = "stability/mwload-stability-32";
//		LogProcessMultiple.rtTotalCategory(subpath);
//		LogProcessMultiple.tpGeneratorCategory(subpath);
//		LogProcessMultiple.rtTotal(subpath);
//		LogProcessMultiple.tpTotal(subpath);
//    	LogProcessMultiple.CIProcess(subpath);
//    	LogProcessMultiple.RTtimeline(subpath, 60);
//    	LogProcessMultiple.TPtimeline(subpath, 60);
    	
    	
    	String op = "mwload-system-max";
//    	int[] benchmarks = {8, 16, 32, 64, 96, 128, 160, 200};
    	int[] benchmarks = {240, 280, 320, 360, 400};
    	for(int i=0; i<benchmarks.length; i++) {
    		String subpath = "maximum/" + op + "-" + benchmarks[i];
//    		LogProcessMultiple.rtTotalCategory(subpath);
//    		LogProcessMultiple.tpGeneratorCategory(subpath);
//    		LogProcessMultiple.rtTotal(subpath);
//    		LogProcessMultiple.tpTotal(subpath);
        	LogProcessMultiple.CIProcess(subpath);
    		
    	}
    	
    	
//    	String op = "mwload-system-scale";
////    	int[] benchmarks = {11, 12, 13, 14, 15, 21, 22, 23, 24, 25, 31, 32, 33, 34, 35};
//    	int[] benchmarks = {41, 42, 43, 44, 45};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "scalability/" + op + "-" + benchmarks[i];
////    		LogProcessMultiple.rtTotalCategory(subpath);
////    		LogProcessMultiple.tpGeneratorCategory(subpath);
////    		LogProcessMultiple.rtTotal(subpath);
////    		LogProcessMultiple.tpTotal(subpath);
//        	LogProcessMultiple.CIProcess(subpath);
//    		
//    	}
    	
//    	String op = "mwload-system-msg";
//    	int[] benchmarks = {10, 100, 200, 500, 1000};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "perf_rt/" + op + "-" + benchmarks[i];
//    		LogProcessMultiple.rtTotalCategory(subpath);
//    		LogProcessMultiple.tpGeneratorCategory(subpath);
//    		LogProcessMultiple.rtTotal(subpath);
//    		LogProcessMultiple.tpTotal(subpath);
//        	LogProcessMultiple.CIProcess(subpath);
//    		
//    	}
    	
//    	String op = "mwload-system-mw";
//    	int[] benchmarks = {1, 2, 3, 4};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "perf_rt/" + op + "-" + benchmarks[i];
////    		LogProcessMultiple.rtTotalCategory(subpath);
////    		LogProcessMultiple.tpGeneratorCategory(subpath);
////    		LogProcessMultiple.rtTotal(subpath);
////    		LogProcessMultiple.tpTotal(subpath);
//        	LogProcessMultiple.CIProcess(subpath);
//    		
//    	}
    	
//    	String op = "mwload-system-think";
//    	int[] benchmarks = {0, 2, 4, 8, 16};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "perf_rt/" + op + "-" + benchmarks[i];
////    		LogProcessMultiple.rtTotalCategory(subpath);
////    		LogProcessMultiple.tpGeneratorCategory(subpath);
////    		LogProcessMultiple.rtTotal(subpath);
////    		LogProcessMultiple.tpTotal(subpath);
//        	LogProcessMultiple.CIProcess(subpath);
//    		
//    	}
    	
//    	String op = "mwload-system-2k";
//    	int[] benchmarks = {1, 2, 3, 4, 5, 6, 7, 8};
//    	for(int i=0; i<benchmarks.length; i++) {
//    		String subpath = "2k_test/" + op + "-" + benchmarks[i];
////    		LogProcessMultiple.rtTotalCategory(subpath);
////    		LogProcessMultiple.tpGeneratorCategory(subpath);
////    		LogProcessMultiple.rtTotal(subpath);
////    		LogProcessMultiple.tpTotal(subpath);
////        	LogProcessMultiple.CIProcess(subpath);
//    		
//    	}
    	
    	
    }

	private static void rtTotal(String subpath) throws Exception {
		File[] files = (new File(logpath + subpath)).listFiles();
		List<List<Integer>> totalRTs = new ArrayList<List<Integer>>();
		List<String> seperators = new ArrayList<String>();
		boolean isFirst = true;
		for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory() && files[i].getName().split("-")[0].equals("rt")) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
				String str;
				int timer = 0;
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
					
        } 
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(logpath + subpath).getPath() + "/rt-total.txt")));  
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

	private static void tpTotal(String subpath) throws Exception{
		File[] files = (new File(logpath + subpath)).listFiles();
		List<Integer> totalTP = new ArrayList<Integer>();
		boolean isFirst = true;
		for(int i=0;i<files.length;i++){
			if(!files[i].isDirectory() && files[i].getName().split("-")[0].equals("tp")) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i]),"UTF-8"));
				String str;
				int timer = 0;
				if(isFirst) {
					while((str = reader.readLine()) != null){
						totalTP.add(Integer.parseInt(str));
					}
					isFirst = false;
				} else {
					while((str = reader.readLine()) != null){
						if(timer == totalTP.size()) break;
						totalTP.set(timer, totalTP.get(timer) + Integer.parseInt(str));
						timer ++;
					}
				}
				reader.close();
			}
					
        } 
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(logpath + subpath).getPath() + "/tp-total.txt")));  
		for(int k=0; k<totalTP.size(); k++) {
			out.println(totalTP.get(k));
		}
		out.flush();
		out.close();
		
	}
    

}
