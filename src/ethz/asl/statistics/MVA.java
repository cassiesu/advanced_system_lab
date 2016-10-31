package ethz.asl.statistics;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class MVA {
	
	private static String pjpath = System.getProperty("user.dir");
	private static String logpath = pjpath + "/logs/mva/";
	
	public static void main(String args[]) throws Exception{
    	
		int N = 500;		//number of users
		double Z = 0.005;	//think time
		double[] S4 = {0.000198, 0.0000117, 0.000138, 0.000492};		//service time per visit for each component
		int[] D4 = {20, 20, 20, 4};
		
		String file = "mva-model.txt";
		
		int M = D4[0] + D4[1] + D4[2] + D4[3];	//number of devices (not including terminals)
		
		Double[] Ss = new Double[M];
		Double[] Vs = new Double[M];
		for(int i=0; i<M; i++) {
			if(i< D4[0]) {
				Ss[i] = S4[0];
				Vs[i] = 1.0/D4[0];
			} else if(i<D4[0]+D4[1]) {
				Ss[i] = S4[1];
				Vs[i] = 1.0/D4[1];
			} else if(i<D4[0]+D4[1]+D4[2]) {
				Ss[i] = S4[2];
				Vs[i] = 1.0/D4[2];
			} else {
				Ss[i] = S4[3];
				Vs[i] = 1.0/D4[3];
			}
		}
		
		DecimalFormat df  = new DecimalFormat("###.0000");
    	FileWriter fw = new FileWriter(new File(logpath + file));
		
		Double[] Qs = new Double[M];
		Double[] Rs = new Double[M];
		Double[] Us = new Double[M];
		Double[] Xs = new Double[M];
		double R;
		double X = 0.0;
		
		for(int i=0; i<M; i++) {
			Qs[i] = 0.0;
			Us[i] = 0.0;
			Rs[i] = 0.0;
			Xs[i] = 0.0;
		}
		
		double weight = 1.0*(N-1)/N;
//		System.out.print(weight);
		
		for(int n=1; n<=N; n++) {
			for(int j=0; j<M; j++) {
				Rs[j] = Ss[j]*(1+weight*Qs[j]);
			}
			
			R = 0.0;
			for(int j=0; j<M; j++) {
				R += Rs[j]*Vs[j];
			}
			
			X = n/(Z + R);
			
			for(int j=0; j<M; j++) {
				Qs[j] = X*Vs[j]*Rs[j];
			}
			
			for(int j=0; j<M; j++) {
				Xs[j] = X*Vs[j];
				Us[j] = X*Ss[j]*Vs[j];
			}
			
			fw.write(n + "\t" + df.format(X) + "\t" + df.format(R*1000) + "\t" + 
					df.format(Us[0]) + "\t" + df.format(Us[D4[0]]) + "\t" + df.format(Us[D4[0]+D4[1]]) + "\t" + df.format(Us[D4[0]+D4[1]+D4[2]]) + "\t" +
					df.format(Qs[0]) + "\t" + df.format(Qs[D4[0]]) + "\t" + df.format(Qs[D4[0]+D4[1]]) + "\t" + df.format(Qs[D4[0]+D4[1]+D4[2]]) + "\n");
			fw.flush();
			
		}
    	
    	fw.close();  	
    	
    }
}
