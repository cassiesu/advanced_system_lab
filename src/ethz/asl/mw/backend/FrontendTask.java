package ethz.asl.mw.backend;


import ethz.asl.message.impl.FrontendRequest;
import ethz.asl.message.impl.FrontendResponse;
import ethz.asl.mw.ResponseManager;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConn;


public class FrontendTask extends DBTask {
	private ClientConn client;
	private FrontendRequest req;
	
	public FrontendTask(ClientConn client, FrontendRequest req) {
		this.client = client;
		this.req = req;
	}
	
	@Override
	public void run() {
		this.client.traceDatabaseStartTime();
		this.client.traceResponseSubmitTime();
		
		Middleware.FRONTEND_EXECUTOR_POOL.execute(new ResponseManager(this.client, new FrontendResponse(this.req.getEcho())));
	}
}
