package crs.fcl.integration.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import crs.fcl.integration.db.DatabaseAdapter;
import crs.fcl.integration.db.DatabaseAdapter.Return;
import crs.fcl.integration.db.SQLException;
import dro.util.Properties;

public class DBEnvSetup {
	private static final String className = DBEnvSetup.class.getName();
	private static final java.util.logging.Logger logger = dro.util.Logger.getLogger(className);
	private static String sqlScriptFileName = null;
	
	static {
		try {
			Properties.properties(new dro.util.Properties(DBEnvSetup.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public DBEnvSetup() {
	}

	public static void main(String[] args) {
		final Properties __p = Properties.properties();	
		sqlScriptFileName = System.getProperty("sqlfile");
		//if (sqlScriptFileName == null) {
		//	logger.log(Level.SEVERE,
		//			"A SQL(DDL/DML) script file must be provided with '-Dsqlfile=full-path-2-SQL-file'.");
		//	System.exit(1);
		//}
		sqlScriptFileName = "C:\\Users\\Richard.Wang\\WS-HOME\\ws-java\\db-script-runner\\setup.sql";
		
		try {
			final String[][] lines = DatabaseAdapter.parse("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java\\db-script-runner\\DDL-TEST.sql");
			new DatabaseAdapter(Properties.properties())
			.returns(Return.Connection)
			.createStatement(Return.Statement)
			.executeQuery("SELECT * from IIB_STATUS", DatabaseAdapter.Return.ResultSet)
			.returns(Return.DatabaseAdapter).shutdown();
			} catch (final SQLException e) {
			throw new RuntimeException(e);
			}		
	}
	
}
