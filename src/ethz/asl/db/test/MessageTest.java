package ethz.asl.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ethz.asl.db.Message;
import ethz.asl.db.Queue;
import ethz.asl.util.DBConnHelper;

public class MessageTest {

	private Connection con;
	private int queue_id;
	
    @Before
    public void prepareConn() {
        con = DBConnHelper.getConn();
		queue_id = Queue.createQueue(con, 1);
    }
    
    @After
    public void closeConn() {
        Statement stmt;
		try {
			stmt = con.createStatement();
	        stmt.execute("DELETE FROM message;");
	        stmt.execute("DELETE FROM queue;");
		} catch (SQLException e) {
			fail();
		}
    }
	
	@Test
	public void testSendMessage() {
		String text = "helloworld";
		
		assertTrue(Message.sendMessageBroadcast(con, 3, queue_id, text));
		try {
			Statement stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT sender, queue_id, message FROM message");
			if (res.next()) {
				assertEquals(3, res.getInt(1));
				assertEquals(queue_id, res.getInt(2));
				assertEquals(text, res.getString(3));
			} else {
				fail();
			}
		} catch (SQLException e) {
			fail();
		}
	}
	
	@Test
	public void testSendDeleteMessage() {
		String text = "helloworld";
		
		int sender = 2;
		int receiver = 20;
		assertTrue(Message.sendMessageToReceiver(con, sender, receiver, queue_id, text));
		assertEquals(null, Message.deleteMessageFromQueue(con, queue_id, -66));

		Message msg = Message.deleteMessageFromQueue(con, queue_id, receiver);
		assertEquals(sender, msg.getSender());
		assertEquals(text, msg.getText());
		assertEquals(null, Message.deleteMessageFromQueue(con, queue_id, receiver));
	}
	
	@Test
	public void testSendPeekMessage() {
		String text = "helloworld";
		
		int sender = 2;
		
		assertTrue(Message.sendMessageBroadcast(con, sender, queue_id, text));
		Message msg = Message.peekMessageFromQueue(con, queue_id, 1);
		assertEquals(sender, msg.getSender());
		assertEquals(text, msg.getText());
		
		msg = Message.peekMessageFromQueue(con, queue_id, 1);
		assertEquals(sender, msg.getSender());
		assertEquals(text, msg.getText());		
	}
	
	@Test
	public void testSendPeekMessageReceiver() {
		String text = "helloworld";
		
		int sender = 3;
		int receiver = 30;
		assertTrue(Message.sendMessageToReceiver(con, sender, receiver, queue_id, text));
		
		Message msg = Message.peekMessageFromQueue(con, queue_id, receiver);
		assertEquals(sender, msg.getSender());
		assertEquals(text, msg.getText());
		
		msg = Message.peekMessageFromQueue(con, queue_id, receiver);
		assertEquals(sender, msg.getSender());
		assertEquals(text, msg.getText());	
		
		assertEquals(null, Message.peekMessageFromQueue(con, queue_id, -99));
	}
	
	
	@Test
	public void testSendPeekMessageSender() {
		String text = "helloworld";
		
		int sender = 5;
		int receiver = 99;
		assertTrue(Message.sendMessageToReceiver(con, sender, receiver, queue_id, text));
		
		Message msg = Message.peekMessageFromSender(con, sender, receiver);
		assertEquals(sender, msg.getSender());
		assertEquals(text, msg.getText());
	}
	
}
