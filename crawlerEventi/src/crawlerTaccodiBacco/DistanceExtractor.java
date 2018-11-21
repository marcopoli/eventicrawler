package crawlerTaccodiBacco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DistanceExtractor {
	
	public static void main(String[] args) throws ParseException, IOException, SQLException{
		Connection connDb = null;
		try
	    {
	    	Class.forName("org.postgresql.Driver");
			if (connDb == null) {
				connDb = DriverManager.getConnection("jdbc:postgresql://127.0.0.1/PugliaEventi?characterEncoding=utf8", "postgres", "postgres");
			}  
	    }
	    catch(ClassNotFoundException cnfe)
	    {
	        System.out.println("Error loading class!");
	        cnfe.printStackTrace();
	    }
		
		String query = "SELECT DISTINCT comune from eventi WHERE comune NOT IN (SELECT DISTINCT a from distanze) ";
		Statement st0 = connDb.createStatement();
		ResultSet rs0 = st0.executeQuery(query);
		
		
		

		while(rs0.next()) {
		    // Do your job here with `date`.
		    //System.out.println(date);
			 String comune = rs0.getString("comune").replace("'", "''");


				String query1 = "SELECT DISTINCT comune FROM ( (SELECT comune FROM comuni WHERE regione = 'PUG') UNION (SELECT DISTINCT comune from eventi)) as com WHERE comune <> '"+comune+"' ";
				Statement st1 = connDb.createStatement();
				System.out.println(query1);
				ResultSet rs1 = st1.executeQuery(query1);
				
			 while (rs1.next()) {
				 String comuneB = rs1.getString("comune");
				 
				 URL url = new URL ("https://www.distance.to/"+comune.replace("''","'")+"/"+comuneB);
				 URLConnection conn = url.openConnection();
				 conn.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
				 conn.setRequestProperty("Accept","text/html");
				 conn.setRequestProperty("Connection","keep-alive");
				 conn.connect();
				 InputStream stream = conn.getInputStream();
				 InputStreamReader reader = new InputStreamReader(stream);
				 BufferedReader in = new BufferedReader(reader);
				 String inputLine;
				 String html ="";
			        while ((inputLine = in.readLine()) != null) 
			        	html=html+" "+inputLine;
			        
			        Document doc = Jsoup.parse(html);   
				     try {
				     double dis = Double.parseDouble(doc.select("#logo > span.main-distances > span.headerAirline > span.e2nd > span.value.km").html().replace("(", "").replace(")", "").replace(" km", ""));
				    // System.out.println(dis);
				     String q = "INSERT INTO distanze VALUES (?,?,?)";
				     PreparedStatement st2 = connDb.prepareStatement(q);
				     st2.setString(1, comune.replace("''", "'"));
				     st2.setString(2, comuneB.replace("''", "'"));
				     st2.setDouble(3, dis);
				     st2.execute();
				     st2.close();
				     
				     }catch(Exception e) {}
			       
			 }

		
	}
	
	}
}
