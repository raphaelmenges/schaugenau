package schaugenau.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Distributed under the MIT License. (See accompanying file LICENSE or copy at
 * https://github.com/raphaelmenges/schaugenau/blob/master/src/LICENSE)
 * 
 * @author Kevin Schmidt
 *
 */

import schaugenau.app.App;

/*
 * class for accessing database
 */
public class Access {

	public Connection con = null;
	public Statement st = null;
	public ResultSet rs = null;

	public void createConnection() throws ClassNotFoundException, SQLException {

		if (App.connectivity == App.DbConnectivity.ONLINE) { // online
			Class.forName("com.mysql.jdbc.Driver");
			/* Connect with ("Server", "Username", "Password") */
			con = DriverManager.getConnection("url", "user", "password"); // TODO
			st = con.createStatement();
			rs = st.executeQuery("SELECT VERSION()");

		} else { // offline
			Class.forName("com.mysql.jdbc.Driver");
			/* Connect with ("Server", "Username", "Password") */
			con = DriverManager.getConnection("url", "user", "password"); // TODO
			st = con.createStatement();
			rs = st.executeQuery("SELECT VERSION()");
		}
	}

	public void closeConnection() {

	}
}