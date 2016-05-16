//Class for handling log processing

package model;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import com.mongodb.BasicDBObject;
import utility.Constants;




public class LogProcess {

	
	//Gets an array of JSON objects of the form <time,ip,content,username>
	//and pushes to the dB if entries are not in already
	public static void putDb(JSONArray log_entries)
	{
		//extract time,ip,content,username
		for(int i=0;i<log_entries.length();i++)
		{
			JSONObject o;
			try {
				o = (JSONObject) log_entries.get(i);
				int t = new Integer( (Integer) o.get(Constants.LOG_TIME));
			    String ip = (String) o.get(Constants.PROCESS_ID);
			  //push to the database
			    if(! DBHandle.checkExistsInLog(t,ip))
			    	DBHandle.pushToLog((JSONObject)log_entries.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	//Checks if ip1 knows about events on ip2 until time e.time
	//ip_s is the initiator of the sync
	public static boolean hasReceived(String ip_s, String ip_d, int time)
	{
		//Query the timetable database
		int t = DBHandle.getTimeFromTT(ip_d,ip_s);
		if (t >= time)
			return true;
		return false;
	}
	
	//Gets the log entries from the database
	public static JSONArray getDbOnSync(String ip)
	{
		//look the TT for ip as the first entry
		JSONArray ttInfo = DBHandle.getTimeTableFromTTForIP(ip);		
		List<LogQuery> queries = new ArrayList<LogQuery>();
		
		
		for(int i = 0; i < ttInfo.length(); i++)
		{
			BasicDBObject Entryi;
			try {
				Entryi = (BasicDBObject)ttInfo.get(i);
				String p_id = (String) Entryi.get(Constants.PROCESS_ID_D);
				int t;
				try{
				t = ((Double) Entryi.get(Constants.LOG_TIME)).intValue();
				}catch(Exception e){
					t = (Integer) Entryi.get(Constants.LOG_TIME);
				}
				LogQuery o = new LogQuery(p_id,t);	
				queries.add(o);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return DBHandle.getRelevantLog(queries);
	}
	
	public static void updateTT(JSONArray ttentries, String self_id, String incoming_id)
	{
		System.out.println("IPs in updateTT self:" + self_id + " incoming: "+ incoming_id);
		//for each in tuples
		for(int i=0;i<ttentries.length();i++)
		{
			JSONObject o;
			try {
				o = (JSONObject) ttentries.get(i);
				int t = new Integer( (Integer) o.get(Constants.LOG_TIME));
			    String ip_s = (String) o.get(Constants.PROCESS_ID_S);
			    String ip_d = (String) o.get(Constants.PROCESS_ID_D);
			    int t1 = DBHandle.getTimeFromTT(ip_d,ip_s);
			   	if (t > t1) 
			   	{
			   		t1 = t;
			   		DBHandle.pushToTT(ip_d,ip_s,t1);
			   		
				   	//update ip_d,ip_s,t1

			   	}
			   	
			   	
			   	if(ip_s.equals(incoming_id))
			   	{
			   		System.out.println("Adding cross entry self:" + self_id + " incoming: "+ incoming_id);
			   		int t2 = DBHandle.getTimeFromTT(ip_d,self_id);
			   		if (t > t2)
			   		{
			   			t2 = t;
				   		DBHandle.pushToTT(ip_d,self_id,t2);
				   		
			   			//update ip_d,self_ip,t2
			   		}
			   	}
			}
			 catch (JSONException e) {
				e.printStackTrace();
			}
		    
		}   
		   	
	}
	
	
}
