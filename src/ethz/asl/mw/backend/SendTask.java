package ethz.asl.mw.backend;


import ethz.asl.db.Message;
import ethz.asl.message.impl.SendRequest;
import ethz.asl.message.impl.SendResponse;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;

public class SendTask extends DBTask {

	private SendRequest req;
	private ClientConn client;
	
	public SendTask(SendRequest req, ClientConn client) {
		this.req = req;
		this.client = client;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		boolean res;
		if (req.getReceiver() == -1) {
			res = Message.sendMessageBroadcast(conn, req.getSender(), req.getQueue(), req.getMessage());
		} else {
			res = Message.sendMessageToReceiver(conn, req.getSender(), req.getReceiver(), req.getQueue(), req.getMessage());
		}
		this.client.traceResponseSubmitTime();
		
		Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(client, new SendResponse(res)));
	}
}
