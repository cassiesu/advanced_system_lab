package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Response;

public class SendResponse implements Response {
	private boolean valid;
	
	public SendResponse(boolean valid) {
		this.valid = valid;
	}
	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	

	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		if (valid) {
			s.writeByte(Response.SEND_OK_RESPONSE);
		} else {
			s.writeByte(Response.SEND_WRONG_RESPONSE);
		}
		s.flush();
	}
}