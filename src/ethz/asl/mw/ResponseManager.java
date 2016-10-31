package ethz.asl.mw;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import ethz.asl.message.Response;
import ethz.asl.mw.frontend.ClientConn;



public class ResponseManager implements Runnable {
	
	public ResponseManager(ClientConn con, Response resp) {
		this.conn = con;
		this.resp = resp;
	}
	
	private ClientConn conn;
	private Response resp;
	
	private final static Logger LOGGER = Logger.getLogger(ResponseManager.class.getName());

	@Override
	public void run() {
			conn.traceResponseStartTime();
			
			try {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				DataOutputStream s = new DataOutputStream(bytes);
				resp.sendResponse(s);
				conn.write(bytes);
				
			} catch (IOException e) {
				LOGGER.severe("Connection Fails!" + e.getMessage());
			}
			
			conn.traceResponseDoneTime();
			conn.recordTime();

	}
}
