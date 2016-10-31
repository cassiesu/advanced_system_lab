package ethz.asl.mw.backend;


import java.util.ArrayList;

import ethz.asl.db.Queue;
import ethz.asl.message.impl.QueryQueuesForReceiverRequest;
import ethz.asl.message.impl.QueryQueuesForReceiverResponse;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;

public class QueryQueuesForReceiverTask extends DBTask {

	private QueryQueuesForReceiverRequest req;
	private ClientConn client;
	
	public QueryQueuesForReceiverTask(QueryQueuesForReceiverRequest req, ClientConn socket) {
		this.req = req;
		this.client = socket;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		ArrayList<Integer> res = Queue.queryQueuesForReceiver(conn, this.req.getReceiver());
		this.client.traceResponseSubmitTime();

		Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new QueryQueuesForReceiverResponse(res)));
	}
}
