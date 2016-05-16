package model;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import server.TrialServer;
import utility.Constants;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.util.JSON;

public class DBHandle {
	
	public static JSONArray getRelevantLog(List<LogQuery> objList){
		List<DBObject> queries =  new ArrayList<DBObject>();
		for(LogQuery obj : objList){
			DBObject dbObj = new QueryBuilder().start().and(
        			QueryBuilder.start().put(Constants.PROCESS_ID).is(obj.getProcessId()).get(),
        			new QueryBuilder().start().put(Constants.LOG_TIME).greaterThan(obj.getLamportClock())
        			.get()).get();
        	queries.add(dbObj);
		}
		DBObject dbObj = null;
		if(!queries.isEmpty()){
			dbObj = queries.get(0);
			for(int i = 1; i < queries.size(); i++){	
				dbObj = new QueryBuilder().start().or(dbObj, queries.get(i)).get();
			}
		}
		System.out.println(dbObj);
		DBCollection collection = TrialServer.db.getCollection(Constants.LOG_NAME);
		DBCursor cursorDoc = collection.find(dbObj).sort(new BasicDBObject("id",1));
	 	JSONArray jsonArray = new JSONArray();
	 	while (cursorDoc.hasNext()) {
	 		jsonArray.put(cursorDoc.next());
		}
		return jsonArray;
	}
	
