package ethz.asl.message.impl;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Request;

public class QueryQueuesForReceiverRequest implements Request {
	private int receiver;

	public int getReceiver() {
		return this.receiver;
	}
	
	public QueryQueuesForReceiverRequest(int receiver) {
		this.receiver = receiver;
	}

	
	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	@Override
	public byte[] getRequest() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream s = new DataOutputStream(bytes);
		
		s.writeByte(Request.QUERY_QUEUES_REQUEST);
		s.writeInt(receiver);
		s.flush();	
		return bytes.toByteArray();
	}

}
