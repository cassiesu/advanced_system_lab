package ethz.asl.mw.backend;


import ethz.asl.db.Queue;
import ethz.asl.message.impl.DeleteQueueResponse;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;

public class DeleteQueueTask extends DBTask {

	private ClientConn client;
	private int id;
	
	public DeleteQueueTask(ClientConn client, int id) {
		this.client = client;
		this.id = id;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		boolean ok = Queue.deleteQueue(conn, this.id);
		this.client.traceResponseSubmitTime();
		
		Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new DeleteQueueResponse(ok)));
	}
}
