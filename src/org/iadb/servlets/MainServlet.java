package org.iadb.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.CombinedResults;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Entity;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;
import com.ibm.watson.developer_cloud.alchemy.v1.model.LanguageSelection;
import com.ibm.watson.developer_cloud.service.exception.BadRequestException;

import org.iadb.webcrawl.TraverseLinks;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public MainServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String strUrl = request.getParameter("URL");
		// Alchemy language calling
		AlchemyLanguage service = new AlchemyLanguage();
		service.setApiKey("cb6e8f9c95e93f098a285b70bddd2cf1e08eb175");
		LanguageSelection lang = LanguageSelection.ENGLISH;
		service.setLanguage(lang);
		Map<String, Object> params = new HashMap<String, Object>();

		// Recursive extraction
		/*
		 * Document doc; try { // need http protocol doc =
		 * Jsoup.connect(strUrl).get(); // get all links Elements links =
		 * doc.select("a[href]"); for (Element link : links) { // get the value
		 * from href attribute //System.out.println("\nlink : " +
		 * link.attr("href")); //System.out.println("text : " + link.text());
		 * //params.put(AlchemyLanguage.URL, link.attr("href")); } } catch
		 * (IOException e) { e.printStackTrace(); }
		 */

		params.put(AlchemyLanguage.URL, strUrl);

		// Recursive extraction
		List<String> visitedLinks = new ArrayList<String>(); // Holds all the
																// visited
																// links.
		List<String> errors = new ArrayList<String>(); // Holds all the errors
														// which are encountered
														// during the execution.
		TraverseLinks app = new TraverseLinks(params, visitedLinks, errors); // Creating
																				// the
																				// object
																				// of
																				// TraverseLinks
		app.startCheckingLinks(strUrl);

		CombinedResults cmbresult = null;
		try {
			cmbresult = service.getCombinedResults(params).execute();
		} catch (BadRequestException e) {
			e.printStackTrace();
		}
		// Cloudant DB Connection
		String url = "https://55d872e6-3a06-4140-9221-aba28e4c825b-bluemix:4c00eb6a2e7831e5d1d8d2ef7f56b104a82448d235e4b1fdba5b6dce950ed110@55d872e6-3a06-4140-9221-aba28e4c825b-bluemix.cloudant.com";
		String username = "55d872e6-3a06-4140-9221-aba28e4c825b-bluemix";
		String password = "4c00eb6a2e7831e5d1d8d2ef7f56b104a82448d235e4b1fdba5b6dce950ed110";
		CloudantClient client = ClientBuilder.account(url).username(username).password(password).build();
		Database db = client.database("demo_arnab", false); // demo
		try {
			Response resp = db.save(cmbresult);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		// Response response = db.

		/*
		 * DashDB connection.
		 */
		/*
		 * String VCAP_SERVICES = System.getenv("VCAP_SERVICES"); BasicDBList
		 * list = null; BasicDBObject bludb = null; if (VCAP_SERVICES != null) {
		 * BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES); String
		 * thekey = null; Set<String> keys = obj.keySet(); for (String eachkey :
		 * keys) if (eachkey.contains("dashDB")) thekey = eachkey; list =
		 * (BasicDBList) obj.get(thekey); bludb = (BasicDBObject) list.get("0");
		 * bludb = (BasicDBObject) bludb.get("credentials"); } Connection con =
		 * null; try { Class.forName("com.ibm.db2.jcc.DB2Driver"); String
		 * jdbcurl = (String) bludb.get("jdbcurl"); // use ssljdbcurl to connect
		 * via SSL String user = (String) bludb.get("username"); String passwd =
		 * (String) bludb.get("password"); con =
		 * DriverManager.getConnection(jdbcurl, user, passwd);
		 * con.setAutoCommit(true); } catch (SQLException e) {
		 * e.printStackTrace(); } catch (ClassNotFoundException e) {
		 * e.printStackTrace(); } Statement stmt = null; String sqlStatement =
		 * ""; try { stmt = con.createStatement(); sqlStatement =
		 * "SELECT TABNAME,TABSCHEMA FROM SYSCAT.TABLES FETCH FIRST 10 ROWS ONLY"
		 * ; ResultSet rs = stmt.executeQuery(sqlStatement); } catch
		 * (SQLException e) { e.printStackTrace(); } finally { try {
		 * con.close(); } catch (SQLException e) { e.printStackTrace(); } }
		 */

		List<Entity> list = cmbresult.getEntities();
		List<Keyword> keywords = cmbresult.getKeywords();

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		/*
		 * for(Entity entity: list) { System.out.println(entity.getText());
		 * //System.out.println(entity.getSentiment()); //entityList +=
		 * entity.getText() + ","; } System.out.println(); for(Keyword entity:
		 * keywords) { System.out.println(entity.getText()); //keywordList +=
		 * entity.getText() + ","; } System.out.println();
		 */

		String entityList = "";
		String keywordList = "";

		entityList = list.get(0).getText();
		keywordList = keywords.get(0).getText();

		for (int i = 1; i < list.size(); i++) {
			entityList += "," + list.get(i).getText();
		}

		for (int i = 1; i < keywords.size(); i++) {
			keywordList += "," + keywords.get(i).getText();
		}

		File entityFile = new File(
				"/home/arnab/Google_Drive/IADB/Generated CSVs/ENTITY_" + new Date().getTime() + ".csv");
		File keywordFile = new File(
				"/home/arnab/Google_Drive/IADB/Generated CSVs/KEYWORD_" + new Date().getTime() + ".csv");

		PrintWriter EntityWriter = new PrintWriter(entityFile);
		PrintWriter KeywordWriter = new PrintWriter(keywordFile);

		EntityWriter.println(entityList);
		KeywordWriter.println(keywordList);

		EntityWriter.close();
		KeywordWriter.close();

		response.sendRedirect("Response.html");

	}

}
