package ethz.asl.mw.frontend;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import ethz.asl.log.MiddlewareLogFactory;

public class ClientConn {
	private final static Logger LOGGER = Logger.getLogger(ClientConn.class.getName());

	private SelectionKey key;
	private SocketChannel channel;
	
	public SocketChannel getChannel() {
		return this.channel;
	}

	private ByteBuffer buffer;
	
	public ByteBuffer getBuffer() {
		this.buffer.rewind();
		return this.buffer;
	}
	
	int count;
	private int length;
	private byte[] data;
	private int offset;
	
	private final static int div = 1000;

	private String operation;
	
	private long requestSubmit;
	private long requestStart;
	private long databaseSubmit;
	private long databaseStart;
	private long responseSubmit;
	private long responseStart;
	private long responseFinish;
	
	public ClientConn(SocketChannel channel, SelectionKey key) {
		this.channel = channel;
		this.key = key;
		
		this.count = 0;
		this.length = -1;
		this.offset = 0;
		
		this.traceRequestSubmitTime();
		this.buffer = ByteBuffer.allocate(2000);
		this.data = new byte[2000];
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public void traceRequestSubmitTime() {
		if (count <= 0) {
			this.requestSubmit = System.nanoTime();
		}
	}
	
	public void traceRequestStartTime() {
		this.requestStart = System.nanoTime();
	}
	
	public void traceDatabaseSubmitTime() {
		this.databaseSubmit = System.nanoTime();
	}
	
	public void traceDatabaseStartTime() {
		this.databaseStart = System.nanoTime();
	}

	public void traceResponseSubmitTime() {
		this.responseSubmit = System.nanoTime();
	}

	public void traceResponseStartTime() {
		this.responseStart = System.nanoTime();
	}
	
	public void traceResponseDoneTime() {
		this.responseFinish = System.nanoTime();
	}
	
	public void close() {}
	
	public byte[] read(SocketChannel channel) {
		int read = 0;
		try {	
			while((read = channel.read(buffer)) > 0) {
				this.count += read;
				buffer.flip();

				while (buffer.hasRemaining()) {
					data[offset] = buffer.get();
					offset++;
				}
				buffer.flip();
				if (this.length == -1 && this.count < 4)	continue;
				if (this.count >= 4) {
					this.length = ByteBuffer.wrap(data, 0, 4).getInt();
				}
				if (this.count >= (this.length+4)) {
					this.count = 0;
					buffer.rewind();
					
					return data;
				}
			}
		} catch (IOException e) {
			LOGGER.warning("IOException"+ e.getMessage());
		}
		return null;
	}

	public void write(ByteArrayOutputStream os) throws IOException {
		byte[] bytes = os.toByteArray();
		ByteBuffer buf = ByteBuffer.allocate(bytes.length);
		buf.put(bytes);
		buf.flip();
		
		while(buf.hasRemaining()) {
		    this.channel.write(buf);
		}
	}
	
	public int getLength() {
		int len = this.length;
		this.length = -1;		//ready to accept next request
		this.offset = 0;
		return len + 4;
	}
	
	public void recordTime() {
		long requestQ = (requestStart - requestSubmit) / div;
		long requestD = (databaseSubmit - requestStart) / div;
		long databaseQ = (databaseStart - databaseSubmit) / div;
		long databaseD = (responseSubmit - databaseStart) / div;
		long responseQ = (responseStart - responseSubmit) / div;
		long responseD = (responseFinish - responseStart) / div;
		long totalTime = (responseFinish - requestSubmit) / div;
		
		MiddlewareLogFactory.recordTime(requestQ, requestD, databaseQ, databaseD, responseQ, responseD, totalTime, operation);

	}
	
}
