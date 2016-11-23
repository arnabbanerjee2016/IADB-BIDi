package org.iadb.cloudantdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iadb.webcrawl.TraverseLinks;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.CombinedResults;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Entity;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;

public class Test {

	public static void main(String[] args) throws MalformedURLException, FileNotFoundException {
		
		String strUrl = "http://www.iadb.org/en/projects/project-details,1301.html?Country=&Sector=AS&Status=Completed&query=";
	    //Alchemy language calling
	     AlchemyLanguage service = new AlchemyLanguage();
	     service.setApiKey("cb6e8f9c95e93f098a285b70bddd2cf1e08eb175");
	     Map<String, Object> params = new HashMap<String, Object>();
	     
	     params.put(AlchemyLanguage.URL, strUrl);
		 	
//			Recursive extraction
		 	List<String> visitedLinks = new ArrayList<String>(); // Holds all the visited links.
			List<String> errors = new ArrayList<String>();	// Holds all the errors which are encountered during the execution.
			TraverseLinks app = new TraverseLinks(params, visitedLinks, errors); // Creating the object of TraverseLinks
			app.startCheckingLinks(strUrl);
		 	
		 	
		 	CombinedResults cmbresult=service.getCombinedResults(params).execute();    
		  //Cloudant DB Connection
		  String url = "https://55d872e6-3a06-4140-9221-aba28e4c825b-bluemix:4c00eb6a2e7831e5d1d8d2ef7f56b104a82448d235e4b1fdba5b6dce950ed110@55d872e6-3a06-4140-9221-aba28e4c825b-bluemix.cloudant.com";
		  String username = "55d872e6-3a06-4140-9221-aba28e4c825b-bluemix";
		  String password = "4c00eb6a2e7831e5d1d8d2ef7f56b104a82448d235e4b1fdba5b6dce950ed110";
		  //CloudantClient client = new CloudantClient(url, username, password);
		  CloudantClient client = ClientBuilder.url(new URL(url))
                  .username(username)
                  .password(password)
                  .build();
			Database db = client.database("demo_arnab", false);  //demo
			try {
				Response resp = db.save(cmbresult);
			} catch(Exception e) {
				//e.printStackTrace();
			}
			
			List<Entity> list = cmbresult.getEntities();
			List<Keyword> keywords = cmbresult.getKeywords();
			
			
			
			System.out.println();System.out.println();System.out.println();System.out.println();System.out.println();
			/*for(Entity entity: list) {
				System.out.println(entity.getText());
				//System.out.println(entity.getSentiment());
				//entityList += entity.getText() + ",";
			}
			System.out.println();
			for(Keyword entity: keywords) {
				System.out.println(entity.getText());
				//keywordList += entity.getText() + ",";
			}
			System.out.println();*/
			
			String entityList = "";
			String keywordList = "";
			
			entityList = list.get(0).getText();
			keywordList = keywords.get(0).getText();
			
			for(int i = 1; i < list.size(); i++) {
				entityList += "," + list.get(i).getText();
			}
			
			for(int i = 1; i < keywords.size(); i++) {
				keywordList += "," + keywords.get(i).getText();
			}
			
			File entityFile = new File("/home/arnab/Google_Drive/IADB/Generated CSVs/ENTITY_" + new Date().getTime() + ".csv");
			File keywordFile = new File("/home/arnab/Google_Drive/IADB/Generated CSVs/KEYWORD_" + new Date().getTime() + ".csv");
			
			PrintWriter EntityWriter = new PrintWriter(entityFile);
			PrintWriter KeywordWriter = new PrintWriter(keywordFile);
			
			EntityWriter.println(entityList);
			KeywordWriter.println(keywordList);
			
			EntityWriter.close();
			KeywordWriter.close();

	}

}
