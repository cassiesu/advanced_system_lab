package ethz.asl.benchmark.op;

import java.sql.Connection;
import java.util.List;

import ethz.asl.db.Queue;
import ethz.asl.util.Constants;
import ethz.asl.util.DBConnHelper;

public class DBQueryQueuesOp implements BenchmarkOp {

	public DBQueryQueuesOp() {
		this.conn = DBConnHelper.getConn();
	}
	private Connection conn;
	
	@Override
	public void execute() {
		List<Integer> queues = Queue.queryQueuesForReceiver(conn, Constants.generateUser());
		if (queues == null) {
			System.out.println("Querying Queue Exception!");
		}
	}

}
