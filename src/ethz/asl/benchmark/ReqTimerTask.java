package ethz.asl.benchmark;

import java.util.Timer;
import java.util.TimerTask;

public class ReqTimerTask extends TimerTask {

	FixRequestGenerator generator;
	Timer reqTimer;
	Timer stateTimer;
	Thread reqThread;

	@Override
    public void run() {
    	try {
    		execute();
        	try {
        		this.reqTimer.schedule(new ReqTimerTask(this.generator, this.reqTimer, this.stateTimer, this.reqThread), this.generator.incRequestTime());
        	} catch(IllegalStateException e) {}
    	} catch(RuntimeException e) {
    		System.out.println("Fixed Rate Fails!");
    	}

    }
	
	public ReqTimerTask(FixRequestGenerator generator, Timer requestTimer, Timer stateChangeTimer, Thread thread) {
		this.generator = generator;
		this.reqTimer = requestTimer;
		this.stateTimer = stateChangeTimer;
		this.reqThread = thread;
	}
	
	private void execute() {
		this.generator.exeTask.execute(this.generator.isRunning);;
		this.generator.incRequestNum();
	}
	
}
