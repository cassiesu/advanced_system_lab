package ethz.asl.benchmark.op;

import java.io.IOException;
import java.util.logging.Logger;

import ethz.asl.client.Client;
import ethz.asl.message.Response;
import ethz.asl.message.impl.FrontendRequest;
import ethz.asl.message.impl.FrontendResponse;
import ethz.asl.util.Constants;
import ethz.asl.util.StringGenerator;


public class MWFrontendOp implements BenchmarkOp {
	private final static Logger LOGGER = Logger.getLogger(MWFrontendOp.class.getName());

	private Client client;
	
	public MWFrontendOp(String host, int port) {
		try {
			this.client = new Client(host, port);
		} catch (IOException e) {
		}
	}
	
	@Override
	public void execute() {
		try {
			FrontendRequest req = new FrontendRequest(StringGenerator.get(Constants.TEXTLENGTH));
			Response resp = client.send(req);
			if (resp instanceof FrontendResponse) {
				FrontendResponse msg = (FrontendResponse) resp;
				if (msg.getEcho().length() != Constants.TEXTLENGTH) {
					LOGGER.severe("Invalid Response Length");
				}
			} else {
				throw new RuntimeException("Invalid Response");
			}
		} catch (IOException e) {
		    LOGGER.severe(e.getMessage());
		}
	}

}
