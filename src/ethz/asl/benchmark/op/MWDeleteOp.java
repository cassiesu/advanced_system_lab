package ethz.asl.benchmark.op;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import ethz.asl.client.Client;
import ethz.asl.message.Response;
import ethz.asl.message.impl.EmptyResponse;
import ethz.asl.message.impl.InvalidResponse;
import ethz.asl.message.impl.TextResponse;
import ethz.asl.message.impl.DeleteFromQueueRequest;
import ethz.asl.util.Constants;


public class MWDeleteOp implements BenchmarkOp {
	
	public MWDeleteOp(String host, int port) {
		this.queues = Constants.getQueues();
		try {
			this.client = new Client(host, port);
		} catch (IOException e) {
			this.client = null;
		}
	}
	
	private Client client;
	private List<Integer> queues;
	private final static Logger LOGGER = Logger.getLogger(MWDeleteOp.class.getName());
	
	@Override
	public void execute() {
		try {
			int receiver = Constants.generateUser();
			DeleteFromQueueRequest req = new DeleteFromQueueRequest(receiver, Constants.generateUserQueue(queues, receiver));
			Response resp = client.send(req);
			if (resp instanceof TextResponse) {
				TextResponse msg = (TextResponse) resp;
				if (msg.getText().length() != Constants.TEXTLENGTH) {
					LOGGER.severe("Invalid Response Length");
				}
			} else if (resp instanceof EmptyResponse) {
				LOGGER.info("Empty");
			} else if (resp instanceof InvalidResponse) {
				LOGGER.severe("Invalid Response");
			}
		} catch (IOException e) {
		    LOGGER.severe(e.getMessage());
		}
	}

}
