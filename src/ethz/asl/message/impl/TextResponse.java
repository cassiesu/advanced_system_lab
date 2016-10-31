package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;

import ethz.asl.message.Response;

public class TextResponse implements Response {
	private String text;
	
	public TextResponse(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		s.writeByte(Response.TEXT_RESPONSE);
		s.writeUTF(this.text);
		s.flush();
	}
}
