package crawlerTaccodiBacco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
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

public class MeteoExtractor {
	public static boolean error = false;
	public static void extractPastMeteoData() throws Exception{
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
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		String query = "SELECT * from eventi where meteo_bool = 0 AND data_a < '"+formatter.format(date)+"' ";
		System.out.println(query);
		Statement st0 = connDb.createStatement();
		ResultSet rs0 = st0.executeQuery(query);
		
		//Check meteo passato
		

		while(rs0.next()) {
			error = false;
		    // Do your job here with `date`.
		    //System.out.println(date);
			 String link = rs0.getString("link");
			// System.out.println(link);
			
			 Date da = rs0.getDate("data_da");
			 Date a = rs0.getDate("data_a");
			 
			 Calendar start = Calendar.getInstance();
			 start.setTime(da);
			 Calendar end = Calendar.getInstance();
			 end.setTime(a);
			 System.out.println(da);
			 System.out.println(a);
			 String comune = rs0.getString("comune");
			 
			 if(start.equals(end)) {
				 String mese = getMonth(a.getMonth());
				 String iniziale = mese.substring(0, 1);
				 String finale = mese.substring(1, mese.length());
				 String nomeMese = iniziale.toUpperCase() + finale.toLowerCase();
				 			 
				 
				 String linkMeteo = "https://www.ilmeteo.it/portale/archivio-meteo/"+URLEncoder.encode(comune)+"/"+(a.getYear()+1900)+"/"+nomeMese+"/"+a.getDate();
				
				 getMeteoData(linkMeteo,link,rs0.getString("titolo"), a, connDb);
			 }else {
			 
				 for (Date dat = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), dat = start.getTime()) {
					 String mese = getMonth(dat.getMonth());
					 String iniziale = mese.substring(0, 1);
					 String finale = mese.substring(1, mese.length());
					 String nomeMese = iniziale.toUpperCase() + finale.toLowerCase();
					
					 
					 
					 String linkMeteo = "https://www.ilmeteo.it/portale/archivio-meteo/"+URLEncoder.encode(comune)+"/"+(dat.getYear()+1900)+"/"+nomeMese+"/"+dat.getDate();
					 getMeteoData(linkMeteo,link,rs0.getString("titolo"), dat, connDb);
					 Thread.sleep(100);
				 }
			 }
			 if(error!= true) {
				 String up = "UPDATE eventi SET meteo_bool = 1 where link = ?";
	    		 PreparedStatement st3 = connDb.prepareStatement(up);
	    		 st3.setString(1, link);
	    		 st3.execute();
			 } if(error== true) {
				 String up = "UPDATE eventi SET meteo_bool = -1 where link = ?";
	    		 PreparedStatement st3 = connDb.prepareStatement(up);
	    		 st3.setString(1, link);
	    		 st3.execute();
			 }
			 Thread.sleep(5000);
		}
		connDb.close();
	}
	
	public static void getMeteoData(String linkMeteo, String linkEvento, String titolo, Date dataevento, Connection connDb) throws Exception  {
		 try {
			 URL url = new URL(linkMeteo);
				System.out.println(linkMeteo);
			 URLConnection conn = url.openConnection();
			 conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
			 conn.setRequestProperty("Accept","text/html");
			 conn.setRequestProperty("Connection","keep-alive");
			 conn.setConnectTimeout(3000);
	
			 conn.connect();
			 InputStream stream = conn.getInputStream();
			 InputStreamReader reader = new InputStreamReader(stream);
			 BufferedReader in = new BufferedReader(reader);
			 String inputLine;
			 String html ="";
		        while ((inputLine = in.readLine()) != null) 
		        	html=html+" "+inputLine;
		        
		     Document doc = Jsoup.parse(html);   
	       
		     int tempMedia = Integer.parseInt(doc.select("#mainc > div > table:nth-of-type(2) > tbody > tr:nth-of-type(2) > td:nth-of-type(2)").html().replace(" °C", ""));
		     int ventoMedio = Integer.parseInt(doc.select("#mainc > div > table:nth-child(7) > tbody > tr:nth-child(10) > td:nth-child(2)").html().replace(" km/h", ""));
		     String fenomeni = doc.select("#mainc > div > table:nth-child(7) > tbody > tr:nth-child(16) > td:nth-child(2)").text();
		     int sereno = 0;
		     int coperto = 0;
		     int poco_nuv = 0;
		     int pioggia = 0;
		     int temporale = 0;
		     int nebbia = 0;
		     int neve = 0;
		     
		     if(fenomeni.contains("Pioggia")) {
		    	 pioggia = 1;
		     }
		     if(fenomeni.contains("Neve")) {
		    	 neve = 1;
		     }
		     if(fenomeni.contains("Temporale")) {
		    	 pioggia = 1;
		     }
		     if(fenomeni.contains("Nebbia")) {
		    	 pioggia = 1;
		     }
		     
		     String cond = doc.select("#mainc > div > table:nth-child(7) > tbody > tr:nth-child(17) > td:nth-child(2)").text();
		     if(cond.contains("sereno")) {
		    	 sereno = 1;
		     }
		     if(cond.contains("nuvoloso")) {
		    	 poco_nuv = 1;
		     }
		     if(cond.contains("coperto")) {
		    	 coperto = 1;
		     }
		     
		     int primavera = 0;
		     int estate = 0;
		     int autunno = 0;
		     int inverno = 0;
			 
			 String seasons[] = {
					    "Winter", "Winter",
					    "Spring", "Spring", "Spring",
					    "Summer", "Summer", "Summer",
					    "Fall", "Fall", "Fall",
					    "Winter"
			};
			String stagione =  seasons[ dataevento.getMonth() ];
			
			if(stagione.equals("Winter")) {
				inverno = 1;
			}
			if(stagione.equals("Spring")) {
				primavera = 1;
			}
			if(stagione.equals("Summer")) {
				estate = 1;
			}
			if(stagione.equals("Fall")) {
				autunno = 1;
			}
		     
			//Inserisci nel db
	    	 String check = "SELECT * from meteo where link =? AND titolo = ? AND dataevento = ?";
	    	 PreparedStatement st1 = connDb.prepareStatement(check);
	    	 st1.setString(1, linkEvento);
	    	 st1.setString(2, titolo);
	    	 st1.setDate(3, new java.sql.Date(dataevento.getTime()));
	    	 ResultSet rs = st1.executeQuery();
	    	
	    	 if(!rs.next()){
			
	    		 String q = "INSERT INTO meteo(link,titolo,dataevento,primavera,estate,autunno,inverno,sereno,coperto,poco_nuvoloso,pioggia,temporale,nebbia,neve,temperatura,velocita_vento) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    		 PreparedStatement st2 = connDb.prepareStatement(q);
		    	 st2.setString(1, linkEvento);
		    	 st2.setString(2, titolo);
		    	 st2.setDate(3, new java.sql.Date(dataevento.getTime()));
		    	 st2.setInt(4, primavera);
		    	 st2.setInt(5, estate);
		    	 st2.setInt(6, autunno);
		    	 st2.setInt(7, inverno);
		    	 st2.setInt(8, sereno);
		    	 st2.setInt(9, coperto);
		    	 st2.setInt(10, poco_nuv);
		    	 st2.setInt(11, pioggia);
		    	 st2.setInt(12, temporale);
		    	 st2.setInt(13, nebbia);
		    	 st2.setInt(14, neve);
		    	 st2.setInt(15, tempMedia);
		    	 st2.setInt(16, ventoMedio);
		    	 st2.execute();
		    	 
		    	
	    	 }
			
	     
		 }catch(Exception e) {error = true; e.printStackTrace();}
	}
	
	public static String getMonth(int month) {
	    return new DateFormatSymbols(Locale.ITALIAN).getMonths()[month];
	}
	
}
