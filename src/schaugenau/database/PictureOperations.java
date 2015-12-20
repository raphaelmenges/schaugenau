package schaugenau.database;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * @author Kevin Schmidt
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;
//import java.util.Stack;

import org.apache.log4j.Logger;

public class PictureOperations {

	/* Select table */
	protected String table;

	/* picture id's */
	private static int CurrentCorrectID;
	private static int CurrentIncorrectID;

	/* picture tags */
	private static String IncorPictTag;
	private static String CorPictTag;

	/* accessing database */
	private static Access dbaccess = null;

	/* querystring */
	private static String queryString;

	/* logging */
	private static Logger logger = Logger.getLogger(PictureOperations.class);

	/* Postfix for using International Tags */
	private static String language;

	/* constructor */
	public PictureOperations() {
		dbaccess = new Access();
		table = "pictures";
	}

	/** public accessors **/
	public int getCorPictID() {
		return CurrentCorrectID;
	}

	public void setCorPictID(int id) {
		CurrentCorrectID = id;
	}

	public int getIncorPictID() {
		return CurrentIncorrectID;
	}

	public void setIncorPictID(int id) {
		CurrentIncorrectID = id;
	}

	public static String getCorPictTag() {
		return CorPictTag;
	}

	public void setCorPictTag(String tag) {
		CorPictTag = tag;
	}

	public String getIncorPictTag() {
		return IncorPictTag;
	}

	public void setIncorPictTag(String tag) {
		IncorPictTag = tag;
	}

	public String getLanguage() {
		return language;
	}

	public static void setLanguage(String lng) {
		language = lng;
	}

	/*
	 * Method to load two different pictures from the database
	 */
	public void loadPictures() {
		try {
			dbaccess = new Access();
			dbaccess.createConnection();
			createRdmTags();

			// Picture with a matching tag
			String Tag = getCorPictTag();
			int ID = fetchID(Tag);
			logger.debug("Loading Picture with tag = " + Tag + " and filename = Correct" + ID + ".png");
			setCorPictID(ID);
			setCorPictTag(Tag);
			queryString = "SELECT Picture FROM " + table + " WHERE ID = '" + ID + "'";
			PreparedStatement psmnt = dbaccess.con.prepareStatement(queryString);
			ResultSet rs = psmnt.executeQuery();
			while (rs.next()) {
				File picture1 = new File("images/Correct" + ID + ".png");
				FileOutputStream fos = new FileOutputStream(picture1);
				byte[] buffer = new byte[1024];
				InputStream is = rs.getBinaryStream(1);
				while (is.read(buffer) > 0) {
					fos.write(buffer);
				}
				fos.close();
			}

			// Picture with not matching tag
			String Dif_Tag = getIncorPictTag();

			// This is not supposed to happen!
			// For debugging and errorhandling only
			if (Dif_Tag == getCorPictTag()) {
				Random generator = new Random();
				LinkedList<String> errPoollist = createTagPool();
				Object[] errArray = new String[errPoollist.size()];
				errArray = errPoollist.toArray();
				int randomIndex = generator.nextInt(errArray.length);
				errPoollist.remove(getCorPictTag());
				setIncorPictTag((String) errArray[randomIndex]);
				Dif_Tag = getIncorPictTag();
			}

			int Dif_ID = fetchID(Dif_Tag);
			setIncorPictID(Dif_ID);
			logger.debug("Loading Picture with tag = " + Dif_Tag + " and filename = Incorrect" + Dif_ID + ".png");
			queryString = "SELECT Picture FROM " + table + " WHERE ID = '" + Dif_ID + "'";
			PreparedStatement psmnt2 = dbaccess.con.prepareStatement(queryString);
			ResultSet rs2 = psmnt2.executeQuery();
			while (rs2.next()) {
				File picture2 = new File("images/Incorrect" + Dif_ID + ".png");
				FileOutputStream fos2 = new FileOutputStream(picture2);
				byte[] buffer2 = new byte[1024];
				InputStream is2 = rs2.getBinaryStream(1);
				while (is2.read(buffer2) > 0) {
					fos2.write(buffer2);
				}
				fos2.close();
			}
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			logger.fatal("PictureOperations.loadPicture() collapsed!");
		}
	}

	/**
	 * Methods to update values in the database
	 **/

	/*
	 * Saves how often the picture was loaded as the correct picture
	 */
	public void incrementUsedAsCorrect(int ID) {
		try {
			dbaccess = new Access();
			dbaccess.createConnection();
			queryString = "UPDATE " + table + " SET UsedAsCorrect = UsedAsCorrect + 1 WHERE ID = '" + ID + "'";
			dbaccess.st.executeUpdate(queryString);
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("PictureOperations.incrementUsedAsCorrect collapsed!");
			e.printStackTrace();
		}
	}

