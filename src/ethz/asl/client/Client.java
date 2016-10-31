package ethz.asl.client;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import ethz.asl.message.Request;
import ethz.asl.message.Response;
import ethz.asl.message.impl.CreateQueueResponse;
import ethz.asl.message.impl.DeleteQueueResponse;
import ethz.asl.message.impl.FrontendResponse;
import ethz.asl.message.impl.EmptyResponse;
import ethz.asl.message.impl.InvalidResponse;
import ethz.asl.message.impl.TextResponse;
import ethz.asl.message.impl.QueryQueuesForReceiverResponse;
import ethz.asl.message.impl.SendResponse;

/**
 * Client: Blocking Socket Connection to Server
 * @author CassieSu
 *
 */
public class Client {
	private Socket socket;
	DataInputStream in;
	DataOutputStream out;
	
	public Client(String host, int port) throws IOException {
		this(InetAddress.getByName(host),port);
	}
	
	public Client(InetAddress addr, int port) throws IOException {
		this(new Socket(addr, port));
	}
	
	
	public Client(Socket socket) throws IOException {
		this.socket = socket;
		this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
		this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
	}
	

	public void cleanup() {
		try {
			this.in.close();
			this.out.close();
			if (this.socket != null) {
				this.socket.close();
			}

		} catch (IOException e) {
		}
	}
	
	// Sends request and retuns the response
	public Response send(Request req) throws IOException {
		byte[] data = req.getRequest();
		this.out.writeInt(data.length);
		this.out.write(data);
		this.out.flush();
		
		byte type = this.in.readByte();
		if (type == Response.SEND_OK_RESPONSE) {
			return new SendResponse(true);
		} else if (type == Response.SEND_WRONG_RESPONSE) {
			return new SendResponse(false);
		} else if (type == Response.TEXT_RESPONSE){
			return new TextResponse(this.in.readUTF());
		} else if (type == Response.EMPTY_RESPONSE){
			return new EmptyResponse();
		} else if (type == Response.CREATE_QUEUE_RESPONSE){
			return new CreateQueueResponse(this.in.readInt());
		} else if (type == Response.DELETE_QUEUE_RESPONSE){
			return new DeleteQueueResponse(this.in.readBoolean());
		} else if (type == Response.QUERY_QUEUES_RESPONSE){
			ArrayList<Integer> ids = new ArrayList<Integer>();
			int size = this.in.readInt();
			for (int i=0; i < size; i++) {
				ids.add(this.in.readInt());
			}
			return new QueryQueuesForReceiverResponse(ids);
		} else if (type == Response.FRONTEND_RESPONSE) {
			return new FrontendResponse(this.in.readUTF());
		} else {
			return new InvalidResponse();
		}
	}

}
