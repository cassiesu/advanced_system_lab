package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Response;

public class InvalidResponse implements Response {

	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		s.writeByte(Response.INVALID);
		s.flush();
	}
}
