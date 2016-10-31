package ethz.asl.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Message {
	
	private final static Logger LOGGER = Logger.getLogger(Message.class.getName());
	
	private int id;
	private int sender;
	private String message;

	public Message(int id, int sender, String text) {
		this.id = id;
		this.sender = sender;
		this.message = text;
	}

	public static boolean sendMessageBroadcast(Connection con, int sender, int queue, String text) {
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM send_message_broadcast(?, ?, ?)");
			stmt.setInt(1, sender);
			stmt.setInt(2, queue);
			stmt.setString(3, text);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			LOGGER.severe("Error: Sending Message\n" + e.getMessage());
			return false;
		}
	}
	
	public static boolean sendMessageToReceiver(Connection con, int sender, int receiver, int queue, String text) {
		try {
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM send_message_to_receiver(?, ?, ?, ?)");
			stmt.setInt(1, sender);
			stmt.setInt(2, receiver);
			stmt.setInt(3, queue);
			stmt.setString(4, text);
			stmt.execute();
			return true;
		} catch (SQLException e) {
			LOGGER.severe("Error: Sending Message\n" + e.getMessage());
			return false;
		}
	}
	
	public static Message deleteMessageFromQueue(Connection con, int queue, int receiver) {
		try {
			PreparedStatement popStmt = con.prepareStatement("SELECT * FROM delete_message_from_queue(?, ?)");
			popStmt.setInt(1, queue);
			popStmt.setInt(2, receiver);
			ResultSet res = popStmt.executeQuery();
			
			if (res.next()) {
				return new Message(res.getInt(1), res.getInt(2), res.getString(3));
			} else {
				return null;
			}
		} catch (SQLException e) {
			LOGGER.severe("Error: Deleting Message\n" + e.getMessage());
			return null;
		}	
	}
	
	public static Message peekMessageFromQueue(Connection con, int queue, int receiver) {
		try {
			PreparedStatement popStmt = con.prepareStatement("SELECT * FROM peek_message_from_queue(?, ?)");
			popStmt.setInt(1, queue);
			popStmt.setInt(2, receiver);
			ResultSet res = popStmt.executeQuery();
			
			if (res.next()) {
				return new Message(res.getInt(1), res.getInt(2), res.getString(3));
			} else {
				return null;
			}
		} catch (SQLException e) {
			LOGGER.severe("Error: Peeking Message\n"+ e.getMessage());
			return null;
		}
	}
	
	public static Message peekMessageFromSender(Connection con, int sender, int receiver) {
		try {
			PreparedStatement popStmt = con.prepareStatement("SELECT * FROM peek_message_from_sender(?, ?)");
			popStmt.setInt(1, sender);
			popStmt.setInt(2, receiver);
			ResultSet res = popStmt.executeQuery();
			
			if (res.next()) {
				return new Message(res.getInt(1), res.getInt(2), res.getString(3));
			} else {
				return null;
			}
		} catch (SQLException e) {
			LOGGER.severe("Error: Peeking Message\n" + e.getMessage());
			return null;
		}
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getSender() {
		return sender;
	}

	public String getText() {
		return message;
	}
	

}
