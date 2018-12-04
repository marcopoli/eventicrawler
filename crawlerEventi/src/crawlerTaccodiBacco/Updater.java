package crawlerTaccodiBacco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Date;

import utility.Converter;
import utility.Core;

public class Updater {
	public static void main (String args[]) throws Exception {
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
		String query = "SELECT * from control";
		Statement st0 = connDb.createStatement();
		ResultSet rs0 = st0.executeQuery(query);
		rs0.next();
		Date last_up = rs0.getDate("last_update");

		//DEBUG_CODE
		System.out.println("Updater.java: last update found: " + last_up.toString());

		LinkExtractor.updateLinks(last_up);
		EventExtractor.eventExtract();

        //DEBUG_CODE
        System.out.println("Updater.java: processing w2v ...");

		Core.eventsTow2v();
		MeteoExtractor.extractPastMeteoData();//<- Sistemare i duplicati e cercare una nuova fonte. ???
		//PrevisioniExtractor.extract7days();
		//Converter.eventiTovec();

        //DEBUG_CODE
        System.out.println("Updater.java: w2v done");



		//Save new update date to DB
		String up = "UPDATE control set last_update = ?";
		LocalDateTime ldt = LocalDateTime.now();
		PreparedStatement pt = connDb.prepareStatement(up);
		pt.setDate(1, java.sql.Date.valueOf(ldt.toLocalDate()));
		pt.execute();
		connDb.close();

        //DEBUG_CODE
        System.out.println("Updater.java: db saved and closed");
		
	}
}
