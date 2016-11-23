/**
 * Name 		- 	TraverseLinks
 * Description	-	This code is built to fetch all the links within a base URL/URI. It will hit a base link/URL and store it in a Map. 
 * 					After that it will hit all the links within the previous link and store all of them in the Map. And this way it
 * 					will traverse each and every link and store the link in the Map.
 * 					Few checks are provided - 
 * 					1. Domain check to avoid links which are not needed (like Facebook, Youtube, etc.)
 * 					2. URL protocol check - only http and https are permitted.
 * 					3. Check for a link which is already visited and no need to visit again.
 * 					4. URLs with relative paths are converted to absolute paths. 
 * Author		-	Arnab Banerjee
 * Date			-	18-Nov-2016
 * Execution Path - 
 * 		1. Create an object of TraverseLinks class.
 * 		2. Either pass references for a Map<String, Object>, and two List<String> objects to the constructor or 
 * 		   just call the default constructor. For either way you will get a reference for those objects said above.
 * 		3. Call the startCheckingLinks() method on the object just created by passing the bas URL. *
 */

package org.iadb.webcrawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;

public class TraverseLinks 
{
	public Map<String, Object> params;
	public List<String> visitedLinks;
	public List<String> errors;
	private int count;
	
	
	// Domains which to avoid
	public static final String FACEBOOK = "facebook";
	public static final String TWITTER = "twitter";
	public static final String FLICKR = "flickr";
	public static final String YOUTUBE = "youtube";
	public static final String GOOGLE = "google";
	public static final String YAHOO = "yahoo";
	public static final String VIMEO = "vimeo";
	public static final String DAILYMOTION = "dailymotion";
	public static final String APPLE = "apple";
	public static final String MERCANTILE = "mercantile";
	public static final String WORDPRESS = "wordpress";
	public static final String MERCH = "merch";
	public static final int COUNTER = 5000;
	
	public TraverseLinks(Map<String, Object> params, List<String> visitedLinks, List<String> errors) {
		this.params = params;
		this.visitedLinks = visitedLinks;
		this.errors = errors;
		this.count = 0;
	}
	
