package edu.carleton.comp4601.graphstuff;

import java.io.IOException;
import java.net.*;

import javax.xml.bind.annotation.XmlRootElement;

//import org.jgrapht.*;
//import org.jgrapht.graph.*;
//import org.jgrapht.defaultedge;

import org.bson.Document;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import com.mongodb.MongoClient; 
import com.mongodb.client.MongoCollection; 
import com.mongodb.client.MongoDatabase; 

import edu.carleton.comp4601.graphstuff.MyVertex;
import edu.carleton.comp4601.resources.MyMongoDB;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class MyGraph
{
	private static DirectedMultigraph<String, DefaultEdge> graph;
    
    //DB Code
   
	private MyMongoDB db;

	//Singleton Code
	private static MyGraph instance = null;
    public static MyGraph getInstance() {
    	 if(instance == null) {
	         instance = new MyGraph();
	      }
	      return instance;
    }
    public static void setInstance(MyGraph instance) {
		MyGraph.instance = instance;
	}
	
	public MyGraph(){
    	graph = createGraph();
    	
    	db = MyMongoDB.getInstance();
    	
    			
    } 

	
    private DirectedMultigraph<String, DefaultEdge> createGraph(){

    	DirectedMultigraph<String, DefaultEdge> g = new DirectedMultigraph<String, DefaultEdge>(DefaultEdge.class);
        return g;
    }

    public void addVertex(MyVertex childVertex, MyVertex parentVertex) throws IOException{
    	//is it an original vertex
    	if(!graph.containsVertex(childVertex.getURL())){
    		
    		//is this the root
	    	if(parentVertex == null){
	    		System.out.println("THIS IS THE ROOT LOCATION!");
	    		graph.addVertex(childVertex.getURL()); 
	       	}
	    	
	    	//its not the root
	    	else{
	    		//does the childs parent exist (it should)
	    		System.out.println("PARENT URL: " + parentVertex.getURL());
	    		graph.addVertex(childVertex.getURL());
	    		db.storeEdge(graph.addEdge(parentVertex.getURL(), childVertex.getURL()));
	    		
	    	}
    	}
    	System.out.println("MY GRAPH: " + graph.edgeSet().toString());
    	
    	
    }
    
    
    
}