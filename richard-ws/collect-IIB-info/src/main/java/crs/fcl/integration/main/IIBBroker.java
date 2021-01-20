package crs.fcl.integration.main;

import java.util.ArrayList;

public class IIBBroker {
	private String compondKey = null;
	private String hostName = null;
	private int port = 22;
	private String proxyUser = null;
	private String sshKeyName = null;
	private String password = null;
	private String iibVersion = null;
	private String iibInstallPath = null;
	private String iNodeName = null;
	private String iNodeStatus = null;
	private String queueManagerName = null;	
	private String queueManagerStatus = null;	
	private String workDir = null;

	
	public String getCompondKey() {
		return compondKey;
	}
	public void setCompondKey(String compondKey) {
		this.compondKey = compondKey;
	}
	public String getiNodeName() {
		return iNodeName;
	}
	public void setiNodeName(String iNodeName) {
		this.iNodeName = iNodeName;
	}
	public String getiNodeStatus() {
		return iNodeStatus;
	}
	public void setiNodeStatus(String iNodeStatus) {
		this.iNodeStatus = iNodeStatus;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getWorkDir() {
		return workDir;
	}
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}	
	public String getSshKeyName() {
		return sshKeyName;
	}
	public void setSshKeyName(String sshKeyName) {
		this.sshKeyName = sshKeyName;
	}
	public String getProxyUser() {
		return proxyUser;
	}
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}
	public String getIibVersion() {
		return iibVersion;
	}
	public void setIibVersion(String iibVersion) {
		this.iibVersion = iibVersion;
	}
	public String getIibInstallPath() {
		return iibInstallPath;
	}
	public void setIibInstallPath(String iibInstallPath) {
		this.iibInstallPath = iibInstallPath;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQueueManagerName() {
		return queueManagerName;
	}

	public void setQueueManagerName(String queueManagerName) {
		this.queueManagerName = queueManagerName;
	}
	public String getQueueManagerStatus() {
		return queueManagerStatus;
	}
	public void setQueueManagerStatus(String queueManagerStatus) {
		this.queueManagerStatus = queueManagerStatus;
	}
	@Override
	public String toString() {
		return "IntegrationNode [hostName=" + hostName + ", iNodeName=" + iNodeName + ", iNodeStatus=" + iNodeStatus + "]";
	}

}
