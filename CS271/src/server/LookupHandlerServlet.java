package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DBHandle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import utility.Constants;

import java.io.IOException;
import java.io.PrintWriter;


public class LookupHandlerServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBObject obj = new BasicDBObject();
		JSONArray jsonArray = DBHandle.getFromLog();
		String htmlContent = "<!DOCTYPE html><html><body style=\"background-color:rgb(51, 153, 255);\">";
		for(int i = 0; i < jsonArray.length(); i++){
			try {
				obj = (DBObject) jsonArray.get(i);
				htmlContent += "<div style=\"color:white; padding:20px;\"><font face=\"Comic sans MS\" size=\"5\">"
				+obj.get(Constants.CONTENT)+"<br><font size=\"2\">"
				+"posted by <i>"+obj.get(Constants.USER_NAME)+"</i> on "
				+ "<i>"+obj.get(Constants.PROCESS_ID)+"</i></div>";
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		htmlContent += "</body></html>";
		response.getWriter().write(htmlContent);
		//response.setContentType("application/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}