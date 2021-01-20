package crs.fcl.integration.main;

public class IIBApplication {
	private String compondKey;
	private String hostName;
	private String iNodeName;
	private String iServerName;
	private String appName;
	private String appStatus;

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
	public String getiServerName() {
		return iServerName;
	}
	public void setiServerName(String iServerName) {
		this.iServerName = iServerName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppStatus() {
		return appStatus;
	}
	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}
	
	@Override
	public String toString() {
		return "Application [hostName=" + hostName + ", iNodeName=" + iNodeName + ", iServerName=" + iServerName
				+ ", appName=" + appName + ", appStatus=" + appStatus + "]";
	}

}
