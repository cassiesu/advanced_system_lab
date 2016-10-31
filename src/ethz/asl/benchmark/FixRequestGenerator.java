package ethz.asl.benchmark;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;



public class FixRequestGenerator implements Runnable {

	protected long warmup;
	protected long cooldown;
	protected long duration;
	protected boolean isRunning;
	
	protected int rate;
	protected AtomicInteger request_num;
	protected ExecutionTask exeTask;
	private Date start;

	public FixRequestGenerator(ExecutionTask exeTask, long warmup, long duration, long cooldown, int rate) {
		this.exeTask = exeTask;
		this.rate = rate;	
		this.warmup = warmup;
		this.cooldown = cooldown;
		this.duration = duration;
		this.isRunning = false;
		
		this.request_num = new AtomicInteger(0);
	}
	
	public void incRequestNum() {
		this.request_num.incrementAndGet();
	}

	public Date incRequestTime() {
		return new Date(this.start.getTime() + (this.request_num.get() * 1000 / this.rate));
	}
	
	@Override
	public void run() {
		final Timer stateTimer = new Timer();
		final Timer reqTimer = new Timer();
		final FixRequestGenerator inst = this;
		start = new Date(System.currentTimeMillis());

		final TimerTask logTask = new TimerTask() {
		      @Override
		      public void run() {
		    	  inst.exeTask.write();
		      }
		};
		
		stateTimer.schedule(new TimerTask() {
		      @Override
		      public void run() {
		    	  inst.isRunning = true;
		          this.cancel();
		      }
		  }, this.warmup);
		
		stateTimer.schedule(new TimerTask() {
		      @Override
		      public void run() {
		    	  inst.isRunning = false;
		    	  inst.exeTask.close();
		    	  logTask.cancel();
		          this.cancel();
		      }
		  }, this.warmup + this.duration);
		
		stateTimer.schedule(new TimerTask() {
		      @Override
		      public void run() {
		          this.cancel();
		          reqTimer.cancel();
		          stateTimer.cancel();
		      }
		  }, this.warmup + this.duration+this.cooldown);
		
		
		stateTimer.scheduleAtFixedRate(logTask, this.warmup + 1000, 1000);
		
		reqTimer.schedule(new ReqTimerTask(this, reqTimer, stateTimer, Thread.currentThread()), this.incRequestTime());
		
		try {
			Thread.sleep(this.warmup+this.cooldown+this.duration);
		} catch (InterruptedException e) {}
		
	}
	
}
