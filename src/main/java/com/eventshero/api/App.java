package com.eventshero.api;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mongodb.*;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        try {
        	//Connect to MongoDB server.
			MongoClient mongo = new MongoClient( "localhost" , 27017 );
			
			//Get database. If the database doesn’t exist, MongoDB will create it
			DB db = mongo.getDB("eventshero");
			
			//Display all collections from selected database.
			Set<String> tables = db.getCollectionNames();
			for(String coll : tables){
				System.out.println(coll);
			}
			
			//Save a document (data) into a collection (table) named “user”.
			DBCollection table = db.getCollection("user");
			BasicDBObject document = new BasicDBObject();
			document.put("name", "mkyong");
			document.put("age", 30);
			document.put("createdDate", new Date());
			table.insert(document);
			
			//Find document where “name=mkyong”, and display it with DBCursor
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("name", "mkyong");
		 
			DBCursor cursor = table.find(searchQuery);
		 
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
			
			//Find document where “name=mkyong”, and delete it.
			table.remove(searchQuery);
			
			//Display all databases.
			List<String> databases = mongo.getDatabaseNames();
			for(String database : databases){
				System.out.println(database);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}
