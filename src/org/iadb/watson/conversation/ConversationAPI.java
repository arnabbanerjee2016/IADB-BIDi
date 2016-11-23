package org.iadb.watson.conversation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.Entity;
import com.ibm.watson.developer_cloud.conversation.v1.model.Intent;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class ConversationAPI {
	
	private ConversationService conversationService;
	private MessageRequest messageRequest;
	private MessageResponse messageResponse;
	private static final String CONVERSATION_SERVICE_USERNAME = "6d5fe943-ad3f-4837-aaa0-082b85e72366";
	private static final String CONVERSATION_SERVICE_PASSWORD = "6DuwZj8z3lzj";
	private static final String CONVERSATION_SERVICE_URL = "https://gateway.watsonplatform.net/conversation/api";
	private static final String CONVERSATION_SERVICE_VERSION_DATE = "2016-09-20";
	private static final String CONVERSATION_SERVICE_WORKSPACE_ID = "fcef3271-d888-4dcb-a64c-492e9673b618";
	private List<Entity> entities;
	private List<Intent> intents;
	private Map<String, Object> output;
	
	
	public ConversationAPI() {
		/*String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		BasicDBList list = null;
		BasicDBObject bludb = null;
		if (VCAP_SERVICES != null) {
		   BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
		   String thekey = null;
		   Set<String> keys = obj.keySet();
		   for (String eachkey : keys)
		      if (eachkey.contains("conversation"))
		         thekey = eachkey;
		   list = (BasicDBList) obj.get(thekey);
		   bludb = (BasicDBObject) list.get("0");
		   bludb = (BasicDBObject) bludb.get("credentials");
		}*/
		conversationService = new ConversationService(ConversationAPI.CONVERSATION_SERVICE_VERSION_DATE);
		conversationService.setUsernameAndPassword(ConversationAPI.CONVERSATION_SERVICE_USERNAME, ConversationAPI.CONVERSATION_SERVICE_PASSWORD);
		entities = new ArrayList<Entity>();
		intents = new ArrayList<Intent>();
		output = new HashMap<String, Object>();
		
		entities.add(new Entity("country", "India", null));
		entities.add(new Entity("country", "Bangladesh", null));
		entities.add(new Entity("country", "Nepal", null));
		
		intents.add(new Intent("search", 1.0));
		intents.add(new Intent("view", 1.0));
		intents.add(new Intent("travel", 1.0));
		
		output.put("text", "Giving you details of @country");
		
		
	}
	
	public void play() {
		messageRequest = new MessageRequest.Builder().inputText("Hi!").build();
		messageResponse = conversationService.message(ConversationAPI.CONVERSATION_SERVICE_WORKSPACE_ID, messageRequest).execute();
		//messageResponse.setEntities(entities);
		//messageResponse.setIntents(intents);		
		//messageResponse.setOutput(output);
		System.out.println(messageResponse.toString());
	}
	
	public static void main(String[] args) {
		ConversationAPI conv = new ConversationAPI();
		conv.play();
	}
		

}
