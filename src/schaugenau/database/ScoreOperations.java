package schaugenau.database;

import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * @author Kevin Schmidt
 *
 */

public class ScoreOperations {

	/** defines **/

	/* database access */
	protected Access dbaccess;

	/* query String */
	protected String queryString;

	/* logging */
	protected Logger logger = Logger.getLogger(ScoreOperations.class);

	/* table for Scores */
	protected String table;

	/* constructor */
	public ScoreOperations() {
		dbaccess = new Access();
		table = "scores";
	}

	/*
	 * Method to save new score in database of Gamestyle "Gamestyle".
	 */
	public void saveScore(String gamestyle, int score, String name, int playedTime, int maxMultiplicator,
			double avMultiplicator, int correctPictures, int incorrectPictures) {
		try {
			dbaccess.createConnection();
			queryString = "INSERT INTO " + table + " SET" + " Score = '" + score + "'," + " Name = '" + name + "',"
					+ " CreationTime = NOW()" + "" + "," + " Gamestyle = '" + gamestyle + "'," + " PlayedTime = '"
					+ playedTime + "'," + " MaxMultiplicator = '" + maxMultiplicator + "'," + " AvMultiplicator = '"
					+ avMultiplicator + "'," + " CorrectPictures = '" + correctPictures + "',"
					+ " IncorrectPictures = '" + incorrectPictures + "'";
			dbaccess.st.executeUpdate(queryString);
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.saveScore() collapsed!");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Methods for the loading ALL-TIME best Scores
	 * 
	 */

	/*
	 * Method to load the highest -count- Scores into an int[] of Gamestyle
	 * "Gamestyle".
	 */
	public int[] loadBestScores(int count) {
		int[] scores = new int[count];
		try {
			dbaccess.createConnection();
			queryString = "SELECT Score FROM " + table + " ORDER BY Score DESC , Pkey DESC LIMIT " + count + "";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			for (int i = 1; i < count + 1; i++) {
				dbaccess.rs.next();
				scores[i - 1] = dbaccess.rs.getInt("Score");
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.loadBestScores() collapsed!");
			e.printStackTrace();
		}
		return scores;
	}

	/*
	 * Method to load the highest -count- Scorers into an String[]
	 */
	public String[] loadBestScorers(int count) {
		String[] scorers = new String[count];
		try {
			dbaccess.createConnection();
			queryString = "SELECT Name FROM " + table + " ORDER BY Score DESC , Pkey DESC LIMIT " + count + "";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			for (int i = 1; i < count + 1; i++) {
				dbaccess.rs.next();
				scorers[i - 1] = dbaccess.rs.getString("Name");
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.loadBestScorers() collapsed!");
			e.printStackTrace();
		}
		return scorers;
	}

	/*
	 * Method to load the highest -count- Gamestyles into an String[]
	 */
	public String[] loadBestGamestyles(int count) {
		String[] gamestyles = new String[count];
		try {
			dbaccess.createConnection();
			queryString = "SELECT Gamestyle FROM " + table + " ORDER BY Score DESC , Pkey DESC LIMIT " + count + "";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			for (int i = 1; i < count + 1; i++) {
				dbaccess.rs.next();
				gamestyles[i - 1] = dbaccess.rs.getString("Gamestyle");
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.loadBestScorers() collapsed!");
			e.printStackTrace();
		}
		return gamestyles;
	}

	/*
	 * Method to load the highest -count- Scores of the last hours into an
	 * int[]".
	 */
	public int[] loadBestScoresOfHours(int count, int hours) {
		int[] scores = new int[count];
		try {
			dbaccess.createConnection();
			queryString = "SELECT Score FROM " + table + " WHERE CreationTime > DATE_SUB(NOW(), INTERVAL " + hours
					+ " HOUR) " + "OR CreationTime = 0000-00-00 ORDER BY Score DESC , Pkey DESC LIMIT " + count + "";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			for (int i = 1; i < count + 1; i++) {
				dbaccess.rs.next();
				scores[i - 1] = dbaccess.rs.getInt("Score");
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.loadBestScoresOfToday() collapsed!");
			e.printStackTrace();
		}
		return scores;
	}

	/*
	 * Method to load the highest -count- Scorers of the last hours into an
	 * String[]
	 */
	public String[] loadBestScorersOfHours(int count, int hours) {
		String[] scorers = new String[count];
		try {
			dbaccess.createConnection();
			queryString = "SELECT Name FROM " + table + " WHERE CreationTime > DATE_SUB(NOW(), INTERVAL " + hours
					+ " HOUR) " + "OR CreationTime = 0000-00-00 ORDER BY Score DESC , Pkey DESC LIMIT " + count + "";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			for (int i = 1; i < count + 1; i++) {
				dbaccess.rs.next();
				scorers[i - 1] = dbaccess.rs.getString("Name");
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.loadBestScorers() collapsed!");
			e.printStackTrace();
		}
		return scorers;
	}

	/*
	 * Method to load the highest -count- Gamestyles of the last hours into an
	 * String[]
	 */
	public String[] loadBestGamestylesOfHours(int count, int hours) {
		String[] gamestyles = new String[count];
		try {
			dbaccess.createConnection();
			queryString = "SELECT Gamestyle FROM " + table + " WHERE CreationTime > DATE_SUB(NOW(), INTERVAL " + hours
					+ " HOUR) " + "OR CreationTime = 0000-00-00 ORDER BY Score DESC , Pkey DESC LIMIT " + count + "";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			for (int i = 1; i < count + 1; i++) {
				dbaccess.rs.next();
				gamestyles[i - 1] = dbaccess.rs.getString("Gamestyle");
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.loadBestScorers() collapsed!");
			e.printStackTrace();
		}
		return gamestyles;
	}

	/*
	 * Method to query the highscore rank of a player
	 * 
	 * @param: score: score of a player
	 * 
	 * @param: gamestyle: chosen gamestyle (A, B or C), null if all gamestyles
	 */
	public int queryHighscoreRank(int score, String gamestyle) {
		int rank = 1;
		try {
			dbaccess.createConnection();

			if (gamestyle == null) {
				queryString = "SELECT Score FROM " + table + " WHERE Score >= " + score + " "
						+ "ORDER BY Score DESC , Pkey DESC";
			} else {
				queryString = "SELECT Score, Gamestyle FROM " + table + " WHERE Score >= " + score + " "
						+ "AND Gamestyle = '" + gamestyle + "' " + "ORDER BY Score DESC , Pkey DESC";
			}

			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			dbaccess.rs.next();
			while (score < dbaccess.rs.getInt("Score")) {
				rank++;
				dbaccess.rs.next();
			}
			dbaccess.con.close();
		} catch (SQLException | ClassNotFoundException e) {
			logger.fatal("ScoreOperations.queryHighscoreRank() collapsed!");
			e.printStackTrace();
		}
		return rank;
	}

	public int queryHighscoreRankOfHours(int score, String gamestyle, int hours) {
		int rank = 1;
		try {
			dbaccess.createConnection();

			if (gamestyle == null) {
				queryString = "SELECT Score FROM " + table + " WHERE Score >= " + score + " "
						+ "AND CreationTime >DATE_SUB(Now(), INTERVAL " + hours + " HOUR) "
						+ "OR CreationTime = 0000-00-00 " + "ORDER BY Score DESC , Pkey DESC";
			} else {
				queryString = "SELECT Score, Gamestyle FROM " + table + " WHERE Score >= " + score + " "
						+ "AND Gamestyle = '" + gamestyle + "' " + "AND CreationTime >DATE_SUB(Now(), INTERVAL " + hours
						+ " HOUR) " + "OR CreationTime = 0000-00-00 " + "ORDER BY Score DESC , Pkey DESC";
			}

			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			dbaccess.rs.next();
			while (score < dbaccess.rs.getInt("Score")) {
				rank++;
				dbaccess.rs.next();
			}
			dbaccess.con.close();
		} catch (SQLException | ClassNotFoundException e) {
			logger.fatal("ScoreOperations.queryHighscoreRank() collapsed!");
			e.printStackTrace();
		}
		return rank;
	}

	/*
	 * helper Method to get the highest Pkey of Scores
	 */
	public int getMaxPkey() {
		int maxID = 1;
		try {
			dbaccess.createConnection();
			queryString = "SELECT Pkey from " + table + " ORDER BY Pkey DESC LIMIT 1;";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			dbaccess.rs.next();
			maxID = dbaccess.rs.getInt("Pkey");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return maxID;
	}
}
