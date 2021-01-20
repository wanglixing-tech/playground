package crs.fcl.integration.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogManager;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssistantMain {
	private static File domainConfigFile = null;
	private static String baseDir4WSnWTS = null;
	private static String iibToolkitHome = null;
	private static String action = "create";
	private static final Logger LOG = LoggerFactory.getLogger(AssistantMain.class);
	private static IIBToolkitWSAssistantV2 mainApp;

	public static void main(String[] args) throws Exception {
		initializeLogging();
		mainApp = new IIBToolkitWSAssistantV2();

		/**
		 * First of all, get the location where is the IIB toolkit installed, which is
		 * one of the prerequisites and must be provided from JVM arguments
		 */
		iibToolkitHome = System.getProperty("iibToolkitHome");
		if (iibToolkitHome == null) {
			LOG.error("No IIB Toolkit HOME is provided with '-DiibToolkitHome=full-path-2-IIB-Toolkit'.");
			System.exit(1);
		} else {
			if ((new File(iibToolkitHome)).exists()) {
				LOG.info("IBM Integration Toolkit HOME:" + iibToolkitHome + " has been configrmed.");
			} else {
				LOG.error("IBM Integration Toolkit must be installed before running this utility.");
				System.exit(1);
			}
		}

		/**
		 * Then, Domain Configuration File from JVM arguments IIB repository (master)
		 * must be cloned previously as the Domain Configuration Files have to be
		 * uploaded from IIB master.
		 */
		String projectConfigFileName = System.getProperty("yamlFile");
		if (projectConfigFileName == null) {
			LOG.error("No Project Domain Configuration File is provided with '-DyamlFile=full-path-2-config-file'.");
			LOG.error("The file should always be ready immidately under IIB repository (master).");
			System.exit(1);
		}

		domainConfigFile = new File(projectConfigFileName);

		if (!domainConfigFile.exists()) {
			LOG.error("No Domain Configuration File found immidately under IIB repository (master).");
			System.exit(1);
		} else {
			LOG.info("Domain Configuration File:" + projectConfigFileName + " has been confirmed.");
		}

		/**
		 * Third one, the destination where you want to create IIB workspace and working
		 * trees, If nothing specified, it means all IIB workspaces and working trees
		 * will be creating to USER_HOME/WORKSPACE/ and USER_HOME/WORKTREES/,
		 * respectively.
		 */
		String sourceDir = System.getProperty("sourceDir");
		if (sourceDir == null) {
			LOG.warn("No source directory is provided with '-DsourceDir=full-path-2-workspace'.");
			LOG.warn("Current working directory will be used as the source directory.");
			sourceDir = System.getProperty("user.dir");
		}
		baseDir4WSnWTS = sourceDir;

		if (!(new File(sourceDir)).exists()) {
			LOG.error("Specified sourceDir does not exist, please specify a existing directory and try again.");
			System.exit(1);
		} else {
			LOG.info("Source directory for creating IIB workspace and working trees:" + sourceDir
					+ " has been confirmed.");
		}

		/**
		 * Last, the action you want this program to do, use 'create' or 'delete'
		 */
		String inputAct = System.getProperty("action");
		if (inputAct == null) {
			LOG.warn("No action was specified with '-Daction=create' or '-Daction=delete', take default one (create).");
		} else {
			LOG.info("Program will got to " + inputAct + "workspace from now on.");
			action = inputAct;			
		}

		switch (action) {
		case "create":
			try {
				mainApp.createIIBWorkspace(domainConfigFile, baseDir4WSnWTS, iibToolkitHome);
			} catch (InterruptedException | ExecutionException | GitCmdExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "delete":
			try {
				mainApp.deleteIIBWorkspace(domainConfigFile, baseDir4WSnWTS, iibToolkitHome);
			} catch (InterruptedException | ExecutionException | GitCmdExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void initializeLogging() {
		try (InputStream inputStream = Utils.getConfResource("logging.properties")) {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			LOG.error("unable to find logging configuration file logging.properties");
		}
	}

}
