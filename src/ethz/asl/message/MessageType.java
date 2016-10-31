package ethz.asl.message;

public interface MessageType {
	
	//Message Type
	public static final byte SEND_BROADCAST_REQUEST = 1;
	public static final byte SEND_OK_RESPONSE = 2;
	public static final byte SEND_WRONG_RESPONSE = 3;
	public static final byte SEND_TO_RECEIVER_REQUEST = 4;
	
	public static final byte PEEK_FROM_QUEUE_REQUEST = 5;
	public static final byte PEEK_FROM_SENDER_REQUEST = 6;
	public static final byte TEXT_RESPONSE = 7;
	public static final byte EMPTY_RESPONSE = 8;
	public static final byte DELETE_FROM_QUEUE_REQUEST = 9;
	
	//Queue Type
	public static final byte CREATE_QUEUE_REQUEST = 10;
	public static final byte CREATE_QUEUE_RESPONSE = 11;
	public static final byte DELETE_QUEUE_REQUEST = 12;
	public static final byte DELETE_QUEUE_RESPONSE = 13;
	public static final byte QUERY_QUEUES_REQUEST = 14;
	public static final byte QUERY_QUEUES_RESPONSE = 15;
	
	//Test Type
	public static final byte FRONTEND_REQUEST = 16;
	public static final byte FRONTEND_RESPONSE = 17;
	
	//Invalid Type
	public static final byte INVALID = 0;
	
}
