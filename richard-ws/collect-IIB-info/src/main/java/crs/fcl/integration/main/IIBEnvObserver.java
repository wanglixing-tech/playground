package crs.fcl.integration.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.JSchException;

public class IIBEnvObserver {
	final private static String className = IIBEnvObserver.class.getName();
	final private static java.util.logging.Logger logger = dro.util.Logger.getLogger(className);
	private HashMap<String, String> allInOneHistoryMap = null;
	final static private Pattern pIServer = Pattern.compile("Integration\\sserver\\s\\'(.*)\\'\\son\\s.*$");
	final static private Pattern pApp = Pattern.compile("Application\\s\\'(.*)\\'\\son\\s.*$");

	public void refreshINodesStatuses(ArrayList<IIBBroker> iNodeList)
			throws JSchException, IOException, URISyntaxException {

		/**
		 * Check each queue Manager's status for each Integration Node
		 */
		for (IIBBroker in : iNodeList) {
			IIBSshExecHelper ssh = new IIBSshExecHelper(in);
			SshExecResponse resp = ssh.runCommand(RemoteCmdBuilder.getCurrentiNodeStatus(in.getiNodeName()));
			if (resp.getExecCode() == 0) {
				if (resp.getStdOut().contains(in.getiNodeName()) && resp.getStdOut().contains("BIP1284I")) {
					in.setiNodeStatus("Running");
				} else {
					if (resp.getStdOut().contains(in.getiNodeName()) && resp.getStdOut().contains("BIP1285I")) {
						in.setiNodeStatus("Stopped");
					}
				}
			} else {
				in.setiNodeStatus("Unknown");
			}
			
			refreshIServersStatuses(in);
			/*
			 * Dynamic key for iNode object
			 */
			String compondKey = in.getHostName() + "-" + in.getiNodeName();
			if (null == allInOneHistoryMap) {
				allInOneHistoryMap = new HashMap<String, String>();
			} else {
				if (! allInOneHistoryMap.containsKey(compondKey) || 
						!allInOneHistoryMap.get(compondKey).equals(in.getiNodeStatus())) {
					allInOneHistoryMap.put(compondKey, in.getiNodeStatus());
					saveINodeStatusToDB(in);
				} 
			}
		}
	}

	public void saveINodeStatusToDB(IIBBroker in) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println(formatter.format(date) + "\t" + in);
	}

	public void refreshIServersStatuses(IIBBroker in)
			throws JSchException, IOException, URISyntaxException {
		ArrayList<IIBExecutionGroup> iServerList = new ArrayList<IIBExecutionGroup>();
		if (in.getiNodeStatus().contentEquals("Running")) {
			IIBSshExecHelper ssh = new IIBSshExecHelper(in);
			SshExecResponse resp = ssh.runCommand(RemoteCmdBuilder.getCurrentiServersStatuses(in.getiNodeName()));
			if (resp.getExecCode() == 0) {
				String[] lines = resp.getStdOut().split("\n");
				for (final String line : lines) {
					if (null == line) {
						break;
					}
					if (true == line.startsWith("BIP1286") || (true == line.startsWith("BIP1287"))) {
						IIBExecutionGroup is = new IIBExecutionGroup();
						if (true == line.startsWith("BIP1286")) {
							Matcher mIServer = pIServer.matcher(line);
							if (mIServer.find()) {
								is.setHostName(in.getHostName());
								is.setiNodeName(in.getiNodeName());
								is.setiServerName(mIServer.group(1));
								is.setiServerStatus("Running");
							}

						} else {
							if (true == line.startsWith("BIP1287")) {
								Matcher mIServer = pIServer.matcher(line);
								if (mIServer.find()) {
									is.setHostName(in.getHostName());
									is.setiNodeName(in.getiNodeName());
									is.setiServerName(mIServer.group(1));
									is.setiServerStatus("Stopped");
								}
							}
						}
						
						iServerList.add(is);
						/*
						 * Dynamic key for iServer object
						 */
						String compondKey = in.getHostName() + "-" + in.getiNodeName() + "-" + is.getiServerName();
						if (null == allInOneHistoryMap) {
							allInOneHistoryMap = new HashMap<String, String>();
						} else {
							if (! allInOneHistoryMap.containsKey(compondKey) || 
									!allInOneHistoryMap.get(compondKey).equals(is.getiServerStatus())) {
								allInOneHistoryMap.put(compondKey, is.getiServerStatus());
								saveIServerStatusToDB(is);
							} 
						}
					} else {
						continue;
					}
				}
			} else {
				logger.log(Level.WARNING, "Remote call failed with RC=" + resp.getExecCode());
			}

			refreshAppsStatuses(in, iServerList);

		} else {
			logger.log(Level.WARNING, "Integration node " + in.getiNodeName() + " on " + in.getHostName() + " is not running.");
		}
	}

	public void saveIServerStatusToDB(IIBExecutionGroup is) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println(formatter.format(date) + "\t" + is);
	}

	public void refreshAppsStatuses(IIBBroker in, ArrayList<IIBExecutionGroup> iServerList)
			throws JSchException, IOException, URISyntaxException {
		for (IIBExecutionGroup is : iServerList) {
			if (is.getiNodeName().contentEquals(in.getiNodeName())
					&& (is.getiServerStatus().contentEquals("Running"))) {
				IIBSshExecHelper ssh = new IIBSshExecHelper(in);
				SshExecResponse resp = ssh
						.runCommand(RemoteCmdBuilder.getAppsStatuses(in.getiNodeName(), is.getiServerName()));
				if (resp.getExecCode() == 0) {
					String[] lines = resp.getStdOut().split("\n");
					for (final String line : lines) {
						if (null == line) {
							break;
						}
						if (true == line.startsWith("BIP1275I:") || (true == line.startsWith("BIP1276I:"))) {
							IIBApplication app = new IIBApplication();
							if (true == line.startsWith("BIP1275I:")) {
								Matcher mApp = pApp.matcher(line);
								if (mApp.find()) {
									app.setHostName(in.getHostName());
									app.setiNodeName(in.getiNodeName());
									app.setiServerName(is.getiServerName());
									app.setAppName(mApp.group(1));
									app.setAppStatus("Running");
								}

							} else {
								if (true == line.startsWith("BIP1276I:")) {
									Matcher mApp = pApp.matcher(line);
									if (mApp.find()) {
										app.setHostName(in.getHostName());
										app.setiNodeName(in.getiNodeName());
										app.setiServerName(is.getiServerName());
										app.setAppName(mApp.group(1));
										app.setAppStatus("Stopped");
									}

								}
							}
							/*
							 * Dynamic key for Application object
							 */
							String compondKey = in.getHostName() + "-" + in.getiNodeName() + "-" + is.getiServerName() + "-" + app.getAppName();
							if (null == allInOneHistoryMap) {
								allInOneHistoryMap = new HashMap<String, String>();
							} else {
								if (! allInOneHistoryMap.containsKey(compondKey) || 
										!allInOneHistoryMap.get(compondKey).equals(app.getAppStatus())) {
									allInOneHistoryMap.put(compondKey, app.getAppStatus());
									saveAppStatusToDB(app);
								} 
							}
						} else {
							continue;
						}
					}
				} else {
					logger.log(Level.WARNING, "Remote call failed with return code=" + resp.getExecCode());
				}
			}
		}
	}

	public void saveAppStatusToDB(IIBApplication app) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		System.out.println(formatter.format(date) + "\t" + app);
	}
}
