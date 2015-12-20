package schaugenau.database;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * @author Kevin Schmidt
 *
 */

import java.io.IOException;
import java.sql.SQLException;

public class Maintenance {
	public static int CurrentID;
	public static String CurrentTag;
	private static Access dbaccess = null;
	private static String queryString;

	/* ScoreOperations */
	protected ScoreOperations scoreOperation;
	protected PictureOperations pictureOperation;

	public Maintenance() {
		/* database */
		scoreOperation = new ScoreOperations();
		pictureOperation = new PictureOperations();
	}

	// Sets all pictures TagCount back to 1
	public void resetTagCount() throws ClassNotFoundException, SQLException, IOException {
		dbaccess = new Access();
		dbaccess.createConnection();
		for (int i = 0; i < (pictureOperation.getMaxID()); i++) {
			queryString = "UPDATE pictures SET TagCount = 1 WHERE ID = '" + i + "'";
			dbaccess.st.executeUpdate(queryString);
		}
		dbaccess.con.close();
	}

	// Sets all pictures Views back to 0
	public void resetViews() throws ClassNotFoundException, SQLException, IOException {
		dbaccess = new Access();
		dbaccess.createConnection();
		for (int i = 0; i < (pictureOperation.getMaxID()); i++) {
			queryString = "UPDATE pictures SET Views = 0 WHERE ID = '" + i + "'";
			dbaccess.st.executeUpdate(queryString);
		}
		dbaccess.con.close();
	}

	// Deletes all Highscores that are not preset (= empty)
	public void resetHighscore() throws ClassNotFoundException, SQLException, IOException {
		String Gamestyle = "choooseone";
		String table = Gamestyle + "scores";
		dbaccess = new Access();
		dbaccess.createConnection();

		for (int i = 31; i < (scoreOperation.getMaxPkey()); i++) { // First
																	// 30
																	// scores
																	// are
																	// placeholder
			queryString = "DELETE FROM " + table + "WHERE Pkey = '" + i + "'";
			dbaccess.st.executeUpdate(queryString);
		}
		dbaccess.con.close();
	}

}
