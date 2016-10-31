package ethz.asl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Constants {	

	public static int TEXTLENGTH = 100;
	public static int USERNUM = 30;
	public static int QUEUENUM = 20;
	
	public static long WARMUP = 30000;			//ms
	public static long COOLDOWN = 30000;		//ms
	public static long DURATION = 300000;		//ms
	public static long THINKING = 5;			//ms
	public static String DATABASE_URL = "jdbc:postgresql://localhost:5432/asl?user=postgres&password=";
	public static int FWORKERS = 10;
	public static int BWORKERS = 10;
	
	private static Random random = new Random();

	public static int generateUserQueue(List<Integer> queues, int user) {
		if (user < (Constants.USERNUM / 2)) {
			return queues.get(random.nextInt(Constants.QUEUENUM/2));
		} else {
			return queues.get(random.nextInt(
					(Constants.QUEUENUM/2)+Constants.QUEUENUM/2));
		}
	}
	
	public static int getReceiver(int sender) {
		if (sender < (Constants.USERNUM / 2)) {
			return random.nextInt(Constants.USERNUM/2);
		} else {
			return (Constants.USERNUM/2) + random.nextInt(Constants.USERNUM/2);
		}
	}
	
	public static int generateUser() {
		return random.nextInt(Constants.USERNUM);
	}
	
	public static ArrayList<Integer> getQueues(){
		ArrayList<Integer> queues = new ArrayList<Integer>();
		for (int i=1; i <= Constants.QUEUENUM; i++) {
			queues.add(i);
		}
		return queues;
	}
	
}
