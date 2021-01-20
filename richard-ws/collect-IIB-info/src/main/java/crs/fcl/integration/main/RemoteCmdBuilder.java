package crs.fcl.integration.main;

public class RemoteCmdBuilder {
	/*
	 * Prefix of bash for invoking profile
	 */
	private static final String PREFIX = "/bin/bash -l -c '";
	private static final String SURFIX = "'";
	
	public static String getCurrentQMStatus(String qMgrName) {
		String cmd = String.format(PREFIX + "dspmq -m %s" + SURFIX, qMgrName);
		return cmd;
	}

	public static String getAllQueueDepthInfo(String qMgrName) {
		String cmd = String.format(PREFIX + "echo dis qlocal\\(*\\) curdepth maxdepth ipprocs where \\( curdepth GT 0 \\) | runmqsc %s " + SURFIX, qMgrName);
		//System.out.println("Command:" + cmd);
		return cmd;
	}

	public static String getFileStatus(String fileName) {
		/*
		 * ORIGINAL COMMAND WITH WHERE clause: display QLOCAL(*) where (curdepth EQ 0) | runmqsc QM
		 */
		String cmd = String.format(PREFIX + "ls -l | grep %s" + SURFIX , fileName);
		return cmd;
		
	}

	public static String getCurrentiNodeStatus(String iNodeName) {
		String cmd = String.format(PREFIX + "mqsilist | grep %s " + SURFIX , iNodeName);
		return cmd;	
	}

	public static String getCurrentiServersStatuses(String iNodeName) {
		String cmd = String.format(PREFIX + "mqsilist %s " + SURFIX, iNodeName);
		return cmd;
		
	}

	public static String getAppsStatuses(String iNodeName, String iServerName) {
		String cmd = String.format(PREFIX + "mqsilist %s -e %s " + SURFIX, iNodeName, iServerName);
		return cmd;
		
	}
	
	public static String getHttpsServerStatus(String url) {
		// String cmd = String.format(PREFIX + "curl https://sso.account.crs/eai/crseai/login.xhtml" );
		String cmd = String.format(PREFIX + "curl " + url + SURFIX);
		return cmd;
	}
	
	public static void main(String[] args) {
		System.out.println(getHttpsServerStatus("https://sso.account.crs/eai/crseai/login.xhtml"));
	}

}
