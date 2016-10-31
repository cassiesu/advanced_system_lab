package ethz.asl.message;


import java.io.IOException;

/**
 * Request: Message Type
 * @author CassieSu
 *
 */
public interface Request extends MessageType{
	
	public byte[] getRequest() throws IOException;
	
}
