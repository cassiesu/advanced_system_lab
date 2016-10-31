package ethz.asl.benchmark.simple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Logger;

import ethz.asl.db.Message;
import ethz.asl.db.Queue;
import ethz.asl.util.Constants;
import ethz.asl.util.DBConnHelper;


public class DBSimpleBench {
	
	private final static Logger LOGGER = Logger.getLogger(DBSimpleBench.class.getName());
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Constants.DATABASE_URL = "jdbc:postgresql://" + args[0] + ":5432/asl?user=postgres&password=";
    	
		FileWriter w = new FileWriter(new File("DB-Simple-Bench.log"));
		Connection con = DBConnHelper.getConn();
		
		int COUNT = 5000;
		int queue_id = Queue.createQueue(con, 1);

		LOGGER.info("Started Peeking From Queue");
		for (int i=0; i < COUNT; i++) {
			long start = System.nanoTime();
			Message.peekMessageFromQueue(con, queue_id, 1);
			
			w.write(((System.nanoTime()-start) / 1000) +" \n");
			Thread.sleep(100);
		}
		
		LOGGER.info("Started Querying Queues For Receiver");	
		for (int i=0; i < COUNT; i++) {
			long start = System.nanoTime();
			Queue.queryQueuesForReceiver(con, 1);
			
			w.write(((System.nanoTime()-start) / 1000) +" \n");
			Thread.sleep(100);
		}
		
		LOGGER.info("Started Sending Broadcast Message");
		for (int i=0; i < COUNT; i++) {
			long start = System.nanoTime();
			Message.sendMessageBroadcast(con, 1, queue_id, "Hallo");
			
			w.write(((System.nanoTime()-start) / 1000) +" \n");
			Thread.sleep(100);
		}
		
		LOGGER.info("Started Deleting Message From Queue");	
		for (int i=0; i < COUNT; i++) {
			long start = System.nanoTime();
			Message.deleteMessageFromQueue(con, 1, 1);
			
			w.write(((System.nanoTime()-start) / 1000) +" \n");
			Thread.sleep(100);
		}
		
		w.close();
	}
}
