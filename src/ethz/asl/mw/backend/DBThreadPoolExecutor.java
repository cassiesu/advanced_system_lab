package ethz.asl.mw.backend;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DBThreadPoolExecutor extends ThreadPoolExecutor {
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		DBConnThread dbT = (DBConnThread) t;
		DBTask dbR = (DBTask) r;
		dbR.conn = dbT.conn;
	}

	public DBThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, (ThreadFactory) new DBConnThreadFactory());
	}
	
}
