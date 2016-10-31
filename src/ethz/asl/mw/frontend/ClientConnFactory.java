package ethz.asl.mw.frontend;


import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ClientConnFactory {
	
	public ClientConn createConn (SocketChannel channel, SelectionKey key) {
		return new ClientConn(channel, key);
		
	}
}
