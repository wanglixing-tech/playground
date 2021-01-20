package crs.fcl.integration.main;

public class IIBMQM {
	private String hostName = null;
	private String queueManager = null;
	private String status = "UNKNOWN";
	
	public IIBMQM() {
	}

	public IIBMQM(String hostName, String queueManager, String status) {
		this.hostName = hostName;
		this.queueManager = queueManager;
		this.status = status;
	}

	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getQueueManager() {
		return queueManager;
	}
	public void setQueueManager(String queueManager) {
		this.queueManager = queueManager;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "IIBMQM [hostName=" + hostName + ", queueManager=" + queueManager + ", status=" + status + "]";
	}
}
