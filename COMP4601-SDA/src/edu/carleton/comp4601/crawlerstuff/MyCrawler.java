package edu.carleton.comp4601.crawlerstuff;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import edu.carleton.comp4601.dao.DocumentCollection;
import edu.carleton.comp4601.graphstuff.MyGraph;
import edu.carleton.comp4601.graphstuff.MyVertex;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
                                                           + "|png|mp3|mp4|zip|gz))$");

    //Storage Variable(s)
    DocumentCollection documents = DocumentCollection.getInstance();
    
    //Graph variable(s)
    private MyGraph graph = MyGraph.getInstance();
    
    //Adaptive crawling variable(s)
    long startTime;
    long endTime;
    
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         
         //Start the adaptive stopwatch...
         startTime = System.nanoTime();
         
         //Filters out which pages should be visited
         return !FILTERS.matcher(href).matches()
                && (href.startsWith("http://www.ics.uci.edu/") || 
                    href.startsWith("https://sikaman.dyndns.org/courses/4601/resources/") ||
                    href.startsWith("https://sikaman.dyndns.org/courses/4601/handouts/"));
     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
    	 //Store the URL
         String url    = page.getWebURL().getURL();
         String parent = page.getWebURL().getParentUrl();
         
         System.out.println("URL: " + url);
         
         //Variables used to fill Document (later)
         String text                 = null;
         Set<WebURL> links           = null;
         HtmlParseData htmlParseData = null;
         String tikaParseddata       = null;
         String tikaMetadata         = null;
         
         if (page.getParseData() instanceof HtmlParseData) {
        	 htmlParseData = (HtmlParseData) page.getParseData();
        	 links = htmlParseData.getOutgoingUrls();
        	 text = htmlParseData.getText();
         }
      
         
         //MyVertex creation
         MyVertex cVertex = new MyVertex(url);
         MyVertex parentVertex = null;
         if (parent != null && (parent.compareTo(url) != 0)){ parentVertex = new MyVertex(parent);}
         
         //Add MyVertex to Graph
         try { graph.addVertex(cVertex, parentVertex);} 
         catch (IOException e) { e.printStackTrace(); }  
         
     
     	//Create docID
     	int docID = documents.produceID();
     
     	//Extract all metadata/otherdata using Tika on html and non html files
     	//--Tika basic setup
     	AutoDetectParser parser = new AutoDetectParser();
     	BodyContentHandler handler = new BodyContentHandler();
     	Metadata metadata = new Metadata();
     	
     	//--Get input method ready
		try {
			TikaInputStream stream;
			URL tikaURL = new URL(url);
			stream = TikaInputStream.get(tikaURL);
			
	    //--Get output ready
         	ParseContext context =new ParseContext();
        
        //--Parse
         	parser.parse(stream, handler, metadata, context);
         	
        tikaParseddata = handler.toString();
        tikaMetadata   = metadata.toString(); 
		} catch (IOException | SAXException | TikaException e) { e.printStackTrace();} 
     	
     	//(Assumed Step) Populate Document
     	edu.carleton.comp4601.dao.Document newDocument = new edu.carleton.comp4601.dao.Document(docID);
     	ArrayList<String> linksArray = new ArrayList<String>();
     	newDocument.setUrl(url);
     	newDocument.setText(text + "\n\n" + tikaParseddata + "\n\n" + tikaMetadata); //FOR NOW, we will save tika data here...
     	newDocument.setName("webpage " + docID);
     	for (WebURL u : links) { linksArray.add(u.getURL().toLowerCase()); }
     	newDocument.setLinks(linksArray);
     	
     	
     	//Upload Document to MongoDB
     	documents.add(newDocument);
                 
		//Stop adaptive stopwatch...
		endTime = System.nanoTime();
		 
		//Create adaptive pause
		long stopwatch = (endTime - startTime)/1000000; //converts to milliseconds
		System.out.println("STOPWATCH TIME: " +  stopwatch + "    " + "s:"+startTime + " e:"+endTime);
		try { Thread.sleep(stopwatch*10); } 
		catch (InterruptedException e) { e.printStackTrace(); }
     }
         
             
    }
