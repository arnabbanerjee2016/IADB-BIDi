package org.iadb.document_conversion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;
import com.ibm.watson.developer_cloud.document_conversion.v1.model.Answers;

public class ConvertDocument {

	private DocumentConversion service;
	private File file;
	private static final String CONVERSATION_SERVICE_USERNAME = "4b9a728b-d459-4ac5-bc48-a3f8e4a9ac83";
	private static final String CONVERSATION_SERVICE_PASSWORD = "ZS0AxqFPtnD7";
	private static final String CONVERSATION_SERVICE_URL = "https://gateway.watsonplatform.net/document-conversion/api";
	private static final String CONVERSATION_SERVICE_VERSION_DATE = "2015-12-15";
	private static final String CONVERSATION_SERVICE_WORKSPACE_ID = "1a15a6b8-4231-4ddb-91f8-9df513cb2f8b";
	
	public ConvertDocument() {
		service = new DocumentConversion(ConvertDocument.CONVERSATION_SERVICE_VERSION_DATE);
		service.setUsernameAndPassword(ConvertDocument.CONVERSATION_SERVICE_USERNAME, ConvertDocument.CONVERSATION_SERVICE_PASSWORD);
		//file = new File("http://www.ibm.com/watson/developercloud/document-conversion/api/v1/");
		try {
			URL url = new URL("http://www.ibm.com/watson/developercloud/document-conversion/api/v1/");
			url.openConnection();
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		JsonParser json = new JsonParser();
		String configAsString = "{"
				  + "\"word\":{"
				  + "\"heading\":{"
				  + "\"fonts\":["
				  + "{\"level\":1,\"min_size\":24},"
				  + "{\"level\":2,\"min_size\":16,\"max_size\":24}"
				  + "]}}}";
		JsonObject object = json.parse(configAsString).getAsJsonObject();
		Answers ans = service.convertDocumentToAnswer(file, null, object).execute();
		System.out.println(ans);
	}
	
	public static void main(String[] args) {

		ConvertDocument doc = new ConvertDocument();
		doc.play();

	}

}
