package ethz.asl.mw.backend;


import ethz.asl.db.Message;
import ethz.asl.message.impl.EmptyResponse;
import ethz.asl.message.impl.TextResponse;
import ethz.asl.message.impl.DeleteFromQueueRequest;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;

public class DeleteFromQueueTask extends DBTask {

	private ClientConn client;
	private DeleteFromQueueRequest req;
	
	public DeleteFromQueueTask(DeleteFromQueueRequest req, ClientConn client) {
		this.req = req;
		this.client = client;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		Message res = Message.deleteMessageFromQueue(conn, this.req.getQueue(), this.req.getReceiver());
		this.client.traceResponseSubmitTime();
		
		if (res == null) {
			Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(client, new EmptyResponse()));
		} else {
			Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(client, new TextResponse(res.getText())));
		}
	}
}
