package ethz.asl.benchmark.mw;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

import ethz.asl.benchmark.ExecutionTask;
import ethz.asl.benchmark.FixRequestGenerator;
import ethz.asl.benchmark.op.BenchmarkOp;
import ethz.asl.benchmark.op.MWDeleteOp;
import ethz.asl.benchmark.op.MWPeekOp;
import ethz.asl.benchmark.op.MWQueryOp;
import ethz.asl.benchmark.op.MWSendOp;
import ethz.asl.log.BenchLog;
import ethz.asl.util.Constants;

public class StabilityBench {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		int benchmark = Integer.parseInt(args[0]);
		int machine = Integer.parseInt(args[1]);
		int parallel_threads = Integer.parseInt(args[2]);
		String host = args[3];
		int port = Integer.parseInt(args[4]);

		System.out.println("Running Benchmark "+benchmark+" On Machine "+machine+" With "+parallel_threads+" Parallel Threads");
		System.out.println("Server Host "+host+" Server Port "+port);
		LogManager.getLogManager().readConfiguration(new FileInputStream("logger.properties"));

		for (int i=0; i < parallel_threads; i++) {
			ExecutionTask task = getExecutionTask(host, port, "mwdelete", benchmark, machine, i);
			Thread tre = new Thread(new FixRequestGenerator(task, Constants.WARMUP, Constants.DURATION, Constants.COOLDOWN, 100));
			tre.start();
		}
		
		for (int i=0; i < parallel_threads; i++) {
			ExecutionTask task = getExecutionTask(host, port, "mwpeek", benchmark, machine, i);
			Thread tre = new Thread(new FixRequestGenerator(task, Constants.WARMUP, Constants.DURATION, Constants.COOLDOWN, 100));
			tre.start();
		}
		
		for (int i=0; i < parallel_threads; i++) {
			ExecutionTask task = getExecutionTask(host, port, "mwsend", benchmark, machine, i);
			Thread tre = new Thread(new FixRequestGenerator(task, Constants.WARMUP, Constants.DURATION, Constants.COOLDOWN, 100));
			tre.start();
		}
		
		for (int i=0; i < parallel_threads; i++) {
			ExecutionTask task = getExecutionTask(host, port, "mwquery", benchmark, machine, i);
			Thread tre = new Thread(new FixRequestGenerator(task, Constants.WARMUP, Constants.DURATION, Constants.COOLDOWN, 100));
			tre.start();
		}
	}
	
	private static ExecutionTask getExecutionTask(String host, int port, String name, int benchmark, int machine, int threadnum) {
		BenchmarkOp op;
		
		if (name.equals("mwsend")) {
			op = new MWSendOp(host, port);
		} else if (name.equals("mwpeek")) {
			op = new MWPeekOp(host, port);
		} else if (name.equals("mwdelete")) {
			op = new MWDeleteOp(host, port);
		} else if (name.equals("mwquery")) {
			op = new MWQueryOp(host, port);
		} else {
			throw new IllegalArgumentException("Invalid Operation `"+name+"`");
		}
		BenchLog rtLog = new BenchLog("mwload-stability-"+benchmark+"/"+name+"-"+"rt", "client-"+machine+"-"+threadnum);

		return new ExecutionTask(op, rtLog);
	}
	
}
