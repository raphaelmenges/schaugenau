package schaugenau.database;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * @author Kevin Schmidt
 *
 */

public class CSV_ScoreOperations {

	static String[] bestScorers;

	public static void saveScore(int Score, String Name) {
		try {
			String csv = "logging/scores.csv";
			// CSVWriter writer = new CSVWriter(new FileWriter(csv));
			FileWriter fwriter = new FileWriter(csv, true);
			CSVWriter writer = new CSVWriter(fwriter);
			Date date = new Date();
			String score = "" + Name + "#" + Score + "#" + date + "";
			String[] write = score.split("#");
			writer.writeNext(write);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int[] loadBestScores(int count) {
		int[] scores = new int[count];
		String[] scorers = new String[count];
		String[] dates = new String[count];
		try {
			String csvFilename = "logging/scores.csv";
			CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
			String[] row = null;
			while ((row = csvReader.readNext()) != null) {
				int currentScore = Integer.parseInt(row[1]);
				String currentScorer = row[0];
				String currentDate = row[2];
				// scores = processCSV(currentScorer, currentScore, currentDate,
				// count);
				int smallestref = 0;
				int smallest = scores[0];
				for (int i = 0; i < count; i++) { // check for smallest score in
													// scores[]
					if (scores[i] < smallest) {
						smallest = scores[i];
						smallestref = i;
					}
					if (smallest < currentScore) { // replace smallest score
													// with a bigger one
						scorers[smallestref] = currentScorer;
						scores[smallestref] = currentScore;
						dates[smallestref] = currentDate;
					}
				}
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setScorers(scorers);
		return scores;
	}

	private static void setScorers(String[] scorers) {
		bestScorers = scorers;
	}

	public static String[] loadBestScorers(int count) {
		// not included into loadScores method for easy call-adaption
		return bestScorers;
	}

}
