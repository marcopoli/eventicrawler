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

public class EventExtractor {
	
	public static void eventExtract() throws ParseException, IOException, SQLException{
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
		
		String query = "SELECT * from links where extracted = 0";
		Statement st0 = connDb.createStatement();
		ResultSet rs0 = st0.executeQuery(query);
		
		
		

		while(rs0.next()) {
		    // Do your job here with `date`.
		    //System.out.println(date);
			 String link = rs0.getString("link");
			System.out.println(link);
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
		     try {
		     Element blocco = doc.getElementsByClass("evento-dove-quando").get(0); 
		     
		     String dataString = blocco.childNode(1).childNode(0).outerHtml();
		     		     
		     String[] dates = dataString.replaceAll("da ", "").split(" a ");
		     Date da = null;
		     Date a = null;
		     DateFormat formatter = new SimpleDateFormat("MMMM d yyyy", Locale.ENGLISH);
		    
		     
		     if(dates.length > 1) {
		    	 String d = dates[0].trim().toLowerCase();
		    	 
		    	 String mese = "";
		    	 
		    	 if(d.contains("gennaio")) {
		    		 mese = "January";
		    	 }else if(d.contains("febbraio")) {
		    		 mese = "February";
		    	 }else if(d.contains("marzo")) {
		    		 mese = "March";
		    	 }else if(d.contains("aprile")) {
		    		 mese = "April";
		    	 }else if(d.contains("maggio")) {
		    		 mese = "May";
		    	 }else if(d.contains("giugno")) {
		    		 mese = "June";
		    	 }else if(d.contains("luglio")) {
		    		 mese = "July";
		    	 }else if(d.contains("agosto")) {
		    		 mese = "August";
		    	 }else if(d.contains("settembre")) {
		    		 mese = "September";
		    	 }else if(d.contains("ottobre")) {
		    		 mese = "October";
		    	 }else if(d.contains("novembre")) {
		    		 mese = "November";
		    	 }else if(d.contains("dicembre")) {
		    		 mese = "December";
		    	 }
		    	 Pattern p = Pattern.compile("\\d+");
		    	 Matcher m = p.matcher(d);
		    	 m.find();
		    	 String giorno = m.group();
		    	 String anno = "";
		    	 try {
		    	 m.find();
		    	  anno = m.group();
		    	 }catch(Exception e) {}
		    	 
		    	 d = dates[1].trim().toLowerCase();
		    	 
		    	 String mese2 = "";
		    	 
		    	 if(d.contains("gennaio")) {
		    		 mese2 = "January";
		    	 }else if(d.contains("febbraio")) {
		    		 mese2 = "February";
		    	 }else if(d.contains("marzo")) {
		    		 mese2 = "March";
		    	 }else if(d.contains("aprile")) {
		    		 mese2 = "April";
		    	 }else if(d.contains("maggio")) {
		    		 mese2 = "May";
		    	 }else if(d.contains("giugno")) {
		    		 mese2 = "June";
		    	 }else if(d.contains("luglio")) {
		    		 mese2 = "July";
		    	 }else if(d.contains("agosto")) {
		    		 mese2 = "August";
		    	 }else if(d.contains("settembre")) {
		    		 mese2 = "September";
		    	 }else if(d.contains("ottobre")) {
		    		 mese2 = "October";
		    	 }else if(d.contains("novembre")) {
		    		 mese2 = "November";
		    	 }else if(d.contains("dicembre")) {
		    		 mese2 = "December";
		    	 }
		    	 
		    	 m = p.matcher(d);
		    	 m.find();
		    	 String giorno2 = m.group();
		    	 m.find();
		    	 String anno2 = m.group();
		    	 
		    	 if(mese.equals("")) {
		    		 mese = mese2;
		    	 }
		    	 if(anno.equals("")) {
		    		 anno = anno2;
		    	 }
		    	 
		    	 da = formatter.parse(mese+" "+giorno+" "+anno );
		    	 a = formatter.parse(mese2+" "+giorno2+" "+anno2 );
		    	 
		    	 
		     }else {
		    	 String d = dates[0].trim().toLowerCase();
		    	 
		    	 String mese = "";
		    	 
		    	 if(d.contains("gennaio")) {
		    		 mese = "January";
		    	 }else if(d.contains("febbraio")) {
		    		 mese = "February";
		    	 }else if(d.contains("marzo")) {
		    		 mese = "March";
		    	 }else if(d.contains("aprile")) {
		    		 mese = "April";
		    	 }else if(d.contains("maggio")) {
		    		 mese = "May";
		    	 }else if(d.contains("giugno")) {
		    		 mese = "June";
		    	 }else if(d.contains("luglio")) {
		    		 mese = "July";
		    	 }else if(d.contains("agosto")) {
		    		 mese = "August";
		    	 }else if(d.contains("settembre")) {
		    		 mese = "September";
		    	 }else if(d.contains("ottobre")) {
		    		 mese = "October";
		    	 }else if(d.contains("novembre")) {
		    		 mese = "November";
		    	 }else if(d.contains("dicembre")) {
		    		 mese = "December";
		    	 }
		    	 Pattern p = Pattern.compile("\\d+");
		    	 Matcher m = p.matcher(d);
		    	 m.find();
		    	 String giorno = m.group();
		    	 m.find();
		    	 String anno = m.group();
		    	 
		    	 da = formatter.parse(mese+" "+giorno+" "+anno );
		    	 a = formatter.parse(mese+" "+giorno+" "+anno );
		    	 
		     }
		   
		     String posto = "";
		     String postoLink = blocco.childNode(5).attr("href");
		     if(postoLink.equals("https://iltaccodibacco.it/puglia/guida//")) {
		    	 postoLink ="";
		     }else {
			     posto = blocco.childNode(5).childNode(0).outerHtml();
		     }
		     String comune = blocco.childNode(6).outerHtml().trim();
		     
		     int featured = 0;
		     
		     Elements feat = doc.getElementsByClass("featured-label");
		     try {
		    	 Element f = feat.get(0);
		    	 featured = 1;
		     }catch(Exception e) {}
		     
		     Element tit = doc.getElementsByClass("titolo").first();
		     String titolo = tit.childNode(1).childNode(0).outerHtml();
		     
		     int free_entry = 0;
		     int arte = 0;
		     int avv = 0;
		     int cinema = 0;
		     int citt = 0;
		     int classica = 0;
		     int geek = 0;
		     int bambini = 0;
		     int folk = 0;
		     int cultura = 0;
		     int jazz = 0;
		     int concerti = 0;
		     int teatro = 0;
		     int nott = 0;
		     
		     
		     Element attributi =  doc.getElementsByClass("tdb-attributi").get(2);
		     for(int i = 0; i < attributi.children().size(); i++) {
		    	 Element attributo = attributi.child(i);
		    	 String tag = attributo.childNode(1).attr("title");
		    	 
		    	 if(tag.equalsIgnoreCase("ingresso libero")) {
		    		 free_entry = 1;
		    	 } else if(tag.equalsIgnoreCase("Arte e fotografia")) {
		    		 arte = 1;
		    	 } else if(tag.equalsIgnoreCase("Avventura, escursioni")) {
		    		 avv = 1;
		    	 } else if(tag.equalsIgnoreCase("Cinema")) {
		    		 cinema = 1;
		    	 } else if(tag.equalsIgnoreCase("Cittadinanza attiva")) {
		    		 citt = 1;
		    	 } else if(tag.equalsIgnoreCase("Eventi geek")) {
		    		 geek = 1;
		    	 } else if(tag.equalsIgnoreCase("Eventi per bambini")) {
		    		 bambini = 1;
		    	 } else if(tag.equalsIgnoreCase("Folklore e tradizioni")) {
		    		 folk = 1;
		    	 } else if(tag.equalsIgnoreCase("Incontri culturali")) {
		    		 cultura = 1;
		    	 } else if(tag.equalsIgnoreCase("Jazz e dintorni")) {
		    		 jazz = 1;
		    	 } else if(tag.equalsIgnoreCase("Musica e concerti")) {
		    		 concerti = 1;
		    	 } else if(tag.equalsIgnoreCase("Teatro e danza")) {
		    		 teatro = 1;
		    	 } else if(tag.equalsIgnoreCase("Vita notturna")) {
		    		 nott = 1;
		    	 }
		    	 
		     }
		     
		     String description = doc.getElementsByClass("evento_descrizione").outerHtml().replaceAll("0x", "").replace("\u0000", "");
		     String content_html = doc.getElementsByClass("tabella_evento").outerHtml().replaceAll("0x", "").replace("\u0000", "");
		     String pop = doc.getElementsByClass("letture").outerHtml();
		     Pattern p = Pattern.compile("\\d+");
	    	 Matcher m = p.matcher(pop);
	    	 m.find();
	    	 int letture = Integer.parseInt(m.group());
	    	 
	    	 //System.out.println(letture);
	    	 
	    	 //Inserisci nel db
	    	 String check = "SELECT * from eventi where link =? AND titolo = ?";
    		 PreparedStatement st1 = connDb.prepareStatement(check);
	    	 st1.setString(1, link);
	    	 st1.setString(2, titolo);
	    	 ResultSet rs = st1.executeQuery();
    		 System.out.println(link);
	    	 if(!rs.next()){
	    		 String q = "INSERT INTO eventi (link,titolo,posto_link,posto_nome,data_da,data_a,comune,free_entry,arte,avventura,cinema,cittadinanza,musica_classica,geek,bambini,folklore,cultura,jazz,concerti,teatro,vita_notturna,featured,descrizione,popolarita,contenuto_html) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    		 PreparedStatement st = connDb.prepareStatement(q);
	    		 st.setString(1, link);
	    		 st.setString(2, titolo);
	    		 st.setString(3, postoLink);
	    		 st.setString(4, posto);
	    		 st.setDate(5, new java.sql.Date(da.getTime()));
	    		 st.setDate(6, new java.sql.Date(a.getTime()));
	    		 st.setString(7, comune);
	    		 st.setInt(8, free_entry);
	    		 st.setInt(9, arte);
	    		 st.setInt(10, avv);
	    		 st.setInt(11, cinema);
	    		 st.setInt(12, citt);
	    		 st.setInt(13, classica);
	    		 st.setInt(14, geek);
	    		 st.setInt(15, bambini);
	    		 st.setInt(16, folk);
	    		 st.setInt(17, cultura);
	    		 st.setInt(18, jazz);
	    		 st.setInt(19, concerti);
	    		 st.setInt(20, teatro);
	    		 st.setInt(21, nott);
	    		 st.setInt(22, featured);
	    		 st.setString(23, description);
	    		 st.setInt(24, letture);
	    		 st.setString(25, content_html);
	    		// System.out.println(st.toString());
	    		 
	    		 st.execute();
	    		 
	    		 String up = "UPDATE links SET extracted = 1 where link = ?";
	    		 PreparedStatement st2 = connDb.prepareStatement(up);
	    		 st2.setString(1, link);
	    		 st2.execute();
	    	 }
	    	 
		     }catch(Exception e) {
		    	 String up = "UPDATE links SET extracted = -1 where link = ?";
	    		 PreparedStatement st2 = connDb.prepareStatement(up);
	    		 st2.setString(1, link);
	    		 st2.execute();
		     }
		}
		connDb.close();
	}
	
	
}
