package crs.fcl.integration.main;

import java.util.ArrayList;

public class AceEnvironment {
	private String environmentName = null;
	private String hostName = null;
	private String proxyUser = null;
	private String sshKeyName = null;
	private String iibVersion = null;
	private String iibInstallPath = null;
	private ArrayList<IntegrationNode> iNodeList = null; 
	public String getEnvironmentName() {
		return environmentName;
	}
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public ArrayList<IntegrationNode> getiNodeList() {
		return iNodeList;
	}
	public void setiNodeList(ArrayList<IntegrationNode> iNodeList) {
		this.iNodeList = iNodeList;
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
}
