package edu.carleton.comp4601.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.apache.lucene.queryparser.classic.ParseException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

//import edu.carleton.comp4601.crawlerstuff.Controller;
import edu.carleton.comp4601.dao.*;
import edu.carleton.comp4601.graphstuff.MyGraph;
import edu.carleton.comp4601.resources.*;
import lucenestuff.MyLucene;
import lucenestuff.PageRankFun;
import edu.carleton.comp4601.crawlerstuff.*;

import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


//COMP4601 Searchable Document Archive V1.0: Luke Daschko and Mat Schmid


@Path("/sda")
public class SearchableDocumentArchive {
	//Default things we assume we need...
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	private String name;
 
	
	DocumentCollection documents;
	MyLucene lucene;
	
	//Constructor
	public SearchableDocumentArchive(){
		name = "COMP4601 Searchable Document Archive V1.0: Luke Daschko and Mat Schmid";
		documents = DocumentCollection.getInstance();
		lucene = MyLucene.getInstance();
	}
	
	//Testing Functions
	@GET
	public String printName(){
		return name;
	}
	
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXML(){
		return "<?xml version=\"1.0\"?>" + "<sda> " + name + " </sda>";
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtml(){
		return "<html> " + "<title>" + name + "</title>" + "<body><h1>" + name
				+ "</body></h1>" + "</html> ";
	
	}
	
	// Comment this function to get XML response
	@GET
    @Path("documents")
    @Produces(MediaType.APPLICATION_JSON) 
    public JSONObject getDocuments() throws JSONException {
        List<edu.carleton.comp4601.dao.Document> docs = documents.getDocuments();
        JSONObject arr = new JSONObject();
        for (edu.carleton.comp4601.dao.Document doc: docs) {
            arr.put(String.valueOf(doc.getId()), doc.jsonify());
        }
        return arr;
    }
	
	@GET
    @Path("documents")
    @Produces(MediaType.TEXT_XML)  
    public String getDocumentsXML() throws JSONException {
        List<edu.carleton.comp4601.dao.Document> docs = documents.getDocuments();
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<documents>";
        for (edu.carleton.comp4601.dao.Document doc: docs) {
            xmlString += doc.xmlify();
        }
        xmlString += "\n</documents>";
        return xmlString;
    }
	
	@GET
	@Path("crawl")
	@Produces(MediaType.APPLICATION_JSON)
	public String startCrawl(){
		 try {
			Controller.control();
			MyGraph.getInstance().saveToDB();
			lucene.indexBootup(true);
			lucene.fillUp();
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "{ Crawl has started }";
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeDocument(edu.carleton.comp4601.dao.Document newDoc) {
		//edu.carleton.comp4601.dao.Document newDoc = d.getValue();
		
		documents.add(newDoc);
		
		Response res = Response.ok().build();
		return res;
	}
	
	@Path("{doc}")
	public DocAction getDocument(@PathParam("doc") Integer id) {
		return new DocAction(uriInfo, request, id);
	}
	
	@Path("search")
	public SearchTagAction searchTaggedDocuments(){
		return new SearchTagAction(uriInfo, request);
	}
	
	@Path("delete")
	public DeleteTagAction deleteTaggedDocuments(){
		return new DeleteTagAction(uriInfo, request);
	}
	
	@GET
    @Path("reset")
    @Produces(MediaType.APPLICATION_JSON)
    public String resetDB() {
        documents.getDB().resetCollection();
        return "{ DB has been reset }";
    }
	
	@GET
	@Path("noboost")
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterBoostToOne(){
		lucene.noboost();
		return Response.ok().build();
	}
	
	@GET
	@Path("boost")
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterBoostPageRank(){
		System.out.println("BOOST HIT SDA");
		PageRankFun pr = new PageRankFun();
		documents.boost(pr.produceScoreArray());
		
		return Response.ok().build();
	}
	
	@GET
	@Path("pagerank")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject showPagerank(){
		JSONObject arr = new JSONObject();
		PageRankFun pr = new PageRankFun();
        for (String key : pr.produceScoreArray().keySet()) {
            try {
				arr.put(key, pr.produceScoreArray().get(key));
			} catch (JSONException e) {e.printStackTrace();}
        }
        return arr;
	}
	
	// Comment this function to get XML response
	@GET
	@Path("lucenesearch/{term}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject showSearch(@PathParam("term") String term){
		JSONObject hellya = new JSONObject();
		ArrayList<edu.carleton.comp4601.dao.Document> docs = null;
			try {
				lucene.searchBootup();
				docs = lucene.queryByContent2(term);
				lucene.endLucene();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			for (edu.carleton.comp4601.dao.Document doc: docs) {
	            try {
					hellya.put(String.valueOf(doc.getId()), doc.jsonify());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        return hellya;
			
	
	}
	
	@GET
	@Path("lucenesearch/{term}")
	@Produces(MediaType.TEXT_XML)
	public String showSearchXML(@PathParam("term") String term){
		String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<documents>";
		ArrayList<edu.carleton.comp4601.dao.Document> docs = null;
			try {
				lucene.searchBootup();
				docs = lucene.queryByContent2(term);
				lucene.endLucene();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (edu.carleton.comp4601.dao.Document doc: docs) {
				xmlString += doc.xmlify();
				
	        }
			xmlString += "\n</documents>";
	        return xmlString;
	}
	
}
