package ethz.asl.statistics;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class MMM {
	
	private static String pjpath = System.getProperty("user.dir");
	private static String logpath = pjpath + "/logs/mmm/";
	
	public static void main(String args[]) throws Exception{
    	
		double mu = 5050;
		int m = 10;
		String file = "mmm-mw-model.txt";
		
		double upper = mu * m;
		
		int mfactor = 1;
		for(int i=1; i<=m; i++) {
			mfactor *= i;
		}
		System.out.println("mfactor = " + mfactor);
		
    	double rt;
    	double u;
    	double qp;
    	
    	DecimalFormat df  = new DecimalFormat("###.0000");
    	FileWriter fw = new FileWriter(new File(logpath + file));
    	
    	for(int i=1; i<upper; i++) {
    		u = i/upper;
    		
    		double term = 0;
    		int nfactor = 1;
    		for(int n=1; n<m ; n++) {
    			nfactor *= n;
    			term += Math.pow(m*u, n)/nfactor;
    		}
    		double p0 = 1/(1+ Math.pow(m*u, m)/(mfactor*(1-u)) + term);
    		qp = p0 * Math.pow(m*u, m)/(mfactor*(1-u));
    		
    		rt = 1000 * (1 + qp/(m*(1-u)))/mu;
    		
    		fw.write(i + "\t" + df.format(u) +  "\t" + df.format(qp) + "\t" + df.format(rt) + "\n");
    		fw.flush();
    	
    	}
    	
    	fw.close();
    	
    	
    }
}
