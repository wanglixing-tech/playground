package crs.fcl.integration.main;

import java.util.ArrayList;

public class IntegrationNode {
	private String nodeName = null;
	private ArrayList<IntegrationServer> iServerList = null; 
	private ArrayList<String> requiredResourceList = null;
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public ArrayList<IntegrationServer> getiServerList() {
		return iServerList;
	}
	public void setiServerList(ArrayList<IntegrationServer> iServerList) {
		this.iServerList = iServerList;
	}
	public ArrayList<String> getRequiredResourceList() {
		return requiredResourceList;
	}
	public void setRequiredResourceList(ArrayList<String> requiredResourceList) {
		this.requiredResourceList = requiredResourceList;
	}
}
