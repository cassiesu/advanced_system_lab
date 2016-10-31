package ethz.asl.benchmark.op;

import java.io.IOException;
import java.util.logging.Logger;

import ethz.asl.client.Client;
import ethz.asl.message.Response;
import ethz.asl.message.impl.InvalidResponse;
import ethz.asl.message.impl.QueryQueuesForReceiverRequest;
import ethz.asl.message.impl.QueryQueuesForReceiverResponse;
import ethz.asl.util.Constants;


public class MWQueryOp implements BenchmarkOp {
	private final static Logger LOGGER = Logger.getLogger(MWQueryOp.class.getName());
	private Client client;
	
	public MWQueryOp(String host, int port) {
		try {
			this.client = new Client(host, port);
		} catch (IOException e) {
			this.client = null;
		}
	}
	
	@Override
	public void execute() {	
		try {
			int receiver = Constants.generateUser();
			Response resp = client.send(new QueryQueuesForReceiverRequest(receiver));
			if (resp instanceof QueryQueuesForReceiverResponse) {
				QueryQueuesForReceiverResponse msg = (QueryQueuesForReceiverResponse) resp;
				if (msg.getIds().size() == 0) {
					LOGGER.info("Non Queue Contains Message For Receiver");
				}
			} else if (resp instanceof InvalidResponse) {
				throw new RuntimeException("Invalid Response");
			}
		} catch (IOException e) {
		    LOGGER.severe(e.getMessage());
		}
	}

}
