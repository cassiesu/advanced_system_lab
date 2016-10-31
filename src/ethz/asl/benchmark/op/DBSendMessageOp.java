package ethz.asl.benchmark.op;

import java.sql.Connection;
import java.util.List;
import java.util.Random;

import ethz.asl.db.Message;
import ethz.asl.util.Constants;
import ethz.asl.util.DBConnHelper;
import ethz.asl.util.StringGenerator;

public class DBSendMessageOp implements BenchmarkOp {

	public DBSendMessageOp() {
		this.conn = DBConnHelper.getConn();
		this.queues = Constants.getQueues();
	}
	
	private Connection conn;
	private List<Integer> queues;
	private static Random random = new Random();	
	
	@Override
	public void execute() {
		int sender = Constants.generateUser();
		boolean res;
		if (random.nextFloat() < 0.8) {
			res = Message.sendMessageBroadcast(conn, sender, 
					Constants.generateUserQueue(queues, sender), StringGenerator.get(Constants.TEXTLENGTH));
		} else {
			res = Message.sendMessageToReceiver(conn,sender, 
					Constants.getReceiver(sender), Constants.generateUserQueue(queues, sender), StringGenerator.get(Constants.TEXTLENGTH));
		}
		if (!res) {
			throw new RuntimeException("Sending Message Exception!");
		}
	}

}
