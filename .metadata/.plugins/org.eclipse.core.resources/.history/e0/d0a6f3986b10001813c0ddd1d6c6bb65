package edu.carleton.comp4601.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

public class DocAction {
	//Default stuff we assume we need...
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	String id;
	String rawtags;

	public DocAction(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}
	
	@DELETE
	public void deleteDocument() {
		//Code here would try and delete Document from db/DocumentsCollection
		System.out.println("Delete by id Test, " + "{"+id+"} " +  "= Success");
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getDocument() {
		//Code here would make sure document exists before returning it
		System.out.println("Show by id Test, " + "{"+id+"}"  + "= Success");
		return id;
	}
	
	@POST
	public void updateDocuments() {
			System.out.println("Update by id Test, " + "{"+id+"}" + "= Success");
	}
	
	

}
