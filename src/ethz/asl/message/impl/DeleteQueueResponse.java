package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Response;

public class DeleteQueueResponse implements Response {
	private boolean valid;
	
	public DeleteQueueResponse(boolean valid) {
		this.valid = valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isValid() {
		return this.valid;
	}

	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		s.writeByte(Response.DELETE_QUEUE_RESPONSE);
		s.writeBoolean(this.valid);
		s.flush();
	}
}
