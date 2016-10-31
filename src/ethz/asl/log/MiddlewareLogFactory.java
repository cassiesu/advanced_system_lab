package ethz.asl.log;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import ethz.asl.util.Constants;

public class MiddlewareLogFactory {
	
	private final static Logger LOGGER = Logger.getLogger(MiddlewareLogFactory.class.getName());
	
	private static MiddlewareLogFactory instance;
	
	private static MiddlewareLogFactory getInstance() {
		if (instance == null) {
			instance = new MiddlewareLogFactory();
		}
		return instance;
	}

	private static Timer timer = new Timer();
	private boolean isRunning = false;
	
	public static String benchmark;
	public static int machine;
	private static String dir;
	
	private HashMap<Long, MiddlewareLog> mwlogs;
	
	public static void recordTime(long requestQ, long requestD, long databaseQ, long databaseD, long responseQ, long responseD, long totalTime, String op) {
		if (getInstance().isRunning) {
			MiddlewareLog log = getLog(Thread.currentThread());
			log.recordTime(requestQ, requestD, databaseQ, databaseD, responseQ, responseD, totalTime, op);
		}
	}
	
	private static MiddlewareLog getLog(Thread t) {
		MiddlewareLogFactory logs = getInstance();
		if (!logs.mwlogs.containsKey(t.getId())) {
			synchronized (logs) {
				logs.mwlogs.put(t.getId(), new MiddlewareLog(dir, machine));
			}
		}
		return logs.mwlogs.get(t.getId());
	}
	
	private MiddlewareLogFactory() {
		final MiddlewareLogFactory inst = this;
		
		mwlogs = new HashMap<Long, MiddlewareLog>();
		dir = "server-"+benchmark;
		
		timer.schedule(new TimerTask() {
			public void run() {
				inst.isRunning = true;
			}
		}, Constants.WARMUP);
		
		LOGGER.info("Finish Changing Running State");

		timer.schedule(new TimerTask() {
			public void run() {
				inst.isRunning = false;
				for (MiddlewareLog log : inst.mwlogs.values()) {
					timer.cancel();
					log.requestQLog.close();
					log.requestDLog.close();
					log.databaseQLog.close();
					log.databaseDLog.close();
					log.databaseQLog.close();
					log.databaseDLog.close();
				}
			}
		}, Constants.WARMUP + Constants.DURATION);
		
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				for (MiddlewareLog log : inst.mwlogs.values()) {
					log.requestQLog.appendSeperator("--------");		log.requestQLog.flush();
					log.requestDLog.appendSeperator("--------");		log.requestDLog.flush();
					log.databaseQLog.appendSeperator("--------");		log.databaseQLog.flush();
					log.databaseDLog.appendSeperator("--------");		log.databaseDLog.flush();
					log.responseQLog.appendSeperator("--------");	log.responseQLog.flush();
					log.responseDLog.appendSeperator("--------");	log.responseDLog.flush();
					log.totalTimeLog.appendSeperator("--------");	log.totalTimeLog.flush();
				}
			}
		}, Constants.WARMUP + 1000, 1000);
		
	}

}
