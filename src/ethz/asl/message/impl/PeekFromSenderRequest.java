package ethz.asl.message.impl;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Request;

public class PeekFromSenderRequest implements Request {

	private int sender;
	private int receiver;
	
	public int getSender() {
		return sender;
	}

	public int getReceiver() {
		return receiver;
	}
	
	
	public void setSender(int sender) {
		this.sender = sender;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public PeekFromSenderRequest(int sender, int queue) {
		super();
		this.sender = sender;
		this.receiver = queue;
	}

	
	@Override
	public byte[] getRequest() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream s = new DataOutputStream(bytes);
		
		s.writeByte(Request.PEEK_FROM_SENDER_REQUEST);
		s.writeInt(this.sender);
		s.writeInt(this.receiver);
		s.flush();	
		return bytes.toByteArray();
	}
}
