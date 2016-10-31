package ethz.asl.mw;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import ethz.asl.mw.backend.DBThreadPoolExecutor;
import ethz.asl.mw.frontend.ClientConn;
import ethz.asl.mw.frontend.ClientConnFactory;
import ethz.asl.util.Constants;


public class Middleware implements Runnable {
	
	private final static Logger LOGGER = Logger.getLogger(Middleware.class.getName());

	private ServerSocketChannel ssc;
	private ClientConnFactory ccf;
	
	public static ExecutorService FRONTEND_EXECUTOR_POOL;
	public static ExecutorService BACKEND_EXECUTOR_POOL;
	

	public Middleware(ServerSocketChannel ssc, ClientConnFactory ccf) {
		this.ssc = ssc;
		this.ccf = ccf;
		FRONTEND_EXECUTOR_POOL = Executors.newFixedThreadPool(Constants.FWORKERS);
		BACKEND_EXECUTOR_POOL = new DBThreadPoolExecutor(Constants.BWORKERS, Constants.BWORKERS, 0, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public void run() {
		LOGGER.info("Start Run Server.");
		try {
			//Non-Blocking Server Socket
			Selector selector = SelectorProvider.provider().openSelector();
			ssc.configureBlocking(false)
				.register(selector, SelectionKey.OP_ACCEPT);	
			
			while(true) {
				if(selector.select() == 0) continue;		//wait until channels ready
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				
				while(keys.hasNext()) {
				    SelectionKey key = keys.next();
				    if (!key.isValid())	continue;
				    
				    if(key.isAcceptable()) {
					    LOGGER.info("Ready To Accept New Connection");
						
					    SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
						channel.configureBlocking(false)
							.register(selector, SelectionKey.OP_READ);
						
				    } else if (key.isReadable()) {
				    	ClientConn conn = (ClientConn) key.attachment();
				    	
				    	if (conn == null) {
				    		conn = ccf.createConn((SocketChannel) key.channel(), key);
				    		key.attach(conn);
				    		LOGGER.info("CREATE "+conn.toString()+" "+key.toString());
				    	}
						
						byte[] data = conn.read((SocketChannel) key.channel());
						
						conn.traceRequestSubmitTime();
						if (data != null) {
							Middleware.FRONTEND_EXECUTOR_POOL.execute(new RequestManager(conn, data, conn.getLength()));
						}
				    }
				    
				    keys.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.ssc.close();
		} catch (IOException e) {
		}
		
		FRONTEND_EXECUTOR_POOL.shutdown();
		BACKEND_EXECUTOR_POOL.shutdown();
	}
}
