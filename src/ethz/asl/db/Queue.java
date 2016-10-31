package ethz.asl.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Queue {
	
	private final static Logger LOGGER = Logger.getLogger(Queue.class.getName());

	public static int createQueue(Connection con, int creator) {
		try {
			Statement stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT * FROM create_queue("+creator+")");
			
			if (res.next()) {
				return res.getInt(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			LOGGER.warning("Error: Creating Queue\n"+e.getMessage());
			return -1;
		}
	}
	
	public static boolean deleteQueue(Connection con, int queue) {
		try {
			Statement stmt = con.createStatement();
			stmt.execute("SELECT * FROM delete_queue("+queue + ")");
			return true;
		} catch (SQLException e) {
			LOGGER.warning("Error: Deleting Queue\n"+e.getMessage());
			return false;
		}
	}
	
	public static ArrayList<Integer> queryQueuesForReceiver(Connection con, int receiver) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		try {
			Statement stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT * FROM query_queues_for_receiver("+receiver+")");
			
			while (res.next()) {
				ids.add(res.getInt(1));
			}
		} catch (SQLException e) {
			LOGGER.warning("Error: Query Queues For Receiver\n"+e.getMessage());
		}
		return ids;
	}
}
