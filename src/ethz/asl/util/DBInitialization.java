package ethz.asl.util;


import java.sql.Connection;
import java.util.ArrayList;
import ethz.asl.db.Message;
import ethz.asl.db.Queue;

public class DBInitialization {

	
	public static void main(String[] args) {
		
		Constants.DATABASE_URL = "jdbc:postgresql://" + args[0] + ":5432/asl?user=postgres&password=";
    	Constants.TEXTLENGTH = Integer.parseInt(args[1]);
    	
		Connection con = DBConnHelper.getConn();
		ArrayList<Integer> queue_ids = new ArrayList<Integer>();

		for (int i=1; i <= Constants.QUEUENUM; i++) {
			int id = Queue.createQueue(con, i);
			queue_ids.add(id);
		}
		System.out.println("Initial Queues Created!");

		final String msg = StringGenerator.get(Constants.TEXTLENGTH);
		Connection conn = DBConnHelper.getConn();
		
		for (final int queue_id : queue_ids) {
			for(int user = 0; user< Constants.USERNUM; user ++) {
				for(int num = 0; num <10; num++) {
					Message.sendMessageBroadcast(conn, user, queue_id, msg);
				}
			}
		}
		System.out.println("Initial Messages Created!");
		
	}
}
