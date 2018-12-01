package crawlerTaccodiBacco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkExtractor {
	

	public static void getLinks(String startDat, String endDat) throws ParseException, IOException, SQLException{
		Connection connDb = null;

		//DEBUG_CODE
        int newLinks = 0;
		int oldLinks = 0;
        int totalLinks = 0;

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
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = formatter.parse(startDat);
		Date endDate = formatter.parse(endDat);
		
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

		for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
		    // Do your job here with `date`.
		    //System.out.println(date);
		
		
			 String link = "https://iltaccodibacco.it/puglia/eventi/"+formatter.format(date)+"//";


			 URL url = new URL(link);
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
		     Elements blocchi = doc.getElementsByClass("titolo blocco-locali"); 
		     
		     for(int i = 0; i < blocchi.size();i++){
		    	 Element blocco = blocchi.get(i);
		    	 String linkPage = blocco.childNode(1).childNode(1).attr("href");
		    	 String titolo = blocco.childNode(1).childNode(1).childNode(0).outerHtml();
		    	 if(!linkPage.equals(" ") && !linkPage.equals("") ){
		    		 String check = "SELECT * from links where data_ev = ? AND link =? AND titolo = ?";
		    		 PreparedStatement st1 = connDb.prepareStatement(check);
			    	 st1.setDate(1, new java.sql.Date(date.getTime()));
			    	 st1.setString(2, linkPage);
			    	 st1.setString(3, titolo);
			    	 ResultSet rs = st1.executeQuery();

                     //DEBUG_CODE
                     oldLinks++;

			    	 if(!rs.next()){
				    	 String q = "INSERT INTO links(data_ev,link,titolo) VALUES (?,?,?)";
				    	 PreparedStatement st = connDb.prepareStatement(q);
				    	 st.setDate(1, new java.sql.Date(date.getTime()));
				    	 st.setString(2, linkPage);
				    	 st.setString(3, titolo);
				    	 st.execute();

                         //DEBUG_CODE
                         newLinks++;

			    	 }

                     //DEBUG_CODE
                     totalLinks++;
		    	 }
		     }

		}

		//DEBUG_CODE
        oldLinks = oldLinks - newLinks;
        System.out.println("New links inserted: " + newLinks);
        System.out.println("Links already in db: " + oldLinks);
        System.out.println("TOTAL links found: " + totalLinks);

		connDb.close();
	}
	
	public static void updateLinks(Date last_up){
		
			
		
		LocalDateTime ldt = LocalDateTime.now();
		ldt = (new java.util.Date(last_up.getTime()).toInstant()).atZone(ZoneId.of("Europe/Rome")).toLocalDateTime();
		DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		String startDat = formmat1.format(ldt.minusDays(7));
		
		String endDat = formmat1.format(ldt.plusMonths(6));


		//DEBUG_CODE
		System.out.println("LinkExtractor.java: finding event from " + startDat + " to " + endDat + " ...");
		
		try {
			getLinks(startDat, endDat);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
