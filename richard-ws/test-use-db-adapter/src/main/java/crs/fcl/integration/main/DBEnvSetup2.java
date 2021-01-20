package crs.fcl.integration.main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.logging.Level;

import crs.fcl.integration.db.DatabaseAdapter;
import crs.fcl.integration.db.DatabaseAdapter.Return;
import crs.fcl.integration.db.SQLException;
import dro.util.Properties;

public class DBEnvSetup2 {
	private static final String className = DBEnvSetup2.class.getName();
	private static final java.util.logging.Logger logger = dro.util.Logger.getLogger(className);
	private static String sqlScriptFileName = null;

	static {
		try {
			Properties.properties(new dro.util.Properties(DBEnvSetup2.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

		try {
			new DatabaseAdapter(Properties.properties())
			.returns(Return.Connection)
			.createStatement(Return.Statement)
			//.executeBatch(DatabaseAdapter.parse(sqlScriptFileName), Return.Statement)
			.executeUpdate(DatabaseAdapter.parse(sqlScriptFileName), Return.Statement)
			.returns(Return.DatabaseAdapter).shutdown();
			} catch (final SQLException e) {
			throw new RuntimeException(e);
			}
	}
}
