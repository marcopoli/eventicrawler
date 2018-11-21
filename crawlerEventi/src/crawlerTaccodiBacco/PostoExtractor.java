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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PostoExtractor {
	
	

	public static void main(String args[]) throws ParseException, IOException, SQLException, InterruptedException{
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
		
		

		for (int k = 10511; k < 99999; k++) {
		    // Do your job here with `date`.
		    //System.out.println(date);
		
		
			 String link = "https://iltaccodibacco.it/puglia/guida/"+k+"/";
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
		     
		     
		     if (doc.html().contains("Pagina non trovata!")) {
		    	 System.out.println("Element not exist");
		     }else {
		     
			     Element titolob = doc.getElementsByClass("tdb-recensione-profilo").get(0); 
			     String titolo = titolob.childNode(0).outerHtml();
			     String tipo = "";
			     try {
			    	 tipo =doc.getElementsByClass("tdb-locale-tag").get(0).childNode(0).outerHtml();
			     }catch(Exception e) {}
			     
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
			     int benessere = 0;
			     int bere = 0;
			     int mangiare = 0;
			     int dormire = 0;
			     int golosita = 0;
			     int librerie = 0;
			     int romantico = 0;
			     int museo = 0;
			     int spiagge = 0;
			     int informale = 0;
			     int raffinato = 0;
			     
			     Element attributi =  doc.getElementsByClass("tdb-attributi").get(0);
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
			    	 } else if(tag.equalsIgnoreCase("Centri benessere")) {
			    		 benessere = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Dove bere")) {
			    		 bere = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Dove mangiare")) {
			    		 mangiare = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Dove dormire")) {
			    		 dormire = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Golosità")) {
			    		 golosita = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Librerie e Salotti letterali")) {
			    		 librerie = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Luoghi romantici")) {
			    		 romantico = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Musei, gallerie d'arte e luoghi interessanti")) {
			    		 museo = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Spiagge e lidi")) {
			    		 spiagge = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Teatri e auditorium")) {
			    		 teatro = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Raffinato")) {
			    		 raffinato = 1;
			    	 }
			    	 else if(tag.equalsIgnoreCase("Informale")) {
			    		 informale = 1;
			    	 } 
			    	 
			     }
			     String via = "";
			     String comune = "";
			     try {
			    	 comune = doc.getElementsByClass("tdb-locale-citta").get(0).childNode(0).outerHtml().split("\\(")[0].trim();
			    	 via = doc.getElementsByClass("tdb-locale-via").get(0).childNode(0).outerHtml()+" , "+doc.getElementsByClass("tdb-locale-citta").get(0).childNode(0).outerHtml();
			     }catch(Exception e) {}
			     
			     
			     String latitudine ="";
			     String longitudine = "";
			     String latlong ="";
			     try {
			    	 latlong = doc.getElementsByClass("tdb-pulsante-piccolo").get(0).childNode(0).outerHtml();
			    	 if(latlong.contains("GPS")){
			    		 String[] pieces  = latlong.split(" N ");
			    		 latitudine = pieces[0].replace(" GPS ", "");
			    		 longitudine = pieces[1].replace(" E", "");
			    	 }
			    	
			     }catch(Exception e) {}
			     
			     String chiusura ="";
			     String telefono = "";
			     String sitoWeb = "";
			     
			     try {
			    	 Elements info = doc.getElementsByClass("tdb-locale-dati");
			    	 
			    	 for(int z = 0; z < info.size(); z++) {
			    		 Element e = info.get(z);
			    		 if(e.parent().outerHtml().contains("Giorno di chiusura")) {
			    			 chiusura = e.childNode(0).outerHtml();
			    		 }
			    		 if(e.parent().outerHtml().contains("Telefono")) {
			    			 telefono = e.childNode(0).outerHtml();
			    		 }
			    		 if(e.parent().outerHtml().contains("Sito web")) {
			    			 sitoWeb = e.childNode(1).childNode(0).outerHtml();
			    		 }
			    	 }
			     }catch(Exception e) {}
			    			     
			     String description = "";
			     try {
			    	 description = doc.select("body > div:nth-child(5) > div > div.tdb-left-container > p").outerHtml();
			    	 if(description.equalsIgnoreCase("<p></p>")) {
			    		 description ="";
			    	 }
			     }catch(Exception e) {}
			     
			     double rating = -1;

			     String link2 ="https://www.google.com/search?q="+URLEncoder.encode(titolo, "UTF-8")+"+"+URLEncoder.encode(comune, "UTF-8");
			     URL url2 = new URL(link2);
			 	
				 URLConnection conn2 = url2.openConnection();
				 conn2.setRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
				 conn2.setRequestProperty("Accept","text/html");
				 conn2.setRequestProperty("Connection","keep-alive");
		
				 conn2.connect();
				 InputStream stream2 = conn2.getInputStream();
				 InputStreamReader reader2 = new InputStreamReader(stream2);
				 BufferedReader in2 = new BufferedReader(reader2);
				 String inputLine2;
				 String html2 ="";
			        while ((inputLine2 = in2.readLine()) != null) 
			        	html2=html2+" "+inputLine2;
			        
			     Document doc2 = Jsoup.parse(html2);   
			     
			     String rat = doc2.select("#rhs_block > div > div.kp-blk.knowledge-panel.Wnoohf.OJXvsb > div > div.ifM9O > div:nth-child(2) > div.kp-header > div > div.fYOrjf.kp-hc > div:nth-child(2) > div > div > span.rtng").html();
			     try {
			     rating = Double.parseDouble(rat.replace(",","."));
			     }catch(Exception e) {e.printStackTrace();}
			     
			     
		    	String check = "SELECT * from luoghi where link =? AND nomeposto = ?";
		    	PreparedStatement st1 = connDb.prepareStatement(check);
			    st1.setString(1, link);
			    st1.setString(2, titolo);
			    ResultSet rs = st1.executeQuery();
		    		 
			    	 if(!rs.next()){
				    	 String q = "INSERT INTO luoghi(link,ext_id,nomeposto,tipo,comune,indirizzo,latitudine,longitudine,telefono,sitoweb,chiusura,rating,informale,raffinato,free_entry,"
				    	 		+ "benessere,bere,mangiare,dormire,goloso,libri,romantico,museo,spiaggia,teatro,arte,avventura,cinema,cittadinanza,musica_classica,geek,bambini,folklore,cultura,jazz,concerti,vita_notturna,html) VALUES ("
				    	 		+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				    	 PreparedStatement st = connDb.prepareStatement(q);
				    	 st.setString(1, link);
				    	 st.setInt(2, k);
				    	 st.setString(3, titolo);
				    	 st.setString(4, tipo);
				    	 st.setString(5, comune);
				    	 st.setString(6, via);
				    	 st.setString(7, latitudine);
				    	 st.setString(8, longitudine);
				    	 st.setString(9, telefono);
				    	 st.setString(10, sitoWeb);
				    	 st.setString(11, chiusura);
				    	 st.setDouble(12, rating);
				    	 st.setInt(13, informale);
				    	 st.setInt(14, raffinato);
				    	 st.setInt(15, free_entry);
				    	 st.setInt(16, benessere);
				    	 st.setInt(17, bere);
				    	 st.setInt(18, mangiare);
				    	 st.setInt(19, dormire);
				    	 st.setInt(20, golosita);
				    	 st.setInt(21, librerie);
				    	 st.setInt(22, romantico);
				    	 st.setInt(23, museo);
				    	 st.setInt(24, spiagge);
				    	 st.setInt(25, teatro);
				    	 st.setInt(26, arte);
				    	 st.setInt(27, avv);
				    	 st.setInt(28, cinema);
				    	 st.setInt(29, citt);
				    	 st.setInt(30, classica);
				    	 st.setInt(31, geek);
				    	 st.setInt(32, bambini);
				    	 st.setInt(33, folk);
				    	 st.setInt(34, cultura);
				    	 st.setInt(35, jazz);
				    	 st.setInt(36, concerti);
				    	 st.setInt(37, nott);
				    	 st.setString(38, description);
				    	 st.execute();
				    	 
				   

			    	 }
			    	 
			    	
				     Thread.sleep(5000);
		    	 
		     }
		     
	        
		}
		connDb.close();
	}
	
}
