package ethz.asl.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ethz.asl.db.Message;
import ethz.asl.db.Queue;
import ethz.asl.util.DBConnHelper;

public class QueueTest {

	private Connection con;
	
    /**
     * Sets up the test fixture. 
     * (Called before every test case method.)
     */
    @Before
    public void prepareConn() {
        con = DBConnHelper.getConn();
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
	public void testCreateQueue() {
		int creator = 10;
		int queue = Queue.createQueue(con, creator);
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT creator FROM queue WHERE id = "+queue);
			res.next();
			assertEquals(creator, res.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testDeleteQueue() {
		int creator = 10;
		int queue = Queue.createQueue(con, creator);
		assertTrue(Queue.deleteQueue(con, queue));
		Statement stmt;
		try {
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT count(*) FROM queue");
			res.next();
			assertEquals(0, res.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testQueryQueuesForReceiver() {
		int creator = 10;
		int queue1 = Queue.createQueue(con, creator);
		Message.sendMessageToReceiver(con, 1, 3, queue1, "hallo");
		
		int queue2 = Queue.createQueue(con, creator);
		Message.sendMessageToReceiver(con, 1, 3, queue2, "hallo");
	
		int queue3 = Queue.createQueue(con, creator);
		Message.sendMessageBroadcast(con, 1, queue3, "hallo");
		
		ArrayList<Integer> res = Queue.queryQueuesForReceiver(con, 3);
		assertEquals(2, res.size());
		assertTrue(res.contains(queue1));
		assertTrue(res.contains(queue2));
	}
}
