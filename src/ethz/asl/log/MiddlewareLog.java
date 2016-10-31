package ethz.asl.log;


public class MiddlewareLog {

	BenchLog requestQLog;
	BenchLog requestDLog;
	BenchLog databaseQLog;
	BenchLog databaseDLog;
	BenchLog responseQLog;
	BenchLog responseDLog;
	BenchLog totalTimeLog;

	public void recordTime(long requestQ, long requestD, long backendQ, long backendD, long responseQ, long responseD, long totalTime, String op) {
		requestQLog.appendRecord(requestQ+"");
		requestDLog.appendRecord(requestD+"");
		databaseQLog.appendRecord(backendQ+"");
		databaseDLog.appendRecord(op+"\t"+backendD);
		responseQLog.appendRecord(responseQ+"");
		responseDLog.appendRecord(responseD+"");
		totalTimeLog.appendRecord(op+"\t"+totalTime);
	}
	
	public MiddlewareLog(String dir, int machine) {
		requestQLog = new BenchLog(dir+"/requestQ", "machine-"+machine+"-"+Thread.currentThread().getId());
		requestDLog = new BenchLog(dir+"/requestD", "machine-"+machine+"-"+Thread.currentThread().getId());
		databaseQLog = new BenchLog(dir+"/databaseQ", "machine-"+machine+"-"+Thread.currentThread().getId());
		databaseDLog = new BenchLog(dir+"/databaseD", "machine-"+machine+"-"+Thread.currentThread().getId());
		responseQLog = new BenchLog(dir+"/responseQ", "machine-"+machine+"-"+Thread.currentThread().getId());
		responseDLog = new BenchLog(dir+"/responseD","machine-"+machine+"-"+Thread.currentThread().getId());
		totalTimeLog = new BenchLog(dir+"/totalTime", "machine-"+machine+"-"+Thread.currentThread().getId());
	}

}
