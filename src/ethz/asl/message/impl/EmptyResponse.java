package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Response;

public class EmptyResponse implements Response {

	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		s.writeByte(Response.EMPTY_RESPONSE);
		s.flush();
	}

}
