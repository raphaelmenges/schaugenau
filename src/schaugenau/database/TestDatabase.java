package schaugenau.database;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * @author Kevin Schmidt
 *
 */

public class TestDatabase {

	/*
	 * Main class for testing issues
	 * 
	 * Eventually throws declaration needed (and import)
	 * 
	 * All Methods tested regarding SQL regularity
	 * 
	 */

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		/**
		 * Testing PictureOperations:
		 */

		// PictureOperations.updatePicture(1, 2000);

		// Stack<String> tempstack = ImageCrawler.getImageStack();
		// while(!tempstack.isEmpty()){
		// PictureOperations.importPicture(1);
		// tempstack.pop();
		// }
		// PictureOperations.incrementChosenAsCorrect(1);
		// PictureOperations.incrementUsedAsCorrect(1);

		// PictureOperations pictureOperations;
		// pictureOperations = new PictureOperations();
		// pictureOperations.importPicture();

		// System.out.println(Functions.fetchID("Baum"));
		// PictureOperations.loadPicture();
		// System.out.println(PictureOperations.getMaxID());
		// PictureOperations.getTagPool();
		// PictureOperations.getRdmTag();
		//
		// PictureOperations.loadPictures();

		/**
		 * Testing ScoreOperations:
		 */

		// ScoreOperations scoreOperations;
		// scoreOperations = new ScoreOperations();

		// NOTE: Set CreationTime to 0000-00-00 for creating universal
		// "empties"
		// for (int i = 0; i < 10; i++) {
		// scoreOperations.saveScore("C", 0, "empty", 0, 0, 0, 0, 0);
		// }

		// int[] test = ScoreOperations.loadBestScores(10);
		// for (int i = 0; i < 10; i++) {
		// System.out.print(test[i] + ", ");
		// }

		// int[] test = ScoreOperations.loadBestScoresOfHours(30, 1);
		// for (int i = 0; i < 30; i++) {
		// System.out.print(test[i] + ", ");
		// }

		// int[] test3 = ScoreOperations.loadBestPkey(10);
		// for (int i = 0; i < 10; i++){
		// System.out.print(test3[i] + ", ");
		// }

		// String[] test3 = ScoreOperations.loadBestGamestyles(10);
		// for (int i = 0; i < 10; i++){
		// System.out.print(test3[i] + ", ");
		// }

		// String[] test2 = ScoreOperations.loadBestScorers(10);
		// for (int i = 0; i < 10; i++) {
		// System.out.print(test2[i] + ", ");
		// }

		// String[] test2 = new String[20];
		// test2 = ScoreOperations.loadNewestScorers(20);
		// for (int i = 0; i < 20; i++){
		// System.out.print(test2[i] + ", ");
		// }

		// ScoreOperations.loadAvatars();

		// CSV_ScoreOperations.saveScore(222, "fxv");
		// int[] test = new int[20];
		// String[] test2 = new String[20];
		// test = CSV_ScoreOperations.loadBestScores(20);
		// for (int i = 0; i < 20; i++){
		// System.out.print(test2[i] + " ");
		// System.out.println(test[i]);
		// };

		/*
		 * Testing SurveyOperations
		 */

		// SurveyOperations surveyOperations;
		// surveyOperations = new SurveyOperations();

		// surveyOperations.saveSurveyItem();

		// SessionID 0 not counted
		// System.out.println(surveyOperations.getSessionCount());

		// System.out
		// .println(surveyOperations.getSession(0, null, null).isEmpty());

		// surveyOperations.saveResult("A", 10, -1, -1);

	}
}
