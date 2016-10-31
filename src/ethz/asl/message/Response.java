package ethz.asl.message;


import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Response: Message Type
 * @author CassieSu
 *
 */
public interface Response extends MessageType{

	public void sendResponse(DataOutputStream s) throws IOException;
	
}