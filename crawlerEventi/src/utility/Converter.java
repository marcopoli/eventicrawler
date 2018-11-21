package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

public class Converter {

	public static void eventiTovec() throws SQLException {
		
		Connection con0 = null;
		try {
			Class.forName("org.postgresql.Driver");
			if (con0 == null) {
				con0 = DriverManager.getConnection("jdbc:postgresql://127.0.0.1/PugliaEventi?characterEncoding=utf8",
						"postgres", "postgres");
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Error loading class!");
			cnfe.printStackTrace();
		}
		
		String q1 = "SELECT * from eventi";
		Statement st = con0.createStatement();
		ResultSet rs0 = st.executeQuery(q1);
		
		//DELETE ALL vec events
		String q2 = "DELETE from eventi_vec WHERE true";
		Statement st2 = con0.createStatement();
		st2.execute(q2);
		
		//Per ogni evento
		while(rs0.next()) {
			String link = rs0.getString("link");
			int popolarita = rs0.getInt("popolarita");
			Date da = rs0.getDate("data_da");
			Date a = rs0.getDate("data_a");
			int lun = 0;
			int mart = 0;
			int merc = 0;
			int giov = 0;
			int ven = 0;
			int sab = 0;
			int dom = 0;
			int num_days = 0;
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
			String stagione =  seasons[ a.getMonth() ];
			
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

			int free = rs0.getInt("free_entry");
			int arte = rs0.getInt("arte");
			int avventura = rs0.getInt("avventura");
			int cinema = rs0.getInt("cinema");
			int cittadinanza = rs0.getInt("cittadinanza");
			int classica = rs0.getInt("musica_classica");
			int geek = rs0.getInt("geek");
			int bambini = rs0.getInt("bambini");
			int folklore = rs0.getInt("folklore");
			int cultura = rs0.getInt("cultura");
			int jazz = rs0.getInt("jazz");
			int concerti = rs0.getInt("concerti");
			int teatro = rs0.getInt("teatro");
			int vita_notturna = rs0.getInt("vita_notturna");
			int featured = rs0.getInt("featured");
			
			Calendar start = Calendar.getInstance();
			start.setTime(da);
			Calendar end = Calendar.getInstance();
			end.setTime(a);
			
			for (Date dat = start.getTime(); start.before(end) || start.equals(end); start.add(Calendar.DATE, 1), dat = start.getTime()) {
				if(dat.getDay() == 0) {
					dom = 1;
				}
				if(dat.getDay() == 1) {
					lun = 1;
				}
				if(dat.getDay() == 2) {
					mart = 1;
				}
				if(dat.getDay() == 3) {
					merc = 1;
				}
				if(dat.getDay() == 4) {
					giov = 1;
				}
				if(dat.getDay() == 5) {
					ven = 1;
				}
				if(dat.getDay() == 6) {
					sab = 1;
				}
				
				 num_days++;
			}
			
			String meteo = "select bit_or(sereno) as ser, bit_or(coperto) as cop, bit_or(poco_nuvoloso) as nuv, bit_or(pioggia) as piog, bit_or(temporale) as temp, bit_or(neve) as nev,  bit_or(nebbia) as neb, avg(temperatura) as tem, avg(velocita_vento) as ven FROM \n" + 
					"((SELECT link,sereno,coperto,poco_nuvoloso,pioggia,temporale,neve,nebbia,temperatura,velocita_vento FROM meteo where link = '"+link+"') UNION (SELECT link,sereno,coperto,poco_nuvoloso,pioggia,temporale,neve,nebbia,temperatura,velocita_vento FROM previsioni where link = '"+link+"') ) as metprev";
			Statement s = con0.createStatement();
			ResultSet rss = s.executeQuery(meteo);
			rss.next();
			
			int sereno = rss.getInt("ser");
			int coperto = rss.getInt("cop");
			int nuvoloso = rss.getInt("nuv");
			int pioggia = rss.getInt("piog");
			int temporale = rss.getInt("temp");
			int neve = rss.getInt("nev");
			int nebbia = rss.getInt("neb");
			double temperatura = rss.getDouble("tem");
			double vento = rss.getDouble("tem");
			
			int e_rating = 0;
			int e_informale = 0;
			int e_raffinato = 0;
			int e_free_entry = 0;
			int e_benessere = 0;
			int e_bere = 0;
			int e_mangiare = 0;
			int e_dormire = 0;
			int e_goloso = 0;
			int e_libri = 0;
			int e_romantico = 0;
			int e_museo = 0;
			int e_spiaggia = 0;
			int e_teatro = 0;
			int e_arte = 0;
			int e_avventura = 0;
			int e_cinema = 0;
			int e_cittadinanza = 0;
			int e_musica_classica = 0;
			int e_geek = 0;
			int e_bambini = 0;
			int e_folklore = 0;
			int e_cultura = 0;
			int e_jazz = 0;
			int e_concerti = 0;
			int e_vita_notturna = 0;
			
			String postoL = "";
			postoL = rs0.getString("posto_link");
			if(!postoL.equalsIgnoreCase("")) {
				String qpl = "SELECT * from luoghi where link ='"+rs0.getString("posto_link")+"'";
				Statement sp = con0.createStatement();
				ResultSet rsp = sp.executeQuery(qpl);
				
				if(rsp.next()) {
					e_rating = rsp.getInt("rating");;
					e_informale = rsp.getInt("informale");
					e_raffinato = rsp.getInt("raffinato");
					e_free_entry = rsp.getInt("free_entry");
					e_benessere = rsp.getInt("benessere");
					e_bere = rsp.getInt("bere");
					e_mangiare = rsp.getInt("mangiare");
					e_dormire = rsp.getInt("dormire");
					e_goloso = rsp.getInt("goloso");
					e_libri = rsp.getInt("libri");
					e_romantico = rsp.getInt("romantico");
					e_museo = rsp.getInt("museo");
					e_spiaggia = rsp.getInt("spiaggia");
					e_teatro = rsp.getInt("teatro");
					e_arte = rsp.getInt("arte");
					e_avventura = rsp.getInt("avventura");
					e_cinema = rsp.getInt("cinema");
					e_cittadinanza = rsp.getInt("cittadinanza");
					e_musica_classica = rsp.getInt("musica_classica");
					e_geek = rsp.getInt("geek");
					e_bambini = rsp.getInt("bambini");
					e_folklore = rsp.getInt("folklore");
					e_cultura = rsp.getInt("cultura");
					e_jazz = rsp.getInt("jazz");
					e_concerti = rsp.getInt("concerti");
					e_vita_notturna = rsp.getInt("vita_notturna");
				}
			}
			
			//Extract w2v
			String qw = "SELECT * from eventi_w2v WHERE link = '"+link+"'";
			Statement sq = con0.createStatement();
			ResultSet rsw = sq.executeQuery(qw);
			
			if(rsw.next()) {
				
				//Insert into db
				
				String insert = "INSERT INTO eventi_vec VALUES ("+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				PreparedStatement ins = con0.prepareStatement(insert);
				ins.setString(1, link);
				ins.setInt(2, popolarita);
				ins.setInt(3, lun);
				ins.setInt(4, mart);
				ins.setInt(5, merc);
				ins.setInt(6, giov);
				ins.setInt(7, ven);
				ins.setInt(8, sab);
				ins.setInt(9, dom);
				ins.setInt(10, num_days);
				ins.setInt(11, primavera);
				ins.setInt(12, estate);
				ins.setInt(13, autunno);
				ins.setInt(14, inverno);
				ins.setInt(15,free);
				ins.setInt(16,arte);
				ins.setInt(17,avventura);
				ins.setInt(18,cinema);
				ins.setInt(19,cittadinanza);
				ins.setInt(20,classica);
				ins.setInt(21,geek);
				ins.setInt(22,bambini);
				ins.setInt(23,folklore);
				ins.setInt(24,cultura);
				ins.setInt(25,jazz);
				ins.setInt(26,concerti);
				ins.setInt(27,teatro);
				ins.setInt(28,vita_notturna);
				ins.setInt(29,featured);
				ins.setInt(30,sereno);
				ins.setInt(31,coperto);
				ins.setInt(32,nuvoloso);
				ins.setInt(33,pioggia);
				ins.setInt(34,temporale);
				ins.setInt(35,neve);
				ins.setInt(36,nebbia);
				ins.setDouble(37,temperatura);
				ins.setDouble(38,vento);
				ins.setInt(39,e_rating);
				ins.setInt(40,e_informale);
				ins.setInt(41,e_raffinato);
				ins.setInt(42,e_free_entry);
				ins.setInt(43,e_benessere);
				ins.setInt(44,e_bere);
				ins.setInt(45,e_mangiare);
				ins.setInt(46,e_dormire);
				ins.setInt(47,e_goloso);
				ins.setInt(48,e_libri);
				ins.setInt(49,e_romantico);
				ins.setInt(50,e_museo);
				ins.setInt(51,e_spiaggia);
				ins.setInt(52,e_teatro);
				ins.setInt(53,e_arte);
				ins.setInt(54,e_avventura);
				ins.setInt(55,e_cinema);
				ins.setInt(56,e_cittadinanza);
				ins.setInt(57,e_musica_classica);
				ins.setInt(58,e_geek);
				ins.setInt(59,e_bambini);
				ins.setInt(60,e_folklore);
				ins.setInt(61,e_cultura);
				ins.setInt(62,e_jazz);
				ins.setInt(63,e_concerti);
				ins.setInt(64,e_vita_notturna);
				
				for(int k = 0; k < 500; k++) {
					ins.setDouble(65+k, rsw.getDouble((k+2)));
				}
				//System.out.println(ins.toString());
				ins.execute(); 
			}
			
			
		}
		
		
		
	}
	
	
}
