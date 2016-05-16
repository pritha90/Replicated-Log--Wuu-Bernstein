package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;
import org.json.JSONObject;
import org.json.JSONArray;

import com.mongodb.util.JSON;

import model.DBHandle;
import model.LogProcess;
import utility.Constants;
import utility.HelperFunctions;


public class SyncHandlerServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ip = request.getParameter(Constants.PROCESS_ID);
		//TODO: the ip of the initiator must be determined from the header/connection instead
		//of the query parameter
		String url = "http://" + ip + ":8081/sync?"+Constants.PROCESS_ID+"="+TrialServer.MY_IP;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer responseStr = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			responseStr.append(inputLine);
		}
		in.close();
		
		JSONObject json = null;
		try {
			json = new JSONObject(responseStr.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Sent data");
		System.out.println(json);
		System.out.println("Before update");
		System.out.println(DBHandle.getTT());
		try {
			LogProcess.putDb((JSONArray)json.get("log"));
			LogProcess.updateTT((JSONArray)json.get("TT"), TrialServer.MY_IP, ip);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("After update");
		System.out.println(DBHandle.getTT());
		//Creating a column for the requestor in the local TT table
		/*DBHandle.initializeTTForIP(ip);
		
		//Why is the ip check later and not before the call to initialize?
		if(ip != null){
			PrintWriter out = response.getWriter();
			out.print(HelperFunctions.contructDbSyncRecord(ip));
		}*/
		//response.setContentType("application/json");
		String htmlContent = "<html><body style=\"background-color:rgb(51, 153, 255);color:white\">Thank you for SyncInc!</body></html>";
		response.getWriter().write(htmlContent);
		response.setStatus(HttpServletResponse.SC_OK);
    }
}
