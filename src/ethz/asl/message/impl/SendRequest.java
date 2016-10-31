package ethz.asl.message.impl;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Request;

public class SendRequest implements Request {
	private int sender;
	private int receiver;
	private int queue;
	private String message;
	
	public void setSender(int sender) {
		this.sender = sender;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public void setQueue(int queue) {
		this.queue = queue;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getSender() {
		return sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public int getQueue() {
		return queue;
	}

	public String getMessage() {
		return message;
	}

	public SendRequest(int sender, int receiver, int queue, String message) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.queue = queue;
		this.message = message;
	}

	@Override
	public byte[] getRequest() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream s = new DataOutputStream(bytes);
		
		if (this.receiver < 0) {
			s.writeByte(Request.SEND_BROADCAST_REQUEST);
			s.writeInt(this.sender);
			s.writeInt(this.queue);
			s.writeUTF(this.message);
		} else {
			s.writeByte(Request.SEND_TO_RECEIVER_REQUEST);
			s.writeInt(this.sender);
			s.writeInt(this.receiver);
			s.writeInt(this.queue);
			s.writeUTF(this.message);
		}
		
		s.flush();	
		return bytes.toByteArray();
	}
}