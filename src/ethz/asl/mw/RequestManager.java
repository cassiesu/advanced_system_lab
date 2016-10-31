package ethz.asl.mw;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import ethz.asl.message.Request;
import ethz.asl.message.impl.FrontendRequest;
import ethz.asl.message.impl.InvalidResponse;
import ethz.asl.message.impl.PeekFromQueueRequest;
import ethz.asl.message.impl.PeekFromSenderRequest;
import ethz.asl.message.impl.DeleteFromQueueRequest;
import ethz.asl.message.impl.QueryQueuesForReceiverRequest;
import ethz.asl.message.impl.SendRequest;
import ethz.asl.mw.backend.CreateQueueTask;
import ethz.asl.mw.backend.DeleteQueueTask;
import ethz.asl.mw.backend.FrontendTask;
import ethz.asl.mw.backend.PeekFromQueueTask;
import ethz.asl.mw.backend.PeekFromSenderTask;
import ethz.asl.mw.backend.DeleteFromQueueTask;
import ethz.asl.mw.backend.QueryQueuesForReceiverTask;
import ethz.asl.mw.backend.SendTask;
import ethz.asl.mw.frontend.ClientConn;


public class RequestManager implements Runnable {
	
	public RequestManager(ClientConn conn, byte[] data, int length) {
		this.conn = conn;
		in = new DataInputStream(new ByteArrayInputStream(data, 4, length));
	}
	
	private final static Logger LOGGER = Logger.getLogger(RequestManager.class.getName());
	private ClientConn conn;
	private DataInputStream in;

	@Override
	public void run() {
		try {
			Runnable task = null;
			this.conn.traceRequestStartTime();        
			byte requestType = in.readByte();
			
			if (requestType == Request.SEND_BROADCAST_REQUEST) {
				Request req = new SendRequest(in.readInt(), -1, in.readInt(), in.readUTF());
				task = new SendTask((SendRequest) req, conn);
				conn.setOperation("SBR");
				
			} else if (requestType == Request.SEND_TO_RECEIVER_REQUEST) {
				SendRequest req = new SendRequest(in.readInt(), in.readInt(), in.readInt(), in.readUTF());
				task = new SendTask(req, conn);
				conn.setOperation("STR");
				
			} else if (requestType == Request.PEEK_FROM_QUEUE_REQUEST) {
				PeekFromQueueRequest req = new PeekFromQueueRequest(in.readInt(), in.readInt());
				task = new PeekFromQueueTask(req, conn);
				conn.setOperation("PFQ");
				
			} else if (requestType == Request.PEEK_FROM_SENDER_REQUEST) {
				PeekFromSenderRequest req = new PeekFromSenderRequest(in.readInt(), in.readInt());
				task = new PeekFromSenderTask(req, conn);
				conn.setOperation("PFS");
				
			} else if (requestType == Request.DELETE_FROM_QUEUE_REQUEST) {
				DeleteFromQueueRequest req = new DeleteFromQueueRequest(in.readInt(), in.readInt());
				task = new DeleteFromQueueTask(req, conn);
				conn.setOperation("DFQ");
				
			} else if (requestType == Request.CREATE_QUEUE_REQUEST) {
				task = new CreateQueueTask(conn);
				conn.setOperation("CQR");
				
			} else if (requestType == Request.DELETE_QUEUE_REQUEST) {
				task = new DeleteQueueTask(conn, in.readInt());
				conn.setOperation("DQR");
				
			} else if (requestType == Request.QUERY_QUEUES_REQUEST) {
				task = new QueryQueuesForReceiverTask(new QueryQueuesForReceiverRequest(in.readInt()), conn);
				conn.setOperation("QQR");
				
			} else if (requestType == Request.FRONTEND_REQUEST) {
				task = new FrontendTask(conn, new FrontendRequest(in.readUTF()));
				conn.setOperation("FR-");
				
			}

			if (task != null) {
				conn.traceDatabaseSubmitTime();
				Middleware.BACKEND_EXECUTOR_POOL.execute(task);
				
			} else {
				LOGGER.severe("Invalid Request");
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(os);
				
				InvalidResponse ir = new InvalidResponse();
				ir.sendResponse(out);
				
				conn.write(os);
				this.conn.close();
			}
		} catch (IOException e) {
			LOGGER.fine("Request Unhandle! Connection Failed!");
		}
	}
	
}
