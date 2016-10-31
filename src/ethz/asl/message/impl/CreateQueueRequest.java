package ethz.asl.message.impl;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Request;

public class CreateQueueRequest implements Request {
	
	@Override
	public byte[] getRequest() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream s = new DataOutputStream(bytes);
		
		s.writeByte(Request.CREATE_QUEUE_REQUEST);
		s.flush();	
		return bytes.toByteArray();
	}
}
