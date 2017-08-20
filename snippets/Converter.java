package app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Converter {

	public static void main(String[] args) {

		// Read file (could be given via command line)
		try {
			BufferedReader reader = new BufferedReader(new FileReader(("data/Image0054.2015-07.txt")));

			// Get first line of data block
			String firstLine = reader.readLine();
			int user = 1;
			while (firstLine != null) {

				// BLOCK

				// Get all the meta data
				String date = firstLine;
				String display = reader.readLine();
				String coverage = reader.readLine();
				String duration = reader.readLine();
				String displayedAs = reader.readLine();
				String chosen = reader.readLine();
				String tutorial = reader.readLine();

				// Prepare variables for extracted data
				List<Integer> fixationX = new LinkedList<Integer>();
				List<Integer> fixationY = new LinkedList<Integer>();
				List<Integer> gazeDuration = new LinkedList<Integer>(); // Milliseconds
				List<Integer> recordingTimestamp = new LinkedList<Integer>(); // Milliseconds

				// Current fixation
				int currentFixationStartX = 0, currentFixationStartY = 0;
				float currentFixationX = 0, currentFixationY = 0;
				float currentFixationStartTime = 0, currentFixationTime = 0, lastFixationTime = 0;
				boolean initialized = false;
				int counter = 0;
				float recordingTimestampOffset = 0;

				// Some values for controlling the algorithm
				int maxCoordinateOffset = 30;
				float maxTimeOffset = 10;

				// Get the data
				String dataLine = reader.readLine();
				while (!dataLine.equals("")) {

					// DATA LINE

					// Extract data in line
					Scanner data = new Scanner(dataLine);
					data.useLocale(Locale.US);
					data.useDelimiter("; ");

					// Raw data extraction
					int x = data.nextInt();
					int y = data.nextInt();
					float time = data.nextFloat();

					// New gaze
					if (time == 0) {
						recordingTimestampOffset += lastFixationTime;
					}

					// New fixation
					if (!initialized) {
						currentFixationStartX = x;
						currentFixationStartY = y;
						currentFixationStartTime = time;
						currentFixationX = x;
						currentFixationY = y;
						currentFixationTime = time;
						counter = 1;
						initialized = true;
					} else {

						// Determine whether it is still the same fixation
						if (Math.abs(currentFixationStartX - x) < maxCoordinateOffset
								&& Math.abs(currentFixationStartY - y) < maxCoordinateOffset
								&& (currentFixationTime - lastFixationTime) < maxTimeOffset
								&& currentFixationTime != 0) {

							// Same fixation
							currentFixationX += x;
							currentFixationY += y;
							currentFixationTime = time;
							counter++;

						} else {

							// Other fixation, so save extracted data
							float currentGazeDuration = currentFixationTime - currentFixationStartTime;
							if (currentGazeDuration > 0) {
								fixationX.add((int) (currentFixationX / counter));
								fixationY.add((int) (currentFixationY / counter));
								gazeDuration.add((int) (1000 * (currentGazeDuration)));
								recordingTimestamp.add((int) (1000 * (currentFixationTime + recordingTimestampOffset))); // No
								// idea
								// whether
								// this
								// is
								// correct
							}

							// Prepare for next fixation
							currentFixationStartX = x;
							currentFixationStartY = y;
							currentFixationStartTime = time;
							currentFixationX = x;
							currentFixationY = y;
							currentFixationTime = time;
							counter = 1;
						}
					}

					// Save current time
					lastFixationTime = currentFixationTime;

					// Close data scanner
					data.close();

					// Next line
					dataLine = reader.readLine();

					// Save last fixation too
					if (dataLine.equals("")) {
						float currentGazeDuration = currentFixationTime - currentFixationStartTime;
						if (currentGazeDuration > 0) {
							fixationX.add((int) (currentFixationX / counter));
							fixationY.add((int) (currentFixationY / counter));
							gazeDuration.add((int) (1000 * (currentGazeDuration)));
							recordingTimestamp.add((int) (1000 * (currentFixationTime + recordingTimestampOffset))); // No
							// idea
							// whether
							// this
							// is
							// correct
						}
					}
				}

				// Check, whether there is gaze data
				if (fixationX.size() > 0) {

					// Save to CSV
					FileWriter writer = new FileWriter("processed/Image0054_" + user + ".csv");

					// Write header
					writer.write(
							"MediaName,RecordingTimestamp,FixationIndex,GazeEventDuration,FixationPointX (MCSpx),FixationPointY (MCSpx)\n");
					for (int i = 0; i < fixationX.size(); i++) {
						// Media name
						writer.write("Box,");

						// Recording time stamp
						writer.write("" + recordingTimestamp.get(i) + ",");

						// Fixation index
						writer.write("" + (i + 1) + ",");

						// Gaze duration
						writer.write("" + gazeDuration.get(i) + ",");

						// Fixation x
						writer.write("" + fixationX.get(i) + ",");

						// Fixation y
						writer.write("" + fixationY.get(i) + "\n");
					}

					writer.close();

					// Next user
					user++;
				}

				// First line of next data block
				firstLine = reader.readLine();
			}

			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Data to extract

	}

}
