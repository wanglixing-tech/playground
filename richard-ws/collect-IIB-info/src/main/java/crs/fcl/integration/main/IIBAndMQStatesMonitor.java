package crs.fcl.integration.main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.jcraft.jsch.JSchException;

public class IIBAndMQStatesMonitor {
	private static final String className = IIBAndMQStatesMonitor.class.getName();
	private static final java.util.logging.Logger logger = dro.util.Logger.getLogger(className);
	private static ArrayList<IIBBroker> iNodeList = null;
	private static File iibEnvConfigFile = null;
	private static String envName = null;
	
	public static void main(String[] args) throws IOException, JSchException, URISyntaxException, InterruptedException {
		/**
		 * First of all, get target environment name from JVM arguments
		 */
		envName = System.getProperty("env");
		if (envName == null) {
			logger.log(Level.SEVERE,
					"Target IIB environment name must be provided with '-Denv=[DEV | TEST | QA | UAT | PROD ] '.");
			System.exit(1);
		}

		logger.info("Target IIB environment is: " + envName);
		/**
		 * First of all, get iibHome and project configuration file from JVM arguments
		 */
		String iibEnvConfigFileName = System.getProperty("yamlFile");
		if (iibEnvConfigFileName == null) {
			logger.log(Level.SEVERE,
					"IIB environment configuration file must be provided with '-DyamlFile=full-path-2-config-file'.");
			System.exit(1);
		}

		/**
		 * IIB repository must be cloned previously as the project configuration file
		 * must be checked out from IIB master.
		 */
		iibEnvConfigFile = new File(iibEnvConfigFileName);

		if (!iibEnvConfigFile.exists()) {
			logger.log(Level.SEVERE, "Specified environment configuration file: " + iibEnvConfigFileName + " could be found.");
			System.exit(1);
		} else {
			logger.info("IIB environment configuration file: " + iibEnvConfigFileName + " has been confirmed.");
		}
		
		IIBEnvFileParser envConf = new IIBEnvFileParser();
		IIBEnvironments devEnv = envConf.getEnvConfig( iibEnvConfigFileName, envName );
		
		IIBMQObserver iibMQObserver = new IIBMQObserver();
		IIBEnvObserver iibEnvObserver = new IIBEnvObserver();
		iNodeList = devEnv.getiNodeList();
		iibMQObserver.refreshMQsStatuses(iNodeList);
		while (true) {			
			iibMQObserver.refreshMQsStatuses(iNodeList);
			iibEnvObserver.refreshINodesStatuses(iNodeList);						
			TimeUnit.MINUTES.sleep(1);		
		}
	}
}
