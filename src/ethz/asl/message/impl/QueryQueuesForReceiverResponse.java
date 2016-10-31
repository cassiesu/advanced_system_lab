package ethz.asl.message.impl;


import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import ethz.asl.message.Response;

public class QueryQueuesForReceiverResponse implements Response {
	private List<Integer> ids;
	
	public QueryQueuesForReceiverResponse(List<Integer> ids) {
		this.ids = ids;
	}
	
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	
	
	public List<Integer> getIds() {
		return this.ids;
	}

	@Override
	public void sendResponse(DataOutputStream s) throws IOException {
		s.writeByte(Response.QUERY_QUEUES_RESPONSE);
		
		s.writeInt(this.ids.size());
		for (int i : this.ids) {
			s.writeInt(i);
		}
		s.flush();
	}

}
