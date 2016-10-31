package ethz.asl.mw.main;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ethz.asl.log.MiddlewareLogFactory;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConnFactory;
import ethz.asl.util.Constants;


public class MiddlewareMain {
	
	private final static Logger LOGGER = Logger.getLogger(MiddlewareMain.class.getName());
	public static int PORT;
	
	public static void main(String args[]) {
		try {
	        LogManager.getLogManager().readConfiguration(new FileInputStream("logger.properties"));
	        
	        if (args.length != 6) {
	        	LOGGER.warning("To Start Server: arg1: benchmark, arg2: runner machine, arg3: port number, arg4: db_ip, arg5: frontworkers, arg6: backworkers");
	        } else {
	        	Constants.DATABASE_URL = "jdbc:postgresql://" + args[3] + ":5432/asl?user=postgres&password=";
	        	Constants.FWORKERS = Integer.parseInt(args[4]);
	        	Constants.BWORKERS = Integer.parseInt(args[5]);
	        	
	        	PORT = Integer.parseInt(args[2]);
				ServerSocketChannel ssc = ServerSocketChannel.open();
				ssc.socket().bind(new InetSocketAddress(PORT));
				
				Middleware server = new Middleware(ssc, new ClientConnFactory());
				MiddlewareLogFactory.benchmark = args[0];
		        MiddlewareLogFactory.machine = Integer.parseInt(args[1]);
		        
				LOGGER.info("Server Started");
				server.run();
	        }
		} catch (IOException e) {
			LOGGER.severe("Server Fail To Start");
		}
	}
}
