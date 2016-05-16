package model;

public class TimeTableRecord{
	String ip_d, ip_s, lamportClock;
	TimeTableRecord(String ip_d, String ip_s, String lamportClock ){
		this.ip_d = ip_d;
		this.ip_s = ip_s;
		this.lamportClock = lamportClock;
	}
}