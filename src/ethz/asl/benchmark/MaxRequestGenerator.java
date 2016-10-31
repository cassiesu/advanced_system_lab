package ethz.asl.benchmark;

import java.util.Timer;
import java.util.TimerTask;



public class MaxRequestGenerator implements Runnable {

	public MaxRequestGenerator(ExecutionTask exeTask, long warmup, long duration, long cooldown) {
		this.exeTask = exeTask;
		this.warmup = warmup;
		this.cooldown = cooldown;
		this.duration = duration;
		
		this.isRunning = false;
		this.isWarmup = true;
		this.isCooldown = false;
	}

	private long warmup;
	private long cooldown;
	private long duration;
	
	private boolean isRunning;
	private boolean isWarmup;
	private boolean isCooldown;
	
	private ExecutionTask exeTask;
	
	@Override
	public void run() {
		this.isWarmup = true;
		Timer timer = new Timer();
		final MaxRequestGenerator inst = this;
		
		TimerTask logTask = new TimerTask() {
		      @Override
		      public void run() {
		    	  inst.exeTask.write();
		      }
		};
		
		timer.schedule(new TimerTask() {
		      @Override
		      public void run() {
		    	  inst.isWarmup = false;
		    	  inst.isRunning = true;
		          this.cancel();
		      }
		}, this.warmup);
		
		timer.schedule(new TimerTask() {
		      @Override
		      public void run() {
		    	  inst.isRunning = false;
		    	  inst.isCooldown = true;
		          this.cancel();
		      }
		}, this.warmup+this.duration);
		
		timer.schedule(new TimerTask() {
		      @Override
		      public void run() {
		    	  inst.isCooldown = false;
		    	  inst.exeTask.close();
		    	  this.cancel();
		      }
		  }, this.warmup+this.duration+this.cooldown);
		
		timer.scheduleAtFixedRate(logTask, this.warmup+1000, 1000);
		
		while (this.isWarmup) {
			this.exeTask.execute(false);
		}
	
		while (this.isRunning) {
			this.exeTask.execute(true);
		}
		logTask.cancel();
		
		while (this.isCooldown) {
			this.exeTask.execute(false);
		}
		timer.cancel();
      
	}

}
