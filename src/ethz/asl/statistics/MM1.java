package ethz.asl.statistics;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class MM1 {
	private static String pjpath = System.getProperty("user.dir");
	private static String logpath = pjpath + "/logs/mm1/";
	
	public static void main(String args[]) throws Exception{
    	
//		double mu = 695;
//		int threads = 20;
//		double upper = mu * threads;
//    	double rt;
//    	
//    	DecimalFormat df  = new DecimalFormat("###.0000");
//    	FileWriter fw = new FileWriter(new File(logpath + "mm1-model.txt"));
//    	
//    	for(int i=1; i<upper; i++) {
//    		rt = 1000/(mu-i/threads);
//    		fw.write(i + "\t" + df.format(rt) + "\n");
//    		fw.flush();
//    	}
//    	fw.close();
    	
		double[] mu = {1055, 962, 853, 730, 523, 398, 328};
    	int[] threads = {1, 2, 4, 8, 16, 32, 64};
    	int length = threads.length;
		double upper = 13000;
    	double rt;
    	double u;
    	
    	DecimalFormat df  = new DecimalFormat("###.0000");
    	FileWriter fw = new FileWriter(new File(logpath + "mm1-scale-model-rt.txt"));
    	FileWriter fw2 = new FileWriter(new File(logpath + "mm1-scale-model-u.txt"));
    	
    	for(int i=1; i<upper; i++) {
    		for(int j=0; j<length; j++) {
    			if(mu[j] * threads[j] > i) {
    				rt = 1000/(mu[j]-i/threads[j]);
    				u = i/(threads[j]*mu[j]);
    				if(j==0) {
    					fw.write(i + "\t" + df.format(rt));
    					fw2.write(i + "\t" + df.format(u));
    				}else {
    					fw.write("\t" + df.format(rt));
    					fw2.write("\t" + df.format(u));
    				}
    			} else {
    				if(j==0) {
    					fw.write(i + "\t" + "?");
    					fw2.write(i + "\t" + "?");
    				}else {
    					fw.write("\t" + "?");
    					fw2.write("\t" + "?");
    				}
    			}
    		}
    		fw.write("\n");
    		fw2.write("\n");
    		
    		fw.flush();
    		fw2.flush();
    		
    	}
    	
    	fw.close();
    	fw2.close();
    	
    }
}
