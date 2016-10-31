package ethz.asl.mw.backend;


import java.sql.Connection;

import ethz.asl.util.DBConnHelper;

public class DBConnThread extends Thread {

	Connection conn;
	
	public Connection getConnection() {
		return conn;
	}
	
	public DBConnThread(Runnable r) {
		super(r);
		conn = DBConnHelper.getConn();
	}
	
}
