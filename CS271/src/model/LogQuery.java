package model;

public class LogQuery{
	String processId;
	int lamportClock;
	
	LogQuery(){}
	
	LogQuery(String processId, int lamportClock){
		this.processId = processId;
		this.lamportClock = lamportClock;
	}
	
	public String getProcessId(){
		return this.processId;
	}
	
	public int getLamportClock(){
		return this.lamportClock;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}
	
	public void setLamportClock(int lamportClock){
		this.lamportClock = lamportClock;
	}
}