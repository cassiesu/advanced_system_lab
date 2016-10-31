package ethz.asl.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnHelper {
	
	public static Connection getConn() {
		try {
			return DriverManager.getConnection(Constants.DATABASE_URL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		DBConnHelper.getConn();
	}
}
