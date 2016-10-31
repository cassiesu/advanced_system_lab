package ethz.asl.benchmark.op;

import java.sql.Connection;
import java.util.List;

import ethz.asl.db.Message;
import ethz.asl.util.Constants;
import ethz.asl.util.DBConnHelper;

public class DBPeekMessageOp implements BenchmarkOp {

	public DBPeekMessageOp() {
		this.conn = DBConnHelper.getConn();
		this.queues = Constants.getQueues();
	}
	
	private Connection conn;
	private List<Integer> queues;

	@Override
	public void execute() {
		int receiver = Constants.generateUser();
		Message msg = Message.peekMessageFromQueue(conn, Constants.generateUserQueue(queues, receiver), receiver);
	}
	
}
