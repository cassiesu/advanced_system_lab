package ethz.asl.benchmark.db;

import java.io.IOException;

import ethz.asl.benchmark.ExecutionTask;
import ethz.asl.benchmark.MaxRequestGenerator;
import ethz.asl.benchmark.op.BenchmarkOp;
import ethz.asl.benchmark.op.DBDeleteMessageOp;
import ethz.asl.benchmark.op.DBEmptyOp;
import ethz.asl.benchmark.op.DBPeekMessageOp;
import ethz.asl.benchmark.op.DBQueryQueuesOp;
import ethz.asl.benchmark.op.DBSendMessageOp;
import ethz.asl.log.BenchLog;
import ethz.asl.util.Constants;

public class DBBenchSingle {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final int benchmark = Integer.parseInt(args[0]);
		final int machine = Integer.parseInt(args[1]);
		final int parallel_threads = Integer.parseInt(args[2]);
		Constants.DATABASE_URL = "jdbc:postgresql://" + args[4] + ":5432/asl?user=postgres&password=";

		System.out.println("Running Benchmark "+benchmark+" On Machine "+machine +" With "+parallel_threads+" parallel threads. Operation: "+args[3]);

		for (int i=0; i < parallel_threads; i++) {
			ExecutionTask task = getExecutionTask(args[3], benchmark, machine, i);
			Thread tre = new Thread( new MaxRequestGenerator( task, Constants.WARMUP, Constants.DURATION, Constants.COOLDOWN));
			tre.start();
		}
		
	}
	
	private static ExecutionTask getExecutionTask(String name, int benchmark, int machine, int threadnum) {
		
		BenchmarkOp op;
		if (name.equals("dbempty")) {
			op = new DBEmptyOp();
		} else if (name.equals("dbsend")){
			op = new DBSendMessageOp();
		} else if (name.equals("dbpeek")){
			op = new DBPeekMessageOp();
		} else if (name.equals("dbdelete")){
			op = new DBDeleteMessageOp();
		} else if (name.equals("dbquery")){
			op = new DBQueryQueuesOp();
		} else {
			throw new IllegalArgumentException("Invalid Operation `"+name+"`");
		}

		BenchLog rtLog = new BenchLog(name+"-"+benchmark+"/rt", "client-"+machine+"-"+threadnum);
		return new ExecutionTask(op, rtLog);
		
	}
	
}
