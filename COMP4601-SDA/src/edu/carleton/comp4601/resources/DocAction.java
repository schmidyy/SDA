package edu.carleton.comp4601.resources;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.carleton.comp4601.dao.DocumentCollection;

import javax.ws.rs.core.Response;

public class DocAction {
	//Default stuff we assume we need...
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	Integer id;
	String rawtags;
	DocumentCollection documents;
	MyMongoDB db;

	public DocAction(UriInfo uriInfo, Request request, Integer id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		documents = DocumentCollection.getInstance();
	}
	
	@DELETE
	public Response deleteDocument() {
		Response res;
		if (documents.find(id) != null){
			documents.delete(id);
			res = Response.ok().build();
			System.out.println("Delete by id Test, " + "{"+id+"} " +  "= Success");
		}else{
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		
		return res;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getDocument() throws JSONException {
		edu.carleton.comp4601.dao.Document getDoc = documents.find(id);
		if (getDoc == null){
			throw new RuntimeException("No such user: " + id);
		}else{
			System.out.println("GET Success!: " + id);
		}
		return getDoc.jsonify();
	}
	
	@POST
    //Should have new Document in parameter...
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDocuments(edu.carleton.comp4601.dao.Document newDoc) {
        Response res;
        edu.carleton.comp4601.dao.Document oldDocument = documents.find(id);
        if (oldDocument != null){  
            System.out.println("In update");
            documents.update(oldDocument.getId(), newDoc);
            res = Response.ok().build();
            System.out.println("Update by id Test, " + "{"+id+"}" + "= Success");
        }else{
            res = Response.created(uriInfo.getAbsolutePath()).build();
        }
       
        return res;
    }
	
	

}