	public static boolean pushToLog(JSONObject jsonObj){
		try{
			DBCollection collection = TrialServer.db.getCollection(Constants.LOG_NAME);
			jsonObj.put("id", getNextLogSequence(Constants.APPEND_LOG_TIME));
			
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());
			collection.insert(dbObject);
			System.out.println("Successfully added!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	

	public static void createCounters(){
		try{
			DBCollection collection = TrialServer.db.getCollection(Constants.COUNTERS_TABLE_NAME);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("_id", Constants.APPEND_LOG_TIME);
			jsonObj.put("seq", 1);
			
			jsonObj.put("lamport", Constants.INC_LAMPORT_TIME);
			jsonObj.put("lamportSeq", 1);
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());

			collection.insert(dbObject);
			System.out.println("Successfully added!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void createTimeTable(){
		try{
			DBCollection collection = TrialServer.db.getCollection(Constants.TT_NAME);
			JSONObject jsonObj = new JSONObject();
			try{
			jsonObj.put(Constants.PROCESS_ID_D, TrialServer.MY_IP);
			jsonObj.put(Constants.PROCESS_ID_S, TrialServer.MY_IP);
			jsonObj.put(Constants.LOG_TIME, 0);
			
			
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());

			collection.insert(dbObject);
			System.out.println("Successfully added!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void updateMyTimeTableValue(int lamportTime){
		try{
			DBCollection collection = TrialServer.db.getCollection(Constants.TT_NAME);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(Constants.PROCESS_ID_D, TrialServer.MY_IP);
			jsonObj.put(Constants.PROCESS_ID_S, TrialServer.MY_IP);
			jsonObj.put(Constants.LOG_TIME, lamportTime);
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());

			BasicDBObject searchQuery = new BasicDBObject().append(Constants.PROCESS_ID_D, TrialServer.MY_IP)
					.append(Constants.PROCESS_ID_S, TrialServer.MY_IP);
			collection.update(searchQuery, dbObject);
			System.out.println("Successfully updated!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static int  getNextLogSequence(String name) {
		DBCollection collection = TrialServer.db.getCollection(Constants.COUNTERS_TABLE_NAME);
		BasicDBObject find = new BasicDBObject();
	    find.put("_id", name);
	    BasicDBObject update = new BasicDBObject();
	    update.put("$inc", new BasicDBObject("seq", 1));
	    DBObject obj =  collection.findAndModify(find, update);
	    return (Integer) obj.get("seq");
	}
	
	public static int  incLamportClock(String name) {
		DBCollection collection = TrialServer.db.getCollection(Constants.COUNTERS_TABLE_NAME);
		BasicDBObject find = new BasicDBObject();
	    find.put("lamport", name);
	    BasicDBObject update = new BasicDBObject();
	    update.put("$inc", new BasicDBObject("lamportSeq", 1));
	    DBObject obj =  collection.findAndModify(find, update);
	    return (Integer) obj.get("lamportSeq");
	}
	
	public static boolean checkExistsInLog(int lamportTime, String processId){
		DBCollection collection = TrialServer.db.getCollection(Constants.LOG_NAME);
		BasicDBObject eqQuery = new BasicDBObject();
		eqQuery.put(Constants.LOG_TIME, new BasicDBObject("$eq", lamportTime));
		eqQuery.put(Constants.PROCESS_ID, new BasicDBObject("$eq", processId));
		DBCursor cursor = collection.find(eqQuery);
		if(cursor.hasNext()) 
			return true;
		return false;
	}
	
	//for lookup - sends complete log
	public static JSONArray getFromLog(){
		JSONArray jsonArray = new JSONArray();
		DBCollection collection = TrialServer.db.getCollection(Constants.LOG_NAME);
		DBCursor cursorDoc = collection.find();
		cursorDoc.sort(new BasicDBObject("id",1));
		while (cursorDoc.hasNext()) {
			jsonArray.put(cursorDoc.next());
		}
		return jsonArray;
	}
	
	 public static JSONArray getTimeTableFromTTForIP(String ip_s)
	 {
	 	JSONArray jsonArray = new JSONArray();
	 	DBCollection collection = TrialServer.db.getCollection(Constants.TT_NAME);
	 	
	 	//Select * from TT where PROCESS_ID_S = ip_s
	 	BasicDBObject sQuery = new BasicDBObject();
		sQuery.put(Constants.PROCESS_ID_S, new BasicDBObject("$eq", ip_s));
	 	DBCursor cursorDoc = collection.find(sQuery);
	 	if(cursorDoc.count() == 0) 
	 		return jsonArray;
	 	while (cursorDoc.hasNext()) {
			jsonArray.put(cursorDoc.next());
		}
		return jsonArray;	
	 }
	 
	 public static JSONArray getTT()
	 {
		 	JSONArray jsonArray = new JSONArray();
		 	DBCollection collection = TrialServer.db.getCollection(Constants.TT_NAME);
		 	
		 	//Select * from TT
		 	DBCursor cursorDoc = collection.find();
		 	
		 	while (cursorDoc.hasNext()) {
				jsonArray.put(cursorDoc.next());
			}
			
			return jsonArray;
	 }
	 
	 public static int getTimeFromTT(String ip_d, String ip_s)
	 {
	 	
		 DBCollection collection = TrialServer.db.getCollection(Constants.TT_NAME);
	 	
	 	//Select * from TT where PROCESS_ID_S = ip_s and PROCESS_ID_D = ip_d
	 	BasicDBObject sQuery = new BasicDBObject();
		sQuery.put(Constants.PROCESS_ID_S, new BasicDBObject("$eq", ip_s));
		sQuery.put(Constants.PROCESS_ID_D, new BasicDBObject("$eq", ip_d));
	 	DBCursor dbCursor = collection.find(sQuery);
 		DBObject dbObj = null;	
 		System.out.println("dbObj");
 		System.out.println(dbCursor);
	 	if(dbCursor.count() != 0){
	 		try{
	 		dbObj = dbCursor.next();	
	 		Double timestamp = new Double((Double) dbObj.get(Constants.LOG_TIME));
	 		return timestamp.intValue();
	 		}catch(Exception e){
	 			int timestamp = (Integer) dbObj.get(Constants.LOG_TIME);
		 		return timestamp;
	 		}
	 	}
	 	else{
	 		System.out.println("else "+ip_d+" "+ip_s);
	 		JSONObject jsonObj = new JSONObject();
	 		try{
		 		jsonObj.put(Constants.PROCESS_ID_D, ip_d);
				jsonObj.put(Constants.PROCESS_ID_S, ip_s);
				jsonObj.put(Constants.LOG_TIME,0);
	 		}catch(Exception e){
	 			e.printStackTrace();
	 		}
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());
			collection.insert(dbObject);
			System.out.println("Successfully added!");
			return 0;
	 	}
	 }
	 
	 public static JSONArray getIPAndTimeFromLog(String ip_d, int t)
	 {
	 	JSONArray jsonArray = new JSONArray();
	 	DBCollection collection = TrialServer.db.getCollection(Constants.LOG_NAME);
	 	
	 	//Select * from Log where PROCESS_ID = ip_d and LOG_TIME > t
	 	BasicDBObject sQuery = new BasicDBObject();
		sQuery.put(Constants.PROCESS_ID, new BasicDBObject("$eq", ip_d));
		sQuery.put(Constants.LOG_TIME, new BasicDBObject("$gt", t));
	 	
	 	DBCursor cursorDoc = collection.find(sQuery);
	 	
	 	while (cursorDoc.hasNext()) {
			jsonArray.put(cursorDoc.next());
		}
		return jsonArray;	
	 }
	//update the record ip_d,ip_s,t 
	public static boolean pushToTT(String ip_d,String ip_s,int t){
			try{
				DBCollection collection = TrialServer.db.getCollection(Constants.TT_NAME);
				//jsonObj.put("id", getNextLogSequence(Constants.APPEND_LOG_TIME));
				
				// convert JSON to DBObject directly
				DBObject dbObject = new BasicDBObject();
				dbObject.put(Constants.PROCESS_ID_D, ip_d);
				dbObject.put(Constants.PROCESS_ID_S, ip_s);
				dbObject.put(Constants.LOG_TIME, t);
				
				BasicDBObject searchQuery = new BasicDBObject().append(Constants.PROCESS_ID_D, ip_d)
						.append(Constants.PROCESS_ID_S, ip_s);
				System.out.println(searchQuery);
				collection.update(searchQuery, dbObject);
				
				System.out.println("Successfully added!");
				return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
	 public static void initializeTTForIP(String ip){
		 	DBCollection collection = TrialServer.db.getCollection(Constants.TT_NAME);
		 	
		 	List<String> destIP = new ArrayList<String>();
		 	//Select * from Log where PROCESS_ID = ip_d and LOG_TIME > t
		 	BasicDBObject sQuery = new BasicDBObject();
			sQuery.put(Constants.PROCESS_ID_S, new BasicDBObject("$eq", TrialServer.MY_IP));
			// all tuples that "I" know about
		 	DBCursor cursorDoc = collection.find(sQuery);
		 	System.out.println("initializeTTForIP");
		 	System.out.println(cursorDoc.count());
		 	while (cursorDoc.hasNext()) {
		 		destIP.add((String) cursorDoc.next().get(Constants.PROCESS_ID_D));
			}
		 	
		 	System.out.println(destIP);
		 	//for each in tuple: if <each ip_d, B> in TT : insert <each ip_d,B,0>
		 	
		 	for(int i = 0; i < destIP.size(); i++)
		 	{
		 			BasicDBObject sQuery1 = new BasicDBObject();
		 			sQuery1.put(Constants.PROCESS_ID_D, new BasicDBObject("$eq", destIP.get(i)));
		 			sQuery1.put(Constants.PROCESS_ID_S, new BasicDBObject("$eq", ip));
		 			if (collection.find(sQuery1).count() == 0)
		 			{
		 				JSONObject jsonObj = new JSONObject();
		 				try{
		 				jsonObj.put(Constants.PROCESS_ID_D, destIP.get(i));
		 				jsonObj.put(Constants.PROCESS_ID_S, ip);
		 				jsonObj.put(Constants.LOG_TIME,0);
		 				System.out.println("Adding " + jsonObj.toString());
		 				}catch(Exception e){
		 					e.printStackTrace();
		 				}
		 				// convert JSON to DBObject directly
		 				DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());
		 				collection.insert(dbObject);
		 				System.out.println("Successfully added!");
		 			}
		 			
		 	}		
	}
}