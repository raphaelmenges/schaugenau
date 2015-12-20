package schaugenau.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import schaugenau.app.App;
import schaugenau.gui.GuiAdapter;
import schaugenau.state.survey.ChoiceItem;
import schaugenau.state.survey.DecisionItem;
import schaugenau.state.survey.LikertScalaItem;
import schaugenau.state.survey.SurveyItem;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * @author Kevin Schmidt
 *
 */

public class SurveyOperations {

	/* Select table */
	protected String table;

	/* querystring */
	private static String queryString;

	/* accessing database */
	private static Access dbaccess = null;

	/* logging */
	private static Logger logger = Logger.getLogger(SurveyOperations.class);

	/* constructor */
	public SurveyOperations() {
		dbaccess = new Access();
		table = "surveyitems";
	}

	/* language */
	private static String language;

	public String getLanguage() {
		return language;
	}

	public static void setLanguage(String lng) {
		language = lng;
	}

	/*
	 * Method to save new surveyItems in database
	 */
	public void saveSurveyItem() {
		try {
			logger.info("Storing surveyItem into database");
			PreparedStatement psmnt = null;
			dbaccess.createConnection();
			psmnt = dbaccess.con.prepareStatement("INSERT INTO " + table + "(Type, QuestionEN, QuestionDE, "
					+ "AnswerM1EN, AnswerM2EN, AnswerM3EN, " + "AnswerM1DE, AnswerM2DE, AnswerM3DE, "
					+ "AnswerLikertLeftEN, AnswerLikertRightEN, AnswerLikertLeftDE, "
					+ "AnswerLikertRightDE, SessionID, OrderPriority)" + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			psmnt.setString(1, "likert"); // Type
			psmnt.setString(2, "The game was entertaining. "); // QuestionEN
			psmnt.setString(3, "Ich fand das Spiel unterhaltsam. "); // QuestionDE
			psmnt.setString(4, ""); // AnswerM1EN
			psmnt.setString(5, ""); // AnswerM2EN
			psmnt.setString(6, ""); // AnswerM3EN
			psmnt.setString(7, ""); // AnswerM1DE
			psmnt.setString(8, ""); // AnswerM2DE
			psmnt.setString(9, ""); // AnswerM3DE
			psmnt.setString(10, "strongly disagree"); // AnswerLikertLeftEN
			psmnt.setString(11, "strongly agree"); // AnswerLikertRightEN
			psmnt.setString(12, "starke Ablehnung"); // AnswerLikertLeftDE
			psmnt.setString(13, "starke Zustimmung"); // AnswerLikertRightDE
			psmnt.setInt(14, 3); // SessionID
			psmnt.setInt(15, 8); // OrderPriority

			int s = psmnt.executeUpdate();
			if (s > 0) {
				System.out.println("Upload successfull");
			} else {
				System.out.println("Upload failed");
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("SurveyOperations.saveSurveyItem collapsed!");
			e.printStackTrace();
		}
	}

	/*
	 * Method to get the count of sessions
	 */
	public int getSessionCount() throws SQLException, ClassNotFoundException {
		int sessionCount;
		dbaccess.createConnection();
		queryString = "SELECT SessionID from " + table + " ORDER BY SessionID DESC LIMIT 1;";
		dbaccess.rs = dbaccess.st.executeQuery(queryString);
		dbaccess.rs.next();
		sessionCount = dbaccess.rs.getInt("SessionID");
		return sessionCount;
	}

	/*
	 * Method to load one Session from the database
	 */
	public List<SurveyItem> getSession(int session, App app, GuiAdapter guiAdapter) {
		List<SurveyItem> resultList = new LinkedList<SurveyItem>();
		List<String> answers = new LinkedList<String>();
		LikertScalaItem likertScalaItem = null;
		ChoiceItem choiceItem = null;
		DecisionItem decisionItem = null;

		try {
			dbaccess.createConnection();
			queryString = "SELECT Type, QuestionEN, QuestionDE, " + "AnswerM1EN, AnswerM2EN, AnswerM3EN, "
					+ "AnswerM1DE, AnswerM2DE, AnswerM3DE, "
					+ "AnswerLikertLeftEN, AnswerLikertRightEN, AnswerLikertLeftDE, "
					+ "AnswerLikertRightDE, SessionID, OrderPriority, Pkey FROM " + table + " WHERE SessionID = '"
					+ session + "' ORDER BY OrderPriority ASC";
			PreparedStatement psmnt = dbaccess.con.prepareStatement(queryString);
			ResultSet rs = psmnt.executeQuery();
			while (rs.next()) {

				boolean likertScala = false;
				boolean choice = false;
				boolean decision = false;
				String question = null;
				String answerM1 = null;
				String answerM2 = null;
				String answerM3 = null;
				String answerLikertLeft = null;
				String answerLikertRight = null;

				String type = rs.getString(1);
				String QuestionEN = rs.getString(2);
				String QuestionDE = rs.getString(3);
				String AnswerM1EN = rs.getString(4);
				String AnswerM2EN = rs.getString(5);
				String AnswerM3EN = rs.getString(6);
				String AnswerM1DE = rs.getString(7);
				String AnswerM2DE = rs.getString(8);
				String AnswerM3DE = rs.getString(9);
				String AnswerLikertLeftEN = rs.getString(10);
				String AnswerLikertRightEN = rs.getString(11);
				String AnswerLikertLeftDE = rs.getString(12);
				String AnswerLikertRightDE = rs.getString(13);
				int ItemID = rs.getInt(16);

				if (getLanguage() == "english") {
					question = QuestionEN;
					answerM1 = AnswerM1EN;
					answerM2 = AnswerM2EN;
					answerM3 = AnswerM3EN;
					answerLikertLeft = AnswerLikertLeftEN;
					answerLikertRight = AnswerLikertRightEN;
				} else {
					question = QuestionDE;
					answerM1 = AnswerM1DE;
					answerM2 = AnswerM2DE;
					answerM3 = AnswerM3DE;
					answerLikertLeft = AnswerLikertLeftDE;
					answerLikertRight = AnswerLikertRightDE;
				}

				if (type.equals("likert")) {
					likertScala = true;
					likertScalaItem = new LikertScalaItem(app, guiAdapter, "Likert", question, answerLikertLeft,
							answerLikertRight, ItemID);
				} else if (type.equals("choice")) {
					choice = true;
					answers.clear();
					if (answerM1 != null) {
						answers.add(answerM1);
					}
					if (answerM2 != null) {
						answers.add(answerM2);
					}
					if (answerM3 != null) {
						answers.add(answerM3);
					}
					choiceItem = new ChoiceItem(app, guiAdapter, "Choice", question, answers, ItemID);
				} else if (type.equals("decision")) {
					decision = true;
					decisionItem = new DecisionItem(app, guiAdapter, "Decision", question, ItemID);
				}

				if (likertScala) {
					resultList.add(likertScalaItem);
				} else if (choice) {
					resultList.add(choiceItem);
				} else if (decision) {
					resultList.add(decisionItem);
				}
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	/*
	 * Method to save result in the database
	 */
	public void saveResult(String gamestyle, int score, int itemID, int choice, int participant) {
		try {
			dbaccess.createConnection();
			queryString = "INSERT INTO surveyresults SET" + " Gamestyle = '" + gamestyle + "'," + " Score = '" + score
					+ "'," + " ItemID = '" + itemID + "'," + " Choice = '" + choice + "'," + " Participant = '"
					+ participant + "'";
			dbaccess.st.executeUpdate(queryString);
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("ScoreOperations.saveScore() collapsed!");
			e.printStackTrace();
		}
	}
}
