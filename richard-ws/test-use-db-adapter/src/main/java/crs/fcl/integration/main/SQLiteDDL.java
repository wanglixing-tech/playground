package crs.fcl.integration.main;

import java.sql.*;

public class SQLiteDDL {
	public static void main(String args[]) {
		Connection c = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS ESB_FILE_STATUS(" + 
					"    ID INTEGER PRIMARY KEY AUTOINCREMENT," + 
					"    HOSTNAME VARCHAR(255)," + 
					"    DIRECTORY VARCHAR(255)," + 
					"    FILE_NAME VARCHAR(255)," + 
					"    CHMOD VARCHAR(255)," + 
					"    CHOWN VARCHAR(255)," + 
					"    CHGRP VARCHAR(255)," + 
					"    FILE_SIZE INTEGER," + 
					"    LAST_MODIFIED VARCHAR(255)," + 
					"    STATUS VARCHAR(255)," + 
					"    STATUS_TIME VARCHAR(255) NOT NULL," + 
					"    CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" + 
					")";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Table created successfully");
		
	}
}