	public TraverseLinks() {
		params = new HashMap<String, Object>();
		visitedLinks = new ArrayList<String>();
		errors = new ArrayList<String>();
		this.count = 0;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public List<String> getVisitedLinks() {
		return visitedLinks;
	}

	public void setVisitedLinks(List<String> visitedLinks) {
		this.visitedLinks = visitedLinks;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public void writeInFile() {
		File file = new File("/home/arnab/Documents/all_links");
		try {
			PrintWriter pw = new PrintWriter(file);
			for(String link: this.visitedLinks) {
				pw.println(link);
			}
			/*pw.println("\nVISITED_LINKS\n");
			for(String link: this.visitedLinks) {
				pw.println(link);
			}
			pw.println("\nERRORS\n");
			for(String link: this.errors) {
				pw.println(link);
			}*/
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void startCheckingLinks(String url) {    	
    	try {       		
    		// Put the first url in the map and in the visitedLinks list, so that it can be recognized later
    		this.params.put(AlchemyLanguage.URL, url);
    		this.visitedLinks.add(url);
    		
    		// Get Protocol
    		Document doc = Jsoup.connect(url).get();    		

    		// Get URI object to extract the domain
    		URI uri = new URI(url);
            String domain = uri.getHost();
            if(url.startsWith("http")) {
            	domain = "http://" + domain;
            } else if(url.startsWith("https")){
            	domain = "https://" + domain;
            }
            
    		// Get all the links
    		Elements links = doc.select("a[href]");
    		
    		// DEBUGS
    		//System.out.println(links.size());
    		
    		// Loop all the links.
    		for (Element link : links) {
    			String temp = link.attr("href");
    			temp = ((temp.charAt(0) == '/')?(domain + temp):(temp));
    			//System.out.println(temp); // DEBUG
    			temp = temp.toLowerCase().trim();
    			try {
    				// Call the method checkLink to check each link individually
    				this.checkLink(temp);
    			} catch(Exception e) {
    				this.errors.add(e.getMessage());
    				continue;
    			} 
    		}
    	} catch (IOException e) {
    		this.errors.add(e.getMessage());
    		e.printStackTrace();
    	} catch (URISyntaxException e) {
    		this.errors.add(e.getMessage());
			e.printStackTrace();
		}
    }
    
    private void checkLink(String url) {  
    	// Check counter to max limit. This is just for testing.
    	
    	
    	// Trim the url string
    	url = url.trim().toLowerCase();
    	
    	// Check the link url is of generic domain like Facebook or Youtube. If so, just skip it.
    	// Or if you want to scan these links too, just comment the below if-check.
    	if(url.contains(TraverseLinks.FACEBOOK) || 
    			url.contains(TraverseLinks.FLICKR) || 
    			url.contains(TraverseLinks.GOOGLE) || 
    			url.contains(TraverseLinks.TWITTER) || 
    			url.contains(TraverseLinks.YAHOO) || 
    			url.contains(TraverseLinks.YOUTUBE) || 
    			url.contains(TraverseLinks.VIMEO) || 
    			url.contains(TraverseLinks.DAILYMOTION) || 
    			url.contains(TraverseLinks.APPLE) || 
    			url.contains(TraverseLinks.MERCANTILE) || 
    			url.contains(TraverseLinks.WORDPRESS) || 
    			url.contains(TraverseLinks.MERCH)) {
    		return;
    	}
    	    	
    	// Check if URL is stared with http or https. If not, add them before url.
    	if(!url.startsWith("http") && !url.startsWith("https")){
            url = "http://" + url;
    	}    	
    	
    	// Check the url is visited or not. If not, add it to the visited url's list.
    	if(this.visitedLinks.contains(url)) {
    		this.count++;
    		return;
    	} else {
    		this.count++;
    		this.visitedLinks.add(url);
    	}
    	
    	// DEBUG
    	System.out.println(url);
    	
    	
    	if(this.count >= TraverseLinks.COUNTER) {
    		return;
    	}
    	
    	// Put the url in the final map.
    	this.params.put(AlchemyLanguage.URL, url);
    	
    	try {
    		// Get the protocol
        	Document doc = Jsoup.connect(url).get();
        	
        	// Extract the domain from the url
        	URI uri = new URI(url);
            String domain = uri.getHost();
            if(url.startsWith("http")) {
            	domain = "http://" + domain;
            } else if(url.startsWith("https")){
            	domain = "https://" + domain;
            }
            
            // Get all the links
            Elements links = doc.select("a[href]");
            
            // DEBUG
    		System.out.println(links.size());
    		
    		// Traverse all the links
    		for(Element link: links) {
    			String temp = link.attr("href");
    			temp = ((temp.charAt(0) == '/')?(domain + temp):(temp));
    			//System.out.println(temp); // DEBUG
    			if(!temp.equals(domain + "/")) {
    				this.checkLink(temp);
    			} else {
    				return;
    			}
    		}
    	} catch(IllegalArgumentException e) { // Handling for any illegal string argument
			this.errors.add(e.getMessage());
			return;
		} catch(SocketTimeoutException e) { // Handling for any reading timed out
			this.errors.add(e.getMessage());
			return;
		} catch(UnknownHostException e) { // Handling for any unknown host
			this.errors.add(e.getMessage());
			return;
		} catch (IOException e) {
			this.errors.add(e.getMessage());
			return;
		} catch (URISyntaxException e) {
			this.errors.add(e.getMessage());
			return;
		} catch(StringIndexOutOfBoundsException e) {
			this.errors.add(e.getMessage());
			return;
		}		
    }
    
    public static void main( String[] args )
    {
    	String url = "http://www.iadb.org/en/research-and-data/research-data,1612.html";
    	TraverseLinks app = new TraverseLinks();
    	app.startCheckingLinks(url);
    	app.writeInFile();
    	
    }
}
