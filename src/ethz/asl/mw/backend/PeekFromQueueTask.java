package ethz.asl.mw.backend;


import ethz.asl.db.Message;
import ethz.asl.message.impl.EmptyResponse;
import ethz.asl.message.impl.TextResponse;
import ethz.asl.message.impl.PeekFromQueueRequest;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;

public class PeekFromQueueTask extends DBTask {

	private PeekFromQueueRequest req;
	private ClientConn client;
	
	public PeekFromQueueTask(PeekFromQueueRequest req, ClientConn client) {
		this.req = req;
		this.client = client;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		Message res = Message.peekMessageFromQueue(conn, this.req.getQueue(), this.req.getReceiver());
		this.client.traceResponseSubmitTime();
		
		if (res == null) {
			Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new EmptyResponse()));
		} else {
			Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new TextResponse(res.getText())));
		}
	}
}
