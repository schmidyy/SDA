package edu.carleton.comp4601.graphstuff;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MyVertex {
	
	private String url;
	Elements images;
	Elements links;
	Elements bodys;
	Elements headers;
	Elements paragraphs;
	
	public MyVertex(String url){
		this.url = url;
	}
	
	public void setURL(String url){
		this.url = url;
	}
	public String getURL(){
		return url;
	}
	
	public void addDoc(Document jdoc){
		
	}
	
	@Override
	public boolean equals(Object o) 
	{
	    if (o instanceof MyVertex) 
	    {
	      MyVertex c = (MyVertex) o;
	      if ( this.url.equals(c.url) ) 
	         return true;
	    }
	    return false;
	}
}
