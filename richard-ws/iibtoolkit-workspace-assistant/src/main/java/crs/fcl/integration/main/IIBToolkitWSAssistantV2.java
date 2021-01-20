package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class IIBToolkitWSAssistantV2 {
	private static final String pomFileName = "pom.xml";
	//private static final String iibAppGroupId = "crs.fcl.integration.iib.bar";
	public static String pluginAppName = "toolkit-plugin.App";
	private static String strPath2IIBRepoHome = null;
	private static String projectName = null;
	private static ArrayList<Worktree> worktreesInfo = new ArrayList<Worktree>();
	private static ArrayList<Object> appsInfo = new ArrayList<Object>();
	private static String workspaceRelPath = null;
	private static ArrayList<String> worktreesPathList = new ArrayList<String>();		
	private static Stack<Set<File>> stackOfProjectSets = null;
	private static final Logger LOG = LoggerFactory.getLogger(IIBToolkitWSAssistantV2.class);
	private static String projectParentGroupId = null;
	private static String projectParentArtifactId = null;
	private static String projectParentVersion = null;
	private static String projectParentRelativePath = null;
	private static String projectAppsGroupId = null;
	private static String projectArtifactId = null;
	private static String projectVersion = null;

	public IIBToolkitWSAssistantV2() {
	}

	public void createIIBWorkspace(File projectConfigFile, String baseDir4WSnWTS, String iibToolkitHome) 
			throws Exception {

		/**
		 * Find out the path to IIB repository (master), which will be used to determine the location 
		 * of for creating working trees in case of using relative path in project configuration file.
		 */
		strPath2IIBRepoHome = (projectConfigFile.getParent());
		getProjectInfo(projectConfigFile);
		getWorktreesInfo(projectConfigFile);
		getAppsInfo(projectConfigFile);

		/** The sourceDir keeps the full path where the workspace is to be created.
		 * Note about WORKSPACE_HOME and WORKTRE_HOME:
		 * (2) If this properties is not provided from command-line, the utility will regards 
		 *     the current working directory as the sourceDir and WORKSPACE_HOME as well
		 *     as WORKTREES_HOME will be created under the current working directory.
		 * (3) If this properties is provided from command-line, the utility will create 
		 *     WORKSPACE_HOME and WORKTREES_HOME under specified sourceDir.
		 * (4) WORKSPACE_HOME and WORKTREES_HOME will ignore the 'shortcut pathname' like '../' 
		 *     and take the pathname part only.
		 */ 
		
		/**
		 * Absolute Path to WORKSPACE
		 */
		String wsAbsolutePath = null;
		
		/*
		 * Remove shortcuts in relative pathname, the reason is considering the case for none-Jenkins environments
		 */
		workspaceRelPath = workspaceRelPath.replaceAll("\\.\\.\\/", "");
		workspaceRelPath = workspaceRelPath.replaceAll("\\/", "\\\\");
		
		wsAbsolutePath = baseDir4WSnWTS + File.separator + workspaceRelPath;
		LOG.info("Absolute path to workspace is: " + wsAbsolutePath);

		if (wsAbsolutePath.equals(strPath2IIBRepoHome)) {
			LOG.error("According to GIT spec, wotking trees can't be created inside of IIB Repository (master). "
					+ "Please create workspace directory in other location and re-run this program from there.");
			System.exit(1);
		}

		File path2IIBRepoHome = new File(strPath2IIBRepoHome);
		
		for (Worktree wt : (ArrayList<Worktree>) worktreesInfo) {
			String wtName = wt.getWorktreeName();
			String branchName = wt.getBranchName();
			
			String wtRelPath = (wt.getWorktreeRelPath()).replaceAll("\\.\\.\\/", "");
			wtRelPath = wtRelPath.replaceAll("\\/", "\\\\");;
			wtName = wtName.replaceAll("\\/", "\\\\");;
			
			String absolutePath2Worktree = baseDir4WSnWTS + File.separator + wtRelPath + File.separator + wtName;
			worktreesPathList.add(absolutePath2Worktree);
			
			LOG.info("Absolute path to worktrees will be:  " + absolutePath2Worktree);
			File wtPath = new File(absolutePath2Worktree + File.separator + ".git");
			if (!wtPath.exists()) {
				LOG.info("Creating Worktree " + absolutePath2Worktree + "......");
				addWorktree(path2IIBRepoHome, absolutePath2Worktree, branchName);
			} else {
				LOG.info("Worktree " + absolutePath2Worktree + " has already been there, refresh it from remote repository server......");
				LOG.warn("Refreshing is going pull work from remote repository server, "
						+ " it may fail if you have uncommitted changes in the worktree......");
				LOG.warn("In case of failure, please take proper actions from your git client.");
				refreshWorktrees(new File(wtPath.getParent()));
			}
		} 
		
		/**
		 * Import all working trees(components) required in the domain
		 * 
		 */
		
		// Determine projects in each worktree for importing		
		ArrayList<String> projects = new ArrayList<String>();
		for (int i = 0; i < worktreesPathList.size(); ++i) {
			String targetWorktreePath = worktreesPathList.get(i);
			File file = new File(targetWorktreePath);
			
			// Double check the possible projects are in their directory
			String[] possiblePrjs = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});

			for (int j = 0; j < possiblePrjs.length; ++j) {
				File targetFile = new File(targetWorktreePath + File.separator + possiblePrjs[j] + File.separator + ".project");
				if (targetFile.exists() && targetFile.isFile()) {
					// True Eclipse project found, add it to project list
					projects.add(targetWorktreePath + File.separator + possiblePrjs[j]);
					LOG.info("Found project: " + targetWorktreePath + File.separator + possiblePrjs[j]);
				} else {
					LOG.warn(targetWorktreePath + File.separator + possiblePrjs[j] + " does not seem to be a qualified project, skipped");					
				}
			}
		}

		if (projects.size() == 0) {
			LOG.warn("No any Eclipse project found from specified worktrees" );
		} else {
			/**
			 * Create pom.xml file for all IIB projects with APP/LIB nature, these generated pom.xml won't be used 
			 * for building any IIB applications as these IIB applications are not really MAVEN projects, the only
			 * effect is to avoid the error messages from BAD pom.xml files when the applications are opened in IIB
			 * Toolkit, this step will create new or overwrite existing pom.xml files with correct contents.
			 */
			LOG.info("Generate pom.xml files for IIB Applications/Libraries");
			ArrayList<String> iibProjects = pickupIIBAppsAndLibs(projects);		
			LOG.info("Total " + iibProjects.size() +  "IIB Applications/Libraries");
			
			for (String iibProject : iibProjects) {
				createAppPomFile(new File(iibProject));
				//AddBuildNProfiles2Pom abp = new AddBuildNProfiles2Pom();
				//abp.insertBuildNProfiles(bootstrapHome, targetPomFile);
			}
			LOG.info("Done.");
			/**
			 * Re-ordering all IIB projects for resolving cross-reference problems, for those projects being referred 
			 * by other projects must be imported first.
			 */
			LOG.info("!!!Re-order all projects!!!");
			ReorganizeProjectImportSequence iRPIS = new ReorganizeProjectImportSequence();	
			stackOfProjectSets = iRPIS.getWellOrganizedProjectList(projects);

			/**
			 * display all re-ordered projects
			 */
			int orderNo = 0;
			System.out.println("All projects in the domain will be imported with following group orders, it may take "
					+ "couple of minutes, please wait patiently till it is complete. Should any problem happend, you "
					+ "will get a popup window, which shows you which log file you are supposed to look into.");
		
			while (!stackOfProjectSets.empty()) {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Import group:" + orderNo++ );
				Set<File> onePrjSet = stackOfProjectSets.pop();
				for (File onePrj : onePrjSet){
					//System.out.println("Importing Project:" + onePrj.getAbsolutePath());
					LOG.info("Importing Project:" + onePrj.getAbsolutePath());
					if (importEclipseProject(iibToolkitHome, wsAbsolutePath, onePrj.getAbsolutePath())) {
						LOG.info("Done.");	
					} else {
						LOG.error("Failed to import " + onePrj + ", program can't understand it's project-specific issue or not,"
								+ "for the safely consideration, program will terminate here, once the problem is fixed, please try again.");
						System.exit(999);
					}
				}
			}
		}
		/**
		 * Double check total current working trees
		 */
		listWorktrees(path2IIBRepoHome);
	}
	
	public void deleteIIBWorkspace(File projectConfigFile, String baseDir4WSnWTS, String iibToolkitHome) 
			throws IOException, InterruptedException, ExecutionException, GitCmdExecutionException {

		/**
		 * Find out the path to IIB repository (master), which will be used to determine the location 
		 * of for creating working trees in case of using relative path in project configuration file.
		 */
		strPath2IIBRepoHome = (projectConfigFile.getParent());
		getProjectInfo(projectConfigFile);
		getWorktreesInfo(projectConfigFile);
		getAppsInfo(projectConfigFile);

		/** The sourceDir keeps the full path where the workspace is to be created.
		 * Note about WORKSPACE_HOME and WORKTRE_HOME:
		 * (2) If this properties is not provided from command-line, the utility will regards 
		 *     the current working directory as the sourceDir and WORKSPACE_HOME as well
		 *     as WORKTREES_HOME will be created under the current working directory.
		 * (3) If this properties is provided from command-line, the utility will create 
		 *     WORKSPACE_HOME and WORKTREES_HOME under specified sourceDir.
		 * (4) WORKSPACE_HOME and WORKTREES_HOME will ignore the 'shortcut pathname' like '../' 
		 *     and take the pathname part only.
		 */ 
		String wsAbsolutePath = null;
		
		/*
		 * Remove shortcuts in relative pathname, the reason is considering the case for none-Jenkins environments
		 */
		workspaceRelPath = workspaceRelPath.replaceAll("\\.\\.\\/", "");
		workspaceRelPath = workspaceRelPath.replaceAll("\\/", "\\\\");
		wsAbsolutePath = baseDir4WSnWTS + File.separator + workspaceRelPath;
		LOG.info("Absolute path to workspace is: " + wsAbsolutePath);

		if (wsAbsolutePath.equals(strPath2IIBRepoHome)) {
			LOG.error("According to GIT spec, wotking trees can't be created inside of IIB Repository (master). "
					+ "Please create workspace directory in other location and re-run this program from there.");
			System.exit(1);
		}

		File path2IIBRepoHome = new File(strPath2IIBRepoHome);
		File workspacePath = new File(wsAbsolutePath);
		for (Worktree wt : (ArrayList<Worktree>) worktreesInfo) {
			String wtName = wt.getWorktreeName();
			String wtType = wt.getWorktreeType();
			String wtRelPath = (wt.getWorktreeRelPath()).replaceAll("\\.\\.\\/", "");
			wtRelPath = wtRelPath.replaceAll("\\/", "\\\\");
			wtName = wtName.replaceAll("\\/", "\\\\");;
			String absolutePath2Worktree = baseDir4WSnWTS + File.separator + wtRelPath + File.separator + wtName;
			LOG.info("Absolute path to worktrees is:  " + absolutePath2Worktree);
			File wtPath = new File(absolutePath2Worktree + File.separator + ".git");
			if (wtPath.exists() && wtType.equals("dedicated")) {
				LOG.info("Deleting dedicated worktree: " + absolutePath2Worktree + "......");
				removeWorktree(path2IIBRepoHome, absolutePath2Worktree);
			} else {
				LOG.info("Worktree " + wtName + " does not exist......");
			}
			LOG.info("......Done");
		}
		LOG.info("Deleting workspace...... " + workspacePath );
		FileUtils.deleteDirectory(workspacePath);
		LOG.info("......Done");
		
		/**
		 * Double check total current working trees
		 */
		listWorktrees(path2IIBRepoHome);
	}
	

	/**
	 * To list all existing working trees
	 * @param gitDir the master branch, which holds all working trees
	 * @throws IOException Possible IO exception
	 * @throws InterruptedException Possible Interruption exception
	 */
	private void listWorktrees(File gitDir) throws IOException, InterruptedException {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		BufferedReader reader = null;
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			builder.command("cmd.exe", "/c", "git worktree list");
		} else {
			builder.command("sh", "-c", "git worktree list");
		}
		
		builder.directory(gitDir);
		Process process = builder.start();
		int exitCode = process.waitFor();
		LOG.info("exitCode==" + exitCode);
		if (exitCode == 0) {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		} else {
			reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		}
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(System.getProperty("line.separator"));
		}
		String result = stringBuilder.toString();
		LOG.info("\n" + result);
	}
	
	/**
	 * To refresh all working trees
	 * @param targetWorktreeDir the target working tree
	 * @throws IOException possible IO exception
	 * @throws InterruptedException Possible Interruption exception
	 */
	private void refreshWorktrees(File targetWorktreeDir) throws IOException, InterruptedException {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		BufferedReader reader = null;
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			builder.command("cmd.exe", "/c", "git pull");
		} else {
			builder.command("sh", "-c", "git pull");
		}
		builder.directory(targetWorktreeDir);
		Process process = builder.start();
		int exitCode = process.waitFor();
		LOG.info("exitCode==" + exitCode);
		if (exitCode == 0) {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		} else {
			reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		}
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(System.getProperty("line.separator"));
		}
		String result = stringBuilder.toString();
		LOG.info(result);
	}

	/**
	 * To add individual working tree
	 * @param gitDir the master branch, which holds all working trees
	 * @param branchName Working tree local branch name
	 * @param wtHome Working tree home
	 * @throws IOException possible exception
	 * @throws InterruptedException possible exception
	 * @throws GitCmdExecutionException 
	 */
	private void addWorktree(File gitDir,String branchName, String wtHome)
			throws IOException, InterruptedException, GitCmdExecutionException {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		BufferedReader reader = null;
		ProcessBuilder builder = new ProcessBuilder();
		String cmd = String.format("git worktree add -f %s --checkout %s", branchName, wtHome);
		LOG.info(cmd);
		if (isWindows) {
			builder.command("cmd.exe", "/c", cmd);
		} else {
			builder.command("sh", "-c", cmd);
		}
		builder.directory(gitDir);
		Process process = builder.start();
		int exitCode = process.waitFor();
		LOG.info("exitCode==" + exitCode);
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		if (exitCode == 0) {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(System.getProperty("line.separator"));
			}
			String result = stringBuilder.toString();
			LOG.info(result);
		} else {
			reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(System.getProperty("line.separator"));
			}
			String result = stringBuilder.toString();
			throw new GitCmdExecutionException(result);
		}
	}

	/**
	 * To remove individual working tree
	 * @param gitDir the master branch, which holds all working trees
	 * @param path2Wt path to worktree
	 * @throws IOException Possible exception
	 * @throws InterruptedException Possible exception
	 */ 
	private void removeWorktree(File gitDir, String path2Wt)
			throws IOException, InterruptedException {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		BufferedReader reader = null;
		ProcessBuilder builder = new ProcessBuilder();
		String cmd = String.format("git worktree remove -f %s", path2Wt );
		LOG.info(cmd);
		if (isWindows) {
			builder.command("cmd.exe", "/c", cmd);
		} else {
			builder.command("sh", "-c", cmd);
		}
		builder.directory(gitDir);
		Process process = builder.start();
		int exitCode = process.waitFor();
		LOG.info("exitCode==" + exitCode);
		if (exitCode == 0) {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		} else {
			reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		}
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(System.getProperty("line.separator"));
		}
		String result = stringBuilder.toString();
		LOG.info(result);
	}

	/**
	 * This is for importing one individual project into workspace
	 * @param iibToolkitHome
	 * @param wsAbsolutePath
	 * @param projectPath
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private boolean importEclipseProject(String iibToolkitHome, String wsAbsolutePath, String projectPath) throws IOException, InterruptedException {
		boolean returnCode = true;
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		BufferedReader reader = null;
		ProcessBuilder builder = new ProcessBuilder();
		String cmd = String.format("eclipse.exe -noSplash -application %s -data %s -import %s", pluginAppName, wsAbsolutePath, projectPath);
		LOG.debug("IIB Toolkit home=" + iibToolkitHome);
		LOG.debug("Workspace absolute path=" + wsAbsolutePath);
		LOG.debug("project path=" + projectPath);
		LOG.debug(cmd);
		if (isWindows) {
			builder.command("cmd.exe", "/c", cmd);
		} else {
			builder.command("sh", "-c", cmd);
		}
		builder.directory(new File(iibToolkitHome));
		Process process = builder.start();
		int exitCode = process.waitFor();
		LOG.info("exitCode==" + exitCode);
		if (exitCode == 0) {
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(System.getProperty("line.separator"));
			}
			String result = stringBuilder.toString();
			LOG.info(result);
			LOG.info(projectPath + " has been imported successfully.");
		} else {
			reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(System.getProperty("line.separator"));
			}
			String result = stringBuilder.toString();
			LOG.error(result);
			LOG.error("Project " + projectPath + " importing was failed unfortunately.");
			returnCode = false;
		}
		return returnCode;
	}
	
	/**
	 * To create application pom.xml file
	 * @param appPath Current working directory, which is all applications' HOME directory.
	 * @param appName Target application name 
	 * @throws Exception Possible exception
	 */
	public static void createAppPomFile(File appPath) throws Exception {

		/**
		 * For the time being, use domain/project version as application version.
		 *  
		 * In the future, we need to control each application's version via domain 
		 * configuration file, we will resume using following approach:
		 *     String appVersion = getAppVersionByAppName(appsInfo, appName);
		 * 
		 */
		String appName = appPath.getName();
		try {
			Writer appPomWriter = new FileWriter(appPath.getPath() + File.separator + pomFileName);  
			Model appModel = new Model();
			appModel.setModelVersion("4.0.0");
			appModel.setName(appName);
			Parent appParentPom = new Parent();
			appParentPom.setGroupId(projectParentGroupId);
			appParentPom.setArtifactId(projectParentArtifactId);
			appParentPom.setVersion(projectParentVersion);
			appParentPom.setRelativePath(projectParentRelativePath);
			appModel.setParent(appParentPom);
			
			appModel.setGroupId(projectAppsGroupId);
			appModel.setArtifactId(appName);
			appModel.setVersion(projectVersion);
			appModel.setPackaging("pom");
			new MavenXpp3Writer().write(appPomWriter, appModel);
			appPomWriter.close();
			LOG.info("Succefully created pom.xml for " + appName);
		} catch (Exception e) {
			LOG.error("Failed to create pom.xml for " + appName);
			LOG.error(e.getStackTrace().toString());
		}
	}

	/**
	 * Get application's version based on an application name, this is designed for managing 
	 * application level version/release by using the project configuration file due to the 
	 * current Bitbucket repository layout can't use JGitflow.
	 * @param appsInfo All applications information defined in project configuration file
	 * @param appName Target application name
	 * @return application version as String
	 */
	public static String getAppVersionByAppName(ArrayList<Object> appsInfo, String appName) {
		String appVersion = null;
		Application targetApp = null;
		for (Object oneApp : appsInfo) {
			// System.out.println("Loaded object type:" + oneApp.getClass());
			targetApp = (Application) oneApp;
			if (targetApp.getAppName().equals(appName) && targetApp.getAppType().equals("APP")) {
				appVersion = targetApp.getAppVersion();
				break;
			}
		}
		return appVersion;
	}


	/**
	 * To get project level information
	 * @param confFile Project configuration file
	 * @throws FileNotFoundException Possible exception
	 */
	@SuppressWarnings("rawtypes")
	private void getProjectInfo(File confFile) throws FileNotFoundException {
		try {
			InputStream input = new FileInputStream(confFile);
			Yaml yaml = new Yaml();
			Iterable<Object> itrPrj = yaml.loadAll(input);
	
			for (Object projectInfo : itrPrj) {
				if (projectInfo instanceof java.util.LinkedHashMap) {
					Object domainAttrs = ((java.util.LinkedHashMap) projectInfo).get("domain");
					System.out.println("Loaded object type:" + domainAttrs.getClass());
					projectName = (String) ((LinkedHashMap) domainAttrs).get("projectName");
					workspaceRelPath = (String) ((LinkedHashMap) domainAttrs).get("workspaceRelPath");
					projectParentGroupId = (String) ((LinkedHashMap) domainAttrs).get("projectParentGroupId");
					projectParentArtifactId = (String) ((LinkedHashMap) domainAttrs).get("projectParentArtifactId");
					projectParentVersion = (String) ((LinkedHashMap) domainAttrs).get("projectParentVersion");
					projectParentRelativePath = (String) ((LinkedHashMap) domainAttrs).get("projectParentRelativePath");
					projectAppsGroupId = (String) ((LinkedHashMap) domainAttrs).get("projectAppsGroupId");
					projectArtifactId = (String) ((LinkedHashMap) domainAttrs).get("projectArtifactId");
					projectVersion = (String) ((LinkedHashMap) domainAttrs).get("projectVersion");
					if ((projectName==null) || (workspaceRelPath==null)) {
						LOG.error("No projectName or workspaceRelPath "
								+  "found form Project Master Configuration File");
					} else {
						LOG.info("Done with project-wise configuration");
					}
					break;
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			LOG.error("Project Master Configuration File was not found.");
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("IOException while working on Project Master Configuration File.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Get working trees defined in project master configuration file for the IIB project.
	 * @param confFile configuration file (specified with -DyamlFile=XXXXXXXX )
	 * @throws FileNotFoundException Possible exceptions
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getWorktreesInfo(File confFile) throws FileNotFoundException {
		LOG.info("In getWorktreesInfo()");
		InputStream input = new FileInputStream(confFile);
		Yaml yaml = new Yaml();
		Iterable<Object> itr = yaml.loadAll(input);
		for (Object projectInfo : itr) {
			if (projectInfo instanceof java.util.LinkedHashMap) {
				// Get working tree's information
				ArrayList<Object> allWorkTrees = (ArrayList) ((java.util.LinkedHashMap) projectInfo).get("worktrees");
				for (Object oneWorktree : allWorkTrees) {
					Worktree wTree = new Worktree();
					wTree.setWorktreeName((String) ((LinkedHashMap) oneWorktree).get("worktreeName"));
					wTree.setBranchName((String) ((LinkedHashMap) oneWorktree).get("branchName"));
					wTree.setWorktreeRelPath((String) ((LinkedHashMap) oneWorktree).get("worktreeRelPath"));
					wTree.setWorktreeType((String) ((LinkedHashMap) oneWorktree).get("worktreeType"));
					worktreesInfo.add(wTree);
				}
			}
		}
	}
	
	/**
	 * To get all applications information
	 * @param confFile project configuration file
	 * @throws FileNotFoundException Possible exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getAppsInfo(File confFile) throws FileNotFoundException {
		InputStream input = new FileInputStream(confFile);
		Yaml yaml = new Yaml();
		Iterable<Object> itr = yaml.loadAll(input);
		for (Object projectInfo : itr) {
			// System.out.println("Loaded object type:" + c1.getClass());
			if (projectInfo instanceof java.util.LinkedHashMap) {
				// Get application's information
				ArrayList<Object> allApps = (ArrayList) ((java.util.LinkedHashMap) projectInfo).get("applications");
				for (Object oneApp : allApps) {
					// System.out.println("Loaded object type:" + oneApp.getClass());
					Application app = new Application();
					app.setAppName((String) ((LinkedHashMap) oneApp).get("appName"));
					app.setAppType((String) ((LinkedHashMap) oneApp).get("appType"));
					app.setAppVersion((String) ((LinkedHashMap) oneApp).get("appVersion"));
					appsInfo.add(app);
				}
			}
		}
	}

	public ArrayList<String> pickupIIBAppsAndLibs(ArrayList<String> projectList) throws DocumentException {
		String appNature = "com.ibm.etools.msgbroker.tooling.applicationNature";
		String libNature = "com.ibm.etools.msgbroker.tooling.libraryNature";
		ArrayList<String> iibAppList = new ArrayList<String>();
		for (String oneProject : projectList) {
			File dotProjectFile = new File(oneProject + File.separator + ".project");
			if (dotProjectFile.exists()) {
				Document dotProjectDoc = parseXML(dotProjectFile);
				String sXPath2targetNode = "//*[name()='nature']";
				List<org.dom4j.Node> natureNodeList = dotProjectDoc.selectNodes(sXPath2targetNode);
				for (org.dom4j.Node nature : natureNodeList) {
					Element natureElem = (Element) nature;
					LOG.info("Nature:" + natureElem.getText());
					if (natureElem.getText().equals(appNature) ||
						natureElem.getText().equals(libNature)) {
						iibAppList.add(oneProject);
						break;
					} 
				}
			} else {
				LOG.warn("No .project exist in project:" + dotProjectFile + ", skip it.");
			}
		}	
		return iibAppList;		
	}

	private Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}


}
