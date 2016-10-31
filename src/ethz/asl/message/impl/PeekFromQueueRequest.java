package ethz.asl.message.impl;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Request;

public class PeekFromQueueRequest implements Request {

	private int receiver;
	private int queue;
	
	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public void setQueue(int queue) {
		this.queue = queue;
	}

	public PeekFromQueueRequest(int receiver, int queue) {
		super();
		this.receiver = receiver;
		this.queue = queue;
	}

	public int getReceiver() {
		return receiver;
	}

	public int getQueue() {
		return queue;
	}
	
	@Override
	public byte[] getRequest() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream s = new DataOutputStream(bytes);
		
		s.writeByte(Request.PEEK_FROM_QUEUE_REQUEST);
		s.writeInt(this.receiver);
		s.writeInt(this.queue);
		s.flush();	
		return bytes.toByteArray();
	}
}
