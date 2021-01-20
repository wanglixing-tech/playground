package crs.fcl.integration.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.JSchException;

public class IIBMQObserver {
	final private static String className = IIBEnvObserver.class.getName();
	final private static java.util.logging.Logger logger = dro.util.Logger.getLogger(className);
	final static private Pattern pQMStatus = Pattern.compile("STATUS\\((.+)\\)$");	
	static private HashMap<String, String> allInOneMQMHistoryMap = null;
	
	public void refreshMQsStatuses(ArrayList<IIBBroker> iNodeList) throws JSchException, IOException, URISyntaxException {
		for (IIBBroker in : iNodeList) {
			IIBMQM iibmqm = getQueueManagerInfo(in);
			/*
			 * Dynamic key for iNode object
			 */
			String compondKey = in.getHostName() + "-" + in.getiNodeName();
			if (null == allInOneMQMHistoryMap) {
				allInOneMQMHistoryMap = new HashMap<String, String>();
			} else {
				if (! allInOneMQMHistoryMap.containsKey(compondKey) || 
						!allInOneMQMHistoryMap.get(compondKey).equals(in.getiNodeStatus())) {
					allInOneMQMHistoryMap.put(compondKey, iibmqm.getStatus());
					saveQMMStatusesToDB(iibmqm);
				} 
			}

			/*
			 * Target QUEUE: 
			 * (1) Any queue but the name not started with "SYSTEM."
			 * (2) Which ends with ".ERR*"; ".STATUS"; ".COMPLETE"
			 * (3) Above queues' curdepth > 0 and iprocs = 0	
			 */
			ArrayList<IIBMQQ> iibmqqList = getQueuesInfo(in);
			saveMQQStatusesToDB(iibmqqList);
		}		
	}
	
	private IIBMQQ convertMapToPojo(String hostname, String queuemanager, final Map<String, String> map) {
		return new IIBMQQ()
				.setHostname(hostname)
				.setQueuemanager(queuemanager)
				.setQUEUE(map.get("QUEUE"))
				.setCURDEPTH(Integer.parseInt(map.get("CURDEPTH")))
				.setMAXDEPTH(Integer.parseInt(map.get("MAXDEPTH")))
				.setIPPROCS(Integer.parseInt(map.get("IPPROCS")));
	}
	
	private ArrayList<IIBMQQ> getQueuesInfo(IIBBroker in) throws JSchException, IOException, URISyntaxException {
		final ArrayList<IIBMQQ> qStatusList = new ArrayList<IIBMQQ>();
		if (in.getQueueManagerStatus().equals("Running")) {
			IIBSshExecHelper ssh = new IIBSshExecHelper(in);
			SshExecResponse resp = ssh.runCommand(RemoteCmdBuilder.getAllQueueDepthInfo(in.getQueueManagerName()));
			//System.out.println(resp.getStdOut());
			//System.out.println(resp.getStdErr());
			String[] lines = resp.getStdOut().split("\n");
			{
				int s = 0;
				StringBuffer sb;// = null;
				Map<String, String> map = null;
				for (final String line: lines) {
					if (null == line) {
						break;
					}
					IIBMQQ q = null;
					if (true == line.startsWith("AMQ8409:") || true == line.startsWith("AMQ8409I:")) {
						if (null != map) {
							String qName = map.get("QUEUE").trim();
							int ipprocs = Integer.parseInt(map.get("IPPROCS"));
							if (! qName.startsWith("SYSTEM.") && 
									(qName.endsWith(".COMPLETE") || 
									qName.endsWith(".STATUS") || 
									qName.endsWith(".ERROR") || 
									qName.endsWith(".ERR")) &&
									ipprocs == 0)
								q = convertMapToPojo(in.getHostName(), in.getQueueManagerName(), map);
							//map = null;
							//System.out.println(q);
						}
						map = new HashMap<String, String>();
						s = 1;
					} else if (true == line.startsWith(" ")) {
						if (1 == s) {
							String key = null, value = null;
							sb = new StringBuffer();
							for (int i = 0; i < line.length(); i++) {
								char c = line.charAt(i);
								if (1 == s && c != ' ') {
									s = 2;
								}
								if (2 == s) {
									if (c == '(') {
										key = sb.toString();
										sb = new StringBuffer();
										s = 3;
									} else {
										sb.append(c);
									}
								} else if (3 == s) {
									if (c != ')') {
										sb.append(c);
									} else {
										value = sb.toString();
										sb = new StringBuffer();
										map.put(key, value);
									  //key = null; value = null;
										s = 1;
									}
								}
							}
						}
					} else {
						if (null != map) {							
							String qName = map.get("QUEUE").trim();
							int ipprocs = Integer.parseInt(map.get("IPPROCS"));
							if (! qName.startsWith("SYSTEM.") && 
									(qName.endsWith(".COMPLETE") || 
									qName.endsWith(".STATUS") || 
									qName.endsWith(".ERROR") || 
									qName.endsWith(".ERR")) &&
									ipprocs == 0)
								q = convertMapToPojo(in.getHostName(), in.getQueueManagerName(), map);
							map = null;
							//System.out.println(q);
						}
					}
					if (null != q) {
						qStatusList.add(q);
						q = null;
					}
				}
				if (null != map) {
					IIBMQQ q = convertMapToPojo(in.getHostName(), in.getQueueManagerName(), map);
					map = null;
					//System.out.println(q);
					if (null != q) {
						qStatusList.add(q);
						q = null;
					}
				}
			}
			qStatusList.hashCode();
		}
		return qStatusList;
	}
	
	private IIBMQM getQueueManagerInfo(IIBBroker in) throws JSchException, IOException, URISyntaxException {
		IIBMQM iibmqm = new IIBMQM();
		IIBSshExecHelper ssh = new IIBSshExecHelper(in);
		SshExecResponse resp = ssh.runCommand(RemoteCmdBuilder.getCurrentQMStatus(in.getQueueManagerName()));
		if (resp.getExecCode() == 0 ) { 
			Matcher mQMStatus = pQMStatus.matcher(resp.getStdOut());
			if(mQMStatus.find()) {
				in.setQueueManagerStatus(mQMStatus.group(1));
				iibmqm.setHostName(in.getHostName());
				iibmqm.setQueueManager(in.getQueueManagerName());
				iibmqm.setStatus(mQMStatus.group(1));
			}
		} else {
			logger.log(Level.WARNING, "Remote call failed with RC=" + resp.getExecCode());
		}
		return iibmqm;
	}
	
	public void saveQMMStatusesToDB(IIBMQM iibmqm) {
		  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		  Date date = new Date();  
		  System.out.println(formatter.format(date) + "\t" + iibmqm);
	}
	public static void saveMQQStatusesToDB(ArrayList<IIBMQQ> iibmqqList) {
		  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		  Date date = new Date();  
		  for (IIBMQQ iibmqq : iibmqqList) {
			  System.out.println(formatter.format(date) + "\t" + iibmqq);
		  }	
	}
			

}
