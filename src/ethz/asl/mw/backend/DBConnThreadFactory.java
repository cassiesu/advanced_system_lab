package ethz.asl.mw.backend;


import java.util.concurrent.ThreadFactory;

public class DBConnThreadFactory implements ThreadFactory {
	
	public Thread newThread(Runnable r) {
		return new DBConnThread(r);  
	}
	
}
