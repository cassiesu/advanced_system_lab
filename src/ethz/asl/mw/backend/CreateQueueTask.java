package ethz.asl.mw.backend;


import ethz.asl.db.Queue;
import ethz.asl.message.impl.CreateQueueResponse;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;

public class CreateQueueTask extends DBTask {

	private ClientConn client;
	
	public CreateQueueTask(ClientConn client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		
		int id = Queue.createQueue(conn, 0);
		this.client.traceResponseSubmitTime();
		
		Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new CreateQueueResponse(id)));
	}
}
