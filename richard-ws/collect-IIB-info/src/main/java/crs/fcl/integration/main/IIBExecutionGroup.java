package crs.fcl.integration.main;

public class IIBExecutionGroup {
	private String compondKey = null;
	private String hostName = null;
	private String iNodeName = null;
	private String iServerName = null;
	private String iServerStatus = null;
	private String workDir = "";

	public String getCompondKey() {
		return compondKey;
	}
	public void setCompondKey(String compondKey) {
		this.compondKey = compondKey;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getiNodeName() {
		return iNodeName;
	}
	public void setiNodeName(String iNodeName) {
		this.iNodeName = iNodeName;
	}
	public IIBExecutionGroup() {
	}
	public String getiServerName() {
		return iServerName;
	}

	public void setiServerName(String iServerName) {
		this.iServerName = iServerName;
	}

	public String getiServerStatus() {
		return iServerStatus;
	}

	public void setiServerStatus(String iServerStatus) {
		this.iServerStatus = iServerStatus;
	}
	public String getWorkDir() {
		return workDir;
	}
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}
	@Override
	public String toString() {
		return "IntegrationServer [hostName=" + hostName + ", iNodeName=" + iNodeName + ", iServerName=" + iServerName
				+ ", iServerStatus=" + iServerStatus + "]";
	}
	
}
