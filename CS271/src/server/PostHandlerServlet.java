package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DBHandle;
import utility.Constants;
import utility.HelperFunctions;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Posts the message to Twitter
public class PostHandlerServlet extends HttpServlet
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String content = request.getParameter(Constants.CONTENT);
		String userName = request.getParameter(Constants.USER_NAME);
		int lamportTime = DBHandle.incLamportClock(Constants.INC_LAMPORT_TIME);
		boolean status = DBHandle.pushToLog(HelperFunctions.contructLogRecord(content, userName, lamportTime));
		
		if(status){
			DBHandle.updateMyTimeTableValue(lamportTime);
		}
		response.setStatus(HttpServletResponse.SC_OK);
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String htmlContent = "<html><body style=\"background-color:rgb(51, 153, 255);\"><form action='http://"+TrialServer.MY_IP+":8081/syncinc/post' method='post'>"
				+ "<div>"
				+ "<label style = color:white; for='content'>Message:</label><textarea rows=2; cols=70; width=10; id='content' name='content'>"
				+ "</textarea></div><br>"
				+ "<div>"
				+ "<label style = color:white;padding:10px; for='name'>Name:</label><input size=72; type='text' id='name' name='userName'>"
				+ "</div><br>"
				+ "<div class='button'><button color=white; type='submit'>Submit</button></div></form></body></html>";
		response.getWriter().write(htmlContent);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}