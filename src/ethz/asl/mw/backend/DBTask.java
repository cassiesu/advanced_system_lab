package ethz.asl.mw.backend;


import java.sql.Connection;


public abstract class DBTask implements Runnable {

	protected Connection conn;
	public abstract void run();
	
}
