package ethz.asl.benchmark.simple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import ethz.asl.client.Client;
import ethz.asl.message.Response;
import ethz.asl.message.impl.CreateQueueRequest;
import ethz.asl.message.impl.CreateQueueResponse;
import ethz.asl.message.impl.FrontendRequest;
import ethz.asl.message.impl.PeekFromQueueRequest;
import ethz.asl.message.impl.DeleteFromQueueRequest;
import ethz.asl.message.impl.QueryQueuesForReceiverRequest;
import ethz.asl.message.impl.SendRequest;


public class MWSimpleBench {
	
	private final static Logger LOGGER = Logger.getLogger(MWSimpleBench.class.getName());
	private static int port;
	private static String host;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		host = args[0];
		port = Integer.parseInt(args[1]);
		
		Client client = new Client(host, port);
		FileWriter w = new FileWriter(new File("MW-Simple-Bench.log"));
		
		Response resp = client.send(new CreateQueueRequest());
		int queue_id = ((CreateQueueResponse) resp).getId();
		int COUNT = 8000;
		
		LOGGER.info("Start Frontend");
		for (int i=0; i < COUNT; i++) {
			long start = System.nanoTime();
			client.send(new FrontendRequest("helloworld"));
			w.write(((System.nanoTime()-start) / 1000) +" \n");
			Thread.sleep(1);
		}
		
		w.close();
	}
}
