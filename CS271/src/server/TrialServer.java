package server;
import model.DBHandle;
import model.LogProcess;

import org.eclipse.jetty.server.*;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import utility.HelperFunctions;

import com.mongodb.DB;
import com.mongodb.Mongo;


public class TrialServer {
	public static int counter = 0;
	public static Mongo mongo = null;
	public static DB db = null;
	public static String MY_IP = null;
	public static void main(String[] args) throws Exception {

		mongo = new Mongo( "localhost" , 27017 );
		db = TrialServer.mongo.getDB("test");
		MY_IP = HelperFunctions.readConfigFile("config.txt");

		DBHandle.createCounters();
		DBHandle.createTimeTable();
		
		Server server = new Server();
		// register the connector
		registerHttpConnector(server);
		//Create servlets to handle GET/POST
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		
		
		context.addServlet(new ServletHolder(new PostHandlerServlet()),"/syncinc/post");
		context.addServlet(new ServletHolder(new LookupHandlerServlet()),"/syncinc/lookup");
		context.addServlet(new ServletHolder(new SyncHandlerServlet()),"/syncinc/sync");
		context.addServlet(new ServletHolder(new SyncServerHandlerServlet()),"/sync");

		System.out.println("Server Starting. Listening for user requests.");
		server.start();
		server.join();

	}

	private static void registerHttpConnector(Server server){
		ServerConnector httpConnector = new ServerConnector(server);
		httpConnector.setPort(8081);
		server.addConnector(httpConnector);
	}

	

}

