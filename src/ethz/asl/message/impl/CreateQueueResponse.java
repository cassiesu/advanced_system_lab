package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Response;

public class CreateQueueResponse implements Response {
	private int qid;
	
	public CreateQueueResponse(int qid) {
		this.qid = qid;
	}

	public boolean isValid() {
		return this.qid > 0;
	}
	
	public int getQid() {
		return qid;
	}

	public void setQid(int qid) {
		this.qid = qid;
	}

	public int getId() {
		return this.qid;
	}
	
	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		s.writeByte(Response.CREATE_QUEUE_RESPONSE);
		s.writeInt(this.qid);
		s.flush();
	}
}