	/*
	 * Saves how often the picture was chosen correctly
	 */
	public void incrementChosenAsCorrect(int ID) {
		try {
			dbaccess = new Access();
			dbaccess.createConnection();
			queryString = "UPDATE " + table + " SET ChosenAsCorrect = ChosenAsCorrect + 1 WHERE ID = '" + ID + "'";
			dbaccess.st.executeUpdate(queryString);
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException e) {
			logger.fatal("PictureOperations.incrementChosenAsCorrect collapsed!");
			e.printStackTrace();
		}
	}

	/**
	 * Methods to store (new) pictures in the database
	 **/

	/*
	 * Method to import pictures into database (for the first time) Pictures are
	 * imported as BLOB's (binary large object) into the database Views is set
	 * to 0
	 */
	public void importPicture() {
		try {
			logger.info("Storing picture into database");
			// String Tag = SearchEngines.getTag();
			// Stack<String> tempstack = ImageCrawler.getImageStack();
			// absolute path of image for testing issues
			// if(!tempstack.isEmpty()){
			// System.out.println("WARNING: Empty stack in
			// PictureOperations.importPicture!");
			// }
			// File picture = new File(
			// "C:/SchauGenau/Downloads/" + tempstack.pop() + ".jpg");
			File picture = new File("C:/Users/SchauGenau/ownCloud/Blumen/Tulpe07.jpg"); // path
			FileInputStream fis;
			PreparedStatement psmnt = null;
			dbaccess = new Access();
			dbaccess.createConnection();
			psmnt = dbaccess.con.prepareStatement("INSERT INTO " + table
					+ "(TagGerman, TagEnglish, Picture, UsedAsCorrect, ChosenAsCorrect)" + "values(?,?,?,?,?)");
			psmnt.setString(1, "Tulpe"); // german tag
			psmnt.setString(2, "Tulip"); // english tag
			psmnt.setInt(4, 0);
			psmnt.setInt(5, 0);
			fis = new FileInputStream(picture);
			psmnt.setBinaryStream(3, fis, (int) (picture.length()));
			int s = psmnt.executeUpdate();
			if (s > 0) {
				System.out.println("Upload successfull");
			} else {
				System.out.println("Upload failed");
			}
			fis.close();
			dbaccess.con.close();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			logger.fatal("PictureOperations.importPicture() collapsed!");
			e.printStackTrace();
		}
	}

	/**
	 * Methods to provide different operations on the database used by other
	 * methods
	 **/

	/*
	 * Method to return the ID of one randomized Picture with given Tag
	 */
	public int fetchID(String Tag) throws ClassNotFoundException, SQLException {
		dbaccess = new Access();
		dbaccess.createConnection();
		int result;
		queryString = "SELECT ID FROM " + table + " WHERE Tag" + getLanguage() + " = '" + Tag
				+ "' ORDER BY RAND() LIMIT 1";
		dbaccess.rs = dbaccess.st.executeQuery(queryString);
		dbaccess.rs.next();
		result = dbaccess.rs.getInt("ID");
		return result;
	}

	/*
	 * Method to get all different Tags the database
	 */
	public LinkedList<String> createTagPool() throws SQLException, ClassNotFoundException {
		int maxID = getMaxID();
		String s;
		LinkedList<String> pool = new LinkedList<String>();

		for (int i = 1; i <= maxID; i++) {
			queryString = "SELECT Tag" + language + " from " + table + " WHERE ID = '" + i + "'";
			dbaccess.rs = dbaccess.st.executeQuery(queryString);
			if (dbaccess.rs.next()) {
				s = dbaccess.rs.getString("Tag" + language);
				if (pool.contains(s) == false) {
					pool.add(s);
				}
			}
		}
		dbaccess.con.close();
		return pool;
	}

	/*
	 * Method to create random tags for pictures
	 */
	public void createRdmTags() {
		try {
			LinkedList<String> poollist = new LinkedList<String>();
			poollist = createTagPool();
			Object[] poolarray = new String[poollist.size()];
			poolarray = poollist.toArray();
			Random generator = new Random();
			int randomIndex = generator.nextInt(poolarray.length);
			setCorPictTag((String) poolarray[randomIndex]);

			LinkedList<String> poollist2 = poollist;
			Object[] poolarray2 = new String[poollist2.size()];
			poolarray2 = poollist2.toArray();
			int randomIndex2 = generator.nextInt(poolarray2.length);
			poollist2.remove(getCorPictTag());
			setIncorPictTag((String) poolarray2[randomIndex2]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Method to get the highest ID of Pictures
	 */
	public int getMaxID() throws SQLException, ClassNotFoundException {
		int maxID = 1;
		dbaccess = new Access();
		dbaccess.createConnection();
		queryString = "SELECT ID from " + table + " ORDER BY ID DESC LIMIT 1;";
		dbaccess.rs = dbaccess.st.executeQuery(queryString);
		dbaccess.rs.next();
		maxID = dbaccess.rs.getInt("ID");
		return maxID;
	}

}
