package ethz.asl.benchmark.op;

import java.io.IOException;
import java.util.logging.Logger;

import ethz.asl.client.Client;
import ethz.asl.message.Response;
import ethz.asl.message.impl.EmptyResponse;
import ethz.asl.message.impl.InvalidResponse;
import ethz.asl.message.impl.TextResponse;
import ethz.asl.message.impl.PeekFromSenderRequest;
import ethz.asl.util.Constants;


public class MWPeekOp implements BenchmarkOp {
	private Client client;
	
	public MWPeekOp(String host, int port) {
		try {
			this.client = new Client(host, port);
		} catch (IOException e) {
			this.client = null;
		}
	}
	private final static Logger LOGGER = Logger.getLogger(MWPeekOp.class.getName());
	
	@Override
	public void execute() {
		try {
			int receiver = Constants.generateUser();
			PeekFromSenderRequest req = new PeekFromSenderRequest(receiver, Constants.generateUser());
			Response resp = client.send(req);
			if (resp instanceof TextResponse) {
				TextResponse msg = (TextResponse) resp;
				if (msg.getText().length() != Constants.TEXTLENGTH) {
					LOGGER.severe("Invalid Response Length");
				}
			} else if (resp instanceof EmptyResponse) {
				LOGGER.info("Empty");
			} else if (resp instanceof InvalidResponse) {
				throw new RuntimeException("Invalid Response");
			}
		} catch (IOException e) {
		    LOGGER.severe(e.getMessage());
		}
	}

}
