package ethz.asl.message.impl;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Request;

public class FrontendRequest implements Request {
	private String echo;
	
	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public FrontendRequest(String s) {
		this.echo = s;
	}

	@Override
	public byte[] getRequest() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream s = new DataOutputStream(bytes);
		
		s.writeByte(Request.FRONTEND_REQUEST);
		s.writeUTF(this.echo);
		s.flush();	
		return bytes.toByteArray();
	}
	
}
