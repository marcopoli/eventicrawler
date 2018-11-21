package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.jsoup.Jsoup;

public class Core {
	public static void eventsTow2v() throws ClassNotFoundException, SQLException {


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

		
		//prendo centroide spazio
		Vector<Double> fullcentr = new Vector<Double>();

		PreparedStatement pr0 = con0.prepareStatement("Select * from centr_w2v");
		ResultSet rs0 = pr0.executeQuery();
		if (rs0.next()) {
			for (int j = 1; j < 501; j++) {
				Double v = rs0.getDouble("f" + String.format("%03d", j));
				Double val = 0.0;

				try {
					val = fullcentr.get(j - 1);
				} catch (Exception e) {
				}

				double newVal = (val + v);

				try {
					fullcentr.remove(j - 1);

				} catch (Exception e) {
				}
				;

				fullcentr.add(j - 1, newVal);
				
			}
		}

		//prendo eventi da elaborare
		String selectEv = "SELECT link,descrizione from eventi where link not in (SELECT link from eventi_w2v)";
		Statement st00 = con0.createStatement();
		ResultSet rs00 = st00.executeQuery(selectEv);

		while (rs00.next()) {
			String link = rs00.getString("link");
			String desc = rs00.getString("descrizione");
			String line = Jsoup.parse(desc).text();

			Twokenize tw = new Twokenize(line);

			List<String> tokens = tw.tokenizeRawTweetText(line);
			Vector<Double> messages = new Vector<Double>();


			// Creo il vettore della stringa
			int count = 0;
			for (int k = 0; k < tokens.size(); k++) {

				if (tokens.get(k).length() > 2) {
					// System.out.println(tokens.get(i));
					try {
						PreparedStatement pr = con0.prepareStatement("Select * from w2v where w=?");
						pr.setString(1, tokens.get(k));

						ResultSet rs3 = pr.executeQuery();

						if (rs3.next()) {
							for (int j = 1; j < 501; j++) {
								Double v = rs3.getDouble("f" + String.format("%03d", j)) - fullcentr.get(j - 1);
								Double val = 0.0;

								try {
									val = messages.get(j - 1);
								} catch (Exception e) {
								}

								double newVal = (val + v);

								try {
									messages.remove(j - 1);

								} catch (Exception e) {
								}
								;

								messages.add(j - 1, newVal);
								// instanceValue[j-1] = newVal;
							}

							count++;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if (messages.size() != 0) {
				for (int j = 0; j < 500; j++) {
					Double val = messages.get(j);
					Double newVal = val / count;
					messages.remove(j);
					messages.add(j, newVal);
				}

			 //salviamo nel db
			String ins = "INSERT INTO eventi_w2v VALUES (";
			for(int k = 0; k < 500; k++) {
				ins = ins + "?,";
			}
			ins = ins + "?)";
			
			PreparedStatement stp = con0.prepareStatement(ins);
			stp.setString(1, link);
			for(int k = 2; k < 502; k++) {
				stp.setDouble(k, messages.get(k-2));
			}
			stp.execute();

			}
		}

	}

}
