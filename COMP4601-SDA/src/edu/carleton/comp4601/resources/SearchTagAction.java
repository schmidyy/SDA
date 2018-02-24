package edu.carleton.comp4601.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.carleton.comp4601.dao.DocumentCollection;

public class SearchTagAction {
	//Default stuff we assume we need...
		@Context
		UriInfo uriInfo;
		@Context
		Request request;
		
		String rawTags;
		List<String> organizedTags;
		String action;
		DocumentCollection documents;
		
	//Constructor
	public SearchTagAction(UriInfo uriInfo, Request request) {
		this.uriInfo = uriInfo;
		this.request = request;
		documents = DocumentCollection.getInstance();
	}
	
	//Cleans tags into list of tags
	@GET
    @Path("{tags}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject search(@PathParam("tags") String rawtags) {
        JSONObject json = null;
        try {
            json = filter(rawtags);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }
   
    public JSONObject filter(String rawtags) throws JSONException{
        JSONObject json = new JSONObject();
        //Step one: search threw document
        List<edu.carleton.comp4601.dao.Document> docs = documents.getDocuments();
        for(int i=0; i<docs.size(); i++){
            edu.carleton.comp4601.dao.Document docAti = docs.get(i);
            //Step two: compare each document to see if it has ANY of the tags
            List<String> tagArr = Arrays.asList(rawtags.split(","));
           
            for (String tag : tagArr) {
                if (docAti.getTags().contains(tag)) {
                    json.put(String.valueOf(docAti.getId()), docAti.jsonify());
                }
            }
        }  
        return json;
    }
	
	
	
	

}
