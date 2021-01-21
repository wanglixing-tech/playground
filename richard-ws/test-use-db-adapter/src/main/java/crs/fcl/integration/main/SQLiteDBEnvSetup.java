package crs.fcl.integration.main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Level;
import dro.util.Properties;

public class SQLiteDBEnvSetup {
	private static final String className = SQLiteDBEnvSetup.class.getName();
	private static final java.util.logging.Logger logger = dro.util.Logger.getLogger(className);
	private static String sqlScriptFileName = null;

	/*
	 * static { try { Properties.properties(new
	 * dro.util.Properties(SQLiteDBEnvSetup.class)); } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } }
	 */
	
	public static void main(String[] args) throws java.sql.SQLException, IOException {
		sqlScriptFileName = System.getProperty("sqlfile");
		if (sqlScriptFileName == null) {
			logger.log(Level.SEVERE,
					"A SQL(DDL/DML) script file must be provided with '-Dsqlfile=full-path-2-SQL-file'.");
			System.exit(1);
		}

		File sqlFile = new File(sqlScriptFileName);
		if (!sqlFile.exists()) {
			logger.log(Level.SEVERE,
					"The SQL(DDL/DML) script file does not exist, it must be provided with '-Dsqlfile=full-path-2-SQL-file'.");
			System.exit(1);
		}

		FileWriter writer = new FileWriter("C:\\Temp\\report.txt");
		PrintWriter printWriter = new PrintWriter(writer);
		//Connection con = null;

		try {
			//Class.forName("org.sqlite.JDBC");
			//con = DriverManager.getConnection("jdbc:sqlite:test.db");
			//System.out.println("Opened database successfully");

			FileReader sqlScript = new FileReader(sqlScriptFileName);
			//ScriptRunner runner = new ScriptRunner(con);
			ScriptRunner runner = new ScriptRunner();
			runner.setLogWriter(printWriter);
			runner.runScript(sqlScript);
			runner.closeConnection();
			sqlScript.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("SQL Script has been executed successfully");
	}
}
