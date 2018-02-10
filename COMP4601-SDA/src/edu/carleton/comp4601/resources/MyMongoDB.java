package edu.carleton.comp4601.resources;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MyMongoDB {
	
	//Singleton code
	private static MyMongoDB instance;
	public static void setInstance(MyMongoDB instance){
		MyMongoDB.instance = instance;
	}
	public static MyMongoDB getInstance(){
		if (instance == null)
			instance = new MyMongoDB();
		return instance;
	}
	
	//Mongo Variables
	private MongoClient mongoClient;
   	private MongoDatabase database;
   	MongoCollection<Document> coll; //note: this is not the Document provided by prof
	
	//Constructor
	public MyMongoDB(){
		//Mongo Set-up
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("SDAdb");
		coll = database.getCollection("testColl"); 
		
		//Test DB code
		Document doc = new Document("name", "Andrew Erlichson");
                 
        coll.insertOne(doc); // first insert 
		
		/* Syntax (post depricated DB)
		Document doc = new Document("name", "Andrew Erlichson")// 
                .append("company", "10gen"); 
 
        coll.insertOne(doc); // first insert 
        doc.remove("_id"); // remove the _id key 
         
        more at: https://www.programcreek.com/java-api-examples/index.php?class=com.mongodb.MongoClient&method=getDatabase
        */
	}

}
