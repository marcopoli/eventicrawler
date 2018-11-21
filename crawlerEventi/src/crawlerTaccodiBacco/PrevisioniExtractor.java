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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrevisioniExtractor {
	public static boolean error = false;

	public static void extract7days() throws Exception {
		Connection connDb = null;

		try {
			Class.forName("org.postgresql.Driver");
			if (connDb == null) {
				connDb = DriverManager.getConnection("jdbc:postgresql://127.0.0.1/PugliaEventi?characterEncoding=utf8",
						"postgres", "postgres");
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Error loading class!");
			cnfe.printStackTrace();
		}
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		String query = "SELECT * from eventi WHERE data_a >= '" + formatter.format(date)
				+ "' AND  data_da <= '" + formatter.format(date) + "' ";
		System.out.println(query);
		Statement st0 = connDb.createStatement();
		ResultSet rs0 = st0.executeQuery(query);
		
		
		String updateOld = "DELETE FROM previsioni WHERE true";
		Statement st = connDb.createStatement();
		st.execute(updateOld);
		

		// Check meteo passato

		while (rs0.next()) {
			
			
			String link = rs0.getString("link");
			

			Date da = rs0.getDate("data_da");
			Date a = rs0.getDate("data_a");

			Calendar start = Calendar.getInstance();
			Calendar start2 = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			end.setTime(a);

			String comune = rs0.getString("comune");
			ArrayList<Date> dateev = new ArrayList();
			String linkMeteo = "https://www.tempoitalia.it/meteo/" + comune.replace(" ", "-").replace("'", "-");
			
			if (DateUtils.isSameDay(start, end)) {
				dateev.add(a);				
			} else {
				Calendar start7p = start;
				start7p.add(Calendar.DATE,7);
				//Se fine evento e oggi sono diversi (non è singola data nè ultimo giorno) è nel centro. Previsioni per max 7 giorni, quindi si sceglie il min fra oggi+7 e fine evento
				Date endD = (Date) minDate(start7p.getTime(),end.getTime());
				Calendar d = Calendar.getInstance();
				d.setTime(endD);
				
				for (Date dat = start2.getTime(); start2.before(d); start2.add(Calendar.DATE,1), dat = start2.getTime()) {
					dateev.add(dat);
				}
			}
			getMeteoData(linkMeteo, link, rs0.getString("titolo"), dateev, connDb);
			
			String up = "UPDATE eventi SET previsioni_bool = 1 where link = ?";
	    	PreparedStatement st3 = connDb.prepareStatement(up);
	    	st3.setString(1, link);
	    	st3.execute();
			 
			Thread.sleep(5000);
		}
		connDb.close();
	}

		public static Comparable minDate(Comparable c1, Comparable c2) {
			if (c1.compareTo(c2) < 0)
				return c1;
			else
				return c2;
		}

		
	public static void getMeteoData(String linkMeteo, String linkEvento, String titolo, ArrayList<Date> dateev, Connection connDb) throws Exception  {
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
			     
			     //Prendo comunque tutti e 7 i giorni
			     ArrayList<Integer> giorni = new ArrayList();
			     ArrayList<Double> temperature = new ArrayList();
			     ArrayList<Integer> venti = new ArrayList();
			     //tags
			     ArrayList<Integer> sereni = new ArrayList();
			     ArrayList<Integer> coperti = new ArrayList();
			     ArrayList<Integer> poco_nuvolosi = new ArrayList();
			     ArrayList<Integer> piogge = new ArrayList();
			     ArrayList<Integer> temporali = new ArrayList();
			     ArrayList<Integer> nebbie = new ArrayList();
			     ArrayList<Integer> nevi = new ArrayList();
			     
			     for(int i = 1 ; i < 8; i ++) {
			    	 //Giorni
			    	 int giorno = Integer.parseInt(doc.select("#tableHours > div.content > table > tbody > tr:nth-child("+i+") > td.timeweek > a").html().split(" ")[1]);
			    	 giorni.add(giorno);
			    	 
			    	 //Temperatura
			    	 double tempMax = Double.parseDouble(doc.select("#tableHours > div.content > table > tbody > tr:nth-child("+i+") > td.tempmax").html().replace("°C", ""));
			    	 double tempMin = Double.parseDouble(doc.select("#tableHours > div.content > table > tbody > tr:nth-child("+i+") > td.tempmin").html().replace("°C", ""));		 
			    	 double temMed = (tempMax+tempMin)/2;
			    	 temperature.add(temMed);
			    	 
			    	 //Vento
			    	 int vento = Integer.parseInt(doc.select("#tableHours > div.content > table > tbody > tr:nth-child("+i+") > td.wind > span.speed").html().replace(" Km/h", ""));
			    	 venti.add(vento);
			    	 
			    	 //Tags
			    	 String dec = doc.select("#tableHours > div.content > table > tbody > tr:nth-child("+i+") > td.skyDesc").html();
			    	 
			    	 if(dec.toLowerCase().contains("sereno")) {
			    		 sereni.add(1);
			    	 }else {sereni.add(0);}
			    	 
			    	 if(dec.toLowerCase().contains("coperto")) {
			    		 coperti.add(1);
			    	 }else {coperti.add(0);}
			    	 
			    	 if(dec.toLowerCase().contains("nuvoloso") || dec.toLowerCase().contains("nubi") ) {
			    		 poco_nuvolosi.add(1);
			    	 }else {poco_nuvolosi.add(0);}
			    	 
			    	 if(dec.toLowerCase().contains("pioggia") || dec.toLowerCase().contains("acquazzoni") || dec.toLowerCase().contains("piovaschi") || dec.toLowerCase().contains("pioviggine") || dec.toLowerCase().contains("rovesci")) {
			    		 piogge.add(1);
			    	 }else {piogge.add(0);}

			    	 if(dec.toLowerCase().contains("temporali")) {
			    		 temporali.add(1);
			    	 }else {temporali.add(0);}
			    	 
			    	 if(dec.toLowerCase().contains("neve")) {
			    		 nevi.add(1);
			    	 }else {nevi.add(0);}
			    	 
			    	 if(dec.toLowerCase().contains("nebbia")) {
			    		 nebbie.add(1);
			    	 }else {nebbie.add(0);}
			    	
			     }
			     
			     for(int i = 0; i < dateev.size(); i++) {
			     Date dat = dateev.get(i);
			     int giorno = dat.getDate();
			     int index = giorni.indexOf(giorno);
			        
			     
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
				String stagione =  seasons[ dat.getMonth() ];
				
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
		    	 String check = "SELECT * from previsioni where link =? AND titolo = ? AND dataevento = ?";
		    	 PreparedStatement st1 = connDb.prepareStatement(check);
		    	 st1.setString(1, linkEvento);
		    	 st1.setString(2, titolo);
		    	 st1.setDate(3, new java.sql.Date(dat.getTime()));
		    	 ResultSet rs = st1.executeQuery();
		    	
		    	 if(!rs.next()){
				     System.out.println(linkMeteo+" "+giorni.get(index));
		    		 String q = "INSERT INTO previsioni(link,titolo,dataevento,primavera,estate,autunno,inverno,sereno,coperto,poco_nuvoloso,pioggia,temporale,nebbia,neve,temperatura,velocita_vento) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    		 PreparedStatement st2 = connDb.prepareStatement(q);
			    	 st2.setString(1, linkEvento);
			    	 st2.setString(2, titolo);
			    	 st2.setDate(3, new java.sql.Date(dat.getTime()));
			    	 st2.setInt(4, primavera);
			    	 st2.setInt(5, estate);
			    	 st2.setInt(6, autunno);
			    	 st2.setInt(7, inverno);
			    	 st2.setInt(8, sereni.get(index));
			    	 st2.setInt(9, coperti.get(index));
			    	 st2.setInt(10, poco_nuvolosi.get(index));
			    	 st2.setInt(11, piogge.get(index));
			    	 st2.setInt(12, temporali.get(index));
			    	 st2.setInt(13, nebbie.get(index));
			    	 st2.setInt(14, nevi.get(index));
			    	 st2.setDouble(15, temperature.get(index));
			    	 st2.setInt(16, venti.get(index));
			    	 st2.execute();
		    	 }
				
			     }
			 }catch(Exception e) {error = true; e.printStackTrace();}
		}
		
		public static String getMonth(int month) {
		    return new DateFormatSymbols(Locale.ITALIAN).getMonths()[month];
		}
		
		

}
