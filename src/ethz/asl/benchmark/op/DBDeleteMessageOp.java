package ethz.asl.benchmark.op;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

import ethz.asl.db.Message;
import ethz.asl.util.Constants;
import ethz.asl.util.DBConnHelper;

public class DBDeleteMessageOp implements BenchmarkOp {

	private final static Logger LOGGER = Logger.getLogger(DBDeleteMessageOp.class.getName());
	
	public DBDeleteMessageOp() {
		this.conn = DBConnHelper.getConn();
		this.queues = Constants.getQueues();
	}
	
	private Connection conn;
	private List<Integer> queues;

	@Override
	public void execute() {
		int receiver = Constants.generateUser();
		Message msg = Message.deleteMessageFromQueue(conn, Constants.generateUserQueue(queues, receiver), receiver);
		if(msg == null) {
			LOGGER.warning("No Message Deleted!");
		}
	}

}
