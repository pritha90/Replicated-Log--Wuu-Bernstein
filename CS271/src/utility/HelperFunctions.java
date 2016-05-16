package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import server.TrialServer;
import model.DBHandle;
import model.LogProcess;


public class HelperFunctions{
	public static org.json.JSONObject contructLogRecord(String content, String userName, int lamportTime){
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(Constants.CONTENT, content);
			jsonObj.put(Constants.PROCESS_ID, TrialServer.MY_IP);
			jsonObj.put(Constants.USER_NAME, userName);
			jsonObj.put(Constants.LOG_TIME, lamportTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
		return jsonObj;
	}
	
	public static JSONObject contructDbSyncRecord(String processId){
		JSONObject jsonObj = new JSONObject();
		
		try {
			jsonObj.put("log", LogProcess.getDbOnSync(processId));
			jsonObj.put("TT", DBHandle.getTT());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return jsonObj;
	}
	
	public static String readConfigFile(String filename) throws IOException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    return everything.trim();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

    }
}