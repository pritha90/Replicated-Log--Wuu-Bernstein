
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class CleanAllTables{
	public static Mongo mongo = null;
	public static DB db = null;
	public static void main(String[] args) throws Exception {

		mongo = new Mongo( "localhost" , 27017 );
		db = mongo.getDB("test");
		DBCollection myCollection = db.getCollection("DSLog");
		myCollection.drop();
		 myCollection = db.getCollection("DSTimeTable");
		myCollection.drop();
		 myCollection = db.getCollection("Counters");
		myCollection.drop();
	}
}