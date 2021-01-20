package crs.fcl.integration.main;

import java.util.ArrayList;

public class IIBEnvironments {
	private String environmentName = null;
	private ArrayList<IIBBroker> iNodeList = null; 
	
	public String getEnvironmentName() {
		return environmentName;
	}
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	public ArrayList<IIBBroker> getiNodeList() {
		return iNodeList;
	}
	public void setiNodeList(ArrayList<IIBBroker> iNodeList) {
		this.iNodeList = iNodeList;
	}
}
