package ethz.asl.mw.backend;


import ethz.asl.db.Message;
import ethz.asl.message.impl.EmptyResponse;
import ethz.asl.message.impl.TextResponse;
import ethz.asl.message.impl.PeekFromSenderRequest;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;

public class PeekFromSenderTask extends DBTask {

	private PeekFromSenderRequest req;
	private ClientConn client;
	
	public PeekFromSenderTask(PeekFromSenderRequest req, ClientConn socket) {
		this.req = req;
		this.client = socket;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		Message res = Message.peekMessageFromSender(conn, this.req.getSender(), this.req.getReceiver());
		this.client.traceResponseSubmitTime();
		
		if (res == null) {
			Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new EmptyResponse()));
		} else {
			Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new TextResponse(res.getText())));
		}
	}
}
