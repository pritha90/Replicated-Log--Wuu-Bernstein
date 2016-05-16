package utility;
public class Constants{
	public static final String LOG_TIME = "lamportClock";
	public static final String PROCESS_ID = "ip";
	public static final String CONTENT = "content";
	public static final String USER_NAME = "userName";
	public static final String APPEND_LOG_TIME = "appendLogTime";
	public static final String INC_LAMPORT_TIME = "incLamportTime";
	public static final String LOG_NAME = "DSLog";
	
	//public static final String MY_IP = "192.1.1.1";

	//For the TT table
	public static final String PROCESS_ID_S = "ip_s";
	public static final String PROCESS_ID_D = "ip_d";
	public static final String TT_NAME = "DSTimeTable";
	//TT Schema <id_d,id_s,log_time>
	//What id_s knows about id_d (convention is reverse of the slides)
	public static final String COUNTERS_TABLE_NAME = "Counters";
}