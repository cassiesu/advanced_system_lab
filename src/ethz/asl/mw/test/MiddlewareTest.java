package ethz.asl.mw.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ethz.asl.client.Client;
import ethz.asl.db.Queue;
import ethz.asl.message.Response;
import ethz.asl.message.impl.CreateQueueRequest;
import ethz.asl.message.impl.CreateQueueResponse;
import ethz.asl.message.impl.DeleteQueueRequest;
import ethz.asl.message.impl.DeleteQueueResponse;
import ethz.asl.message.impl.EmptyResponse;
import ethz.asl.message.impl.InvalidRequest;
import ethz.asl.message.impl.InvalidResponse;
import ethz.asl.message.impl.TextResponse;
import ethz.asl.message.impl.PeekFromQueueRequest;
import ethz.asl.message.impl.PeekFromSenderRequest;
import ethz.asl.message.impl.DeleteFromQueueRequest;
import ethz.asl.message.impl.QueryQueuesForReceiverRequest;
import ethz.asl.message.impl.QueryQueuesForReceiverResponse;
import ethz.asl.message.impl.SendRequest;
import ethz.asl.message.impl.SendResponse;
import ethz.asl.mw.Middleware;
import ethz.asl.mw.frontend.ClientConnFactory;
import ethz.asl.util.DBConnHelper;


public class MiddlewareTest {
	
	private Thread server;
	private ServerSocketChannel ssc;
	private final int PORT = 6666;
	
	private Connection conn = DBConnHelper.getConn();
	private int queue_id = 1;
	
    @Before
    public void prepareConn() throws Exception {
		try {
			Statement stmt = conn.createStatement();
	        stmt.execute("DELETE FROM message;");
	        stmt.execute("DELETE FROM queue;");
	        stmt.execute("INSERT INTO queue(id, creator) VALUES("+queue_id+",1)");
	        stmt.execute("INSERT INTO queue(id, creator) VALUES("+(queue_id+1)+",1)");
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		ssc = ServerSocketChannel.open();
		ssc.socket().bind(new InetSocketAddress(PORT));
		server = new Thread(new Middleware(ssc, new ClientConnFactory()));
        server.start();
    }
    
    @After
    public void closeConn() throws Exception {
    	ssc.close();
    	Thread.sleep(100);
   
		try {
			Statement stmt = conn.createStatement();
	        stmt.execute("DELETE FROM message;");
	        stmt.execute("DELETE FROM queue;");
		} catch (SQLException e) {
			fail();
		}
    }
 
    @Test
	public void testCreateDeleteQueueRequest() throws IOException {
		Client client = new Client("localhost", PORT);
		
		Response resp = client.send(new CreateQueueRequest());
		assertTrue(resp instanceof CreateQueueResponse);
		
		int id = ((CreateQueueResponse) resp).getId();
		
		resp = client.send(new DeleteQueueRequest(id));
		assertTrue(resp instanceof DeleteQueueResponse);
		assertTrue(((DeleteQueueResponse) resp).isValid());
		
		resp = client.send(new SendRequest(3, 5, id, ""));
		assertTrue(resp instanceof SendResponse);
		assertFalse(((SendResponse) resp).isValid());
	}
	
	
	@Test
	public void testQueryQueueRequest() throws IOException {
		
		Client client = new Client("localhost", PORT);
		Response resp = client.send(new CreateQueueRequest());
		assertTrue(resp instanceof CreateQueueResponse);
		
		QueryQueuesForReceiverResponse queues = (QueryQueuesForReceiverResponse) client.send(new QueryQueuesForReceiverRequest(5));
		assertEquals(0, queues.getIds().size());
		
		String text = "helloworld";
		resp = client.send(new SendRequest(4, 2, queue_id, text));
		assertTrue(resp instanceof SendResponse);
		
		resp = client.send(new SendRequest(4, 2, queue_id, text));
		assertTrue(resp instanceof SendResponse);
		
		queues = (QueryQueuesForReceiverResponse) client.send(new QueryQueuesForReceiverRequest(2));
		assertEquals(1, queues.getIds().size());
		assertEquals(queue_id, (int) queues.getIds().get(0));
		
	}
	
	@Test
	public void testSendPeekMessageRequest() throws IOException {
		String text = "helloworld";
		Client client = new Client("localhost", PORT);
		
		Response resp = client.send(new SendRequest(2, 2, queue_id, text));
		resp = client.send(new PeekFromQueueRequest(2, queue_id));
		
		assertTrue(resp instanceof TextResponse);
		assertEquals(text, ((TextResponse) resp).getText());
		
		resp = client.send(new SendRequest(3, 5, queue_id, text));
		assertTrue(resp instanceof SendResponse);
		
		resp = client.send(new PeekFromSenderRequest(3, 5));
		assertTrue(resp instanceof TextResponse);
		assertEquals(text, ((TextResponse) resp).getText());
	}
	
	
	@Test
	public void testSendDeleteMessageRequest() throws IOException {
		String text = "helloworld";
		Client client = new Client("localhost", PORT);
		Response resp = client.send(new SendRequest(10, 5, queue_id, text));
		assertTrue(resp instanceof SendResponse);
		
		resp = client.send(new DeleteFromQueueRequest(5, queue_id));
		assertTrue(resp instanceof TextResponse);
		assertEquals(text, ((TextResponse) resp).getText());
		
		resp = client.send(new DeleteFromQueueRequest(5, queue_id));
		assertTrue(resp instanceof EmptyResponse);
	}
	
	@Test
	public void testInvalidMessageRequest() throws IOException {
		Client client = new Client("localhost", PORT);
		assertTrue(client.send(new InvalidRequest()) instanceof InvalidResponse);
	}
	
}
