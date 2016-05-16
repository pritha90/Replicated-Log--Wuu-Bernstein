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

import model.DBHandle;
import utility.Constants;
import utility.HelperFunctions;


public class SyncServerHandlerServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ip = request.getParameter(Constants.PROCESS_ID);
		//TODO: the ip of the initiator must be determined from the header/connection instead
		//of the query parameter

		//Creating a column for the requestor in the local TT table
		DBHandle.initializeTTForIP(ip);
		
		//Why is the ip check later and not before the call to initialize?
		if(ip != null){
			PrintWriter out = response.getWriter();
			out.print(HelperFunctions.contructDbSyncRecord(ip));
		}
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
    }
}
