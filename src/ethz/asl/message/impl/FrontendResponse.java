package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Response;

public class FrontendResponse implements Response {
	private String echo;
	
	public String getEcho() {
		return this.echo;
	}
	
	public void setEcho(String echo) {
		this.echo = echo;
	}

	public FrontendResponse(String s) {
		this.echo = s;
	}

	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		s.writeByte(Response.FRONTEND_RESPONSE);
		s.writeUTF(this.echo);
		s.flush();
	}

}