package ethz.asl.benchmark;

import ethz.asl.benchmark.op.BenchmarkOp;
import ethz.asl.log.BenchLog;
import ethz.asl.util.Constants;


public class ExecutionTask {
	public ExecutionTask(BenchmarkOp op, BenchLog rtLog) {
		this.op = op;
		this.rtLog = rtLog;
	}
	
	private int div = 1000;
	private BenchmarkOp op;
	private BenchLog rtLog;

	public void write() {
		this.rtLog.appendSeperator("--------");
		this.rtLog.flush();
	}
	
	public void close() {
		this.rtLog.close();
	}

	public void execute(boolean isRunning) {
		long start = System.nanoTime();
		this.op.execute();
		if (isRunning) {
			long end = System.nanoTime();
			this.rtLog.appendRecord(((end-start)/div)+"");
			try {
				Thread.sleep(Constants.THINKING);
			} catch (InterruptedException e) {;
			}
		}
	}

}
