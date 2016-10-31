package ethz.asl.benchmark.op;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import ethz.asl.client.Client;
import ethz.asl.message.impl.SendRequest;
import ethz.asl.message.impl.SendResponse;
import ethz.asl.util.Constants;
import ethz.asl.util.StringGenerator;


public class MWSendOp implements BenchmarkOp {

	private Client client;
	private List<Integer> queues;
	
	public MWSendOp(String host, int port) {
		this.queues = Constants.getQueues();
		try {
			this.client = new Client(host, port);
		} catch (IOException e) {
			this.client = null;
		}
	}
	
	private static Random random = new Random();
	private final static Logger LOGGER = Logger.getLogger(MWSendOp.class.getName());
	
	@Override
	public void execute() {
		try {
			int sender = Constants.generateUser();
			SendRequest req;
			if (random.nextFloat() < 0.8) {
				req = new SendRequest(sender, -1, Constants.generateUserQueue(queues, sender), StringGenerator.get(Constants.TEXTLENGTH));
			} else {
				req = new SendRequest(sender, Constants.getReceiver(sender), Constants.generateUserQueue(queues, sender), StringGenerator.get(Constants.TEXTLENGTH));
			}
			
			SendResponse response = (SendResponse) client.send(req);
			if (!response.isValid()) {
				LOGGER.warning("Invalid Response");
			}
		} catch (IOException e) {
			LOGGER.throwing("SendOperation", "execute", e);
		}
	}

}
