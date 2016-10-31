package ethz.asl.message.impl;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Request;

public class DeleteQueueRequest implements Request {
	
	private int qid;
	
	public DeleteQueueRequest(int qid) {
		this.qid = qid;
	}

	@Override
	public byte[] getRequest() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream s = new DataOutputStream(bytes);
		
		s.writeByte(Request.DELETE_QUEUE_REQUEST);
		s.writeInt(this.qid);
		s.flush();	
		return bytes.toByteArray();
	}

	public int getQid() {
		return qid;
	}

	public void setQid(int qid) {
		this.qid = qid;
	}
}
