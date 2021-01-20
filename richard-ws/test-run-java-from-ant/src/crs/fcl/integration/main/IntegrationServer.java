package crs.fcl.integration.main;

import java.util.ArrayList;

public class IntegrationServer {
	private String integrationServer = null;
	private String workDir = "";
	private ArrayList<String> resourceList = null;
	public String getIntegrationServer() {
		return integrationServer;
	}
	public void setIntegrationServer(String integrationServer) {
		this.integrationServer = integrationServer;
	}
	public ArrayList<String> getResourceList() {
		return resourceList;
	}
	public void setResourceList(ArrayList<String> resourceList) {
		this.resourceList = resourceList;
	}
	public String getWorkDir() {
		return workDir;
	}
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}
}
