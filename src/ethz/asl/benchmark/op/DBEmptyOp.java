package ethz.asl.benchmark.op;


public class DBEmptyOp implements BenchmarkOp {

	@Override
	public void execute() {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
	}

}
