package edu.carleton.comp4601.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import edu.carleton.comp4601.resources.MyMongoDB;

import java.net.UnknownHostException;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentCollection {
	@XmlElement(name="documents")
	
	
	//Singelton Stuff
	public static void setInstance(DocumentCollection instance) {
		DocumentCollection.instance = instance;
	}
	public static DocumentCollection getInstance() {
		if (instance == null)
			instance = new DocumentCollection();
		return instance;
	}
	private static DocumentCollection instance;
	
	//Storage Variables
	MyMongoDB db; 
	private List<edu.carleton.comp4601.dao.Document> documents;
	int nextDocID;
	

	//Constructor
	public DocumentCollection(){
		//Database connection
		db = MyMongoDB.getInstance();
		documents = new ArrayList<edu.carleton.comp4601.dao.Document>();
		populateList();
		nextDocID = documents.size()-1;
	}
	
	
	//Getters and Setters
	public List<edu.carleton.comp4601.dao.Document> getDocuments() {
		this.populateList();
		return documents;
	}
	public void setDocuments(List<edu.carleton.comp4601.dao.Document> documents) {
		this.documents = documents;
	}
	
	//FIND by ID
	public edu.carleton.comp4601.dao.Document find(Integer id_in){
		MongoCursor<Document> cursor = db.getCursor();
		edu.carleton.comp4601.dao.Document searchDocument = null;
		
		while (cursor.hasNext()) {
			Document doc = cursor.next(); //DB Document, not prof Document
			
			Integer id_I = (Integer) doc.get("id");
			if(id_in.intValue() == id_I.intValue()){
				searchDocument = new edu.carleton.comp4601.dao.Document(id_I);
				searchDocument.setLinks((ArrayList<String>) doc.get("links"));
				searchDocument.setName((String) doc.get("name"));
				searchDocument.setTags((ArrayList<String>) doc.get("tags"));
				searchDocument.setScore((double) doc.get("score"));
				searchDocument.setText((String) doc.get("text"));
				searchDocument.setUrl((String) doc.get("url"));
			}
	    }
		return searchDocument;
	}
	
	//FIND by URL
	public edu.carleton.comp4601.dao.Document find(String url_in){
		MongoCursor<Document> cursor = db.getCursor();
		edu.carleton.comp4601.dao.Document searchDocument = null;
		
		while (cursor.hasNext()) {
			Document doc = cursor.next(); //DB Document, not prof Document
			
			String url_I = (String) doc.get("url");
			if(url_in.equals(url_I)){
				searchDocument = new edu.carleton.comp4601.dao.Document();
				searchDocument.setScore((int) doc.get("id"));
				searchDocument.setLinks((ArrayList<String>) doc.get("links"));
				searchDocument.setName((String) doc.get("name"));
				searchDocument.setTags((ArrayList<String>) doc.get("tags"));
				searchDocument.setScore((double) doc.get("score"));
				searchDocument.setText((String) doc.get("text"));
				searchDocument.setUrl((String) doc.get("url"));
			}
	    }
		return searchDocument;
	}
	
	//TAG DELETE
	public void deleteByTag(String tag){
		MongoCursor<Document> cursor = db.getCursor();
		while (cursor.hasNext()) {
			Document doc = cursor.next(); //DB Document, not prof Document
			ArrayList<String> tags = (ArrayList<String>) doc.get("tags");
			if(tags.contains(tag)){
				Integer id_I = (Integer) doc.get("id");
				delete(id_I);
			}
	    }
	}
	
	//ADD
	public void add(edu.carleton.comp4601.dao.Document newdoc){
	
		//TEST CODE
		/*
		edu.carleton.comp4601.dao.Document testDoc = new edu.carleton.comp4601.dao.Document();
		testDoc.setId(1);
		ArrayList<String> links = new ArrayList<String>();
		links.add("Link1");
		links.add("Link2");
		links.add("Link3");
		testDoc.setLinks(links);
		testDoc.setName("Test Name");
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("Tag1");
		tags.add("Tag2");
		tags.add("Tag3");
		testDoc.setTags(tags);
		testDoc.setScore(66.66);
		testDoc.setUrl("www.test.com");
		testDoc.setText("Test Text");
		*/
		
		if (find(newdoc.getUrl()) == null){
			db.add(newdoc);
		}
		else{
			System.out.println("Failed to Add, this dudes is already in the Database, based off the Doc Id");
		}
	}
	
	public void delete(Integer id){	
		db.delete(id);
	}
	
	public void update(Integer oldInt, edu.carleton.comp4601.dao.Document updateDoc){
		db.delete(oldInt);
		db.add(updateDoc);
	}
	
	public String printDocuments(){
		String docs = "";
		
		//populate list
		getDocuments();
		
		for(int i=0; i<documents.size(); i++){
			edu.carleton.comp4601.dao.Document docAti = documents.get(i);
			docs = docs + docAti.getName() + "\n";
		}
		return docs;
	}
	
	public void populateList(){
		System.out.println("Debug Flag 1");
		if (!documents.isEmpty()){
			documents.clear(); //this function will repopulate the list so we need to delete whats inside to avoid duplication
		}
		System.out.println("Debug Flag 2");
		MongoCursor<Document> cursor = db.getCursor();
		
		System.out.println("Debug Flag 3");
			while (cursor.hasNext()) {
				System.out.println("Debug Flag 4");
				Document doc = cursor.next(); //not Prof made Document class
				
				System.out.println("Debug Flag 5");
				edu.carleton.comp4601.dao.Document ourDocument = new  edu.carleton.comp4601.dao.Document();
				
				ourDocument.setId((Integer) doc.get("id"));
				ourDocument.setLinks((ArrayList<String>) doc.get("links"));
				ourDocument.setName((String) doc.get("name"));
				ourDocument.setTags((ArrayList<String>) doc.get("tags"));
				ourDocument.setScore((double) doc.get("score"));
				ourDocument.setUrl((String) doc.get("url"));
				ourDocument.setText((String) doc.get("text"));
				ourDocument.setMetadata((ArrayList<String>) doc.get("metadata"));
				ourDocument.setMetaname((ArrayList<String>) doc.get("metaname"));
				
				
				System.out.println("Debug Flag 6");
			    documents.add(ourDocument);
			    
			    System.out.println("Debug Flag 7");
			    System.out.println(documents.toString());
		   }
	}
	
	public int produceID(){
		nextDocID = nextDocID + 1;
		return nextDocID;
	}
	
}