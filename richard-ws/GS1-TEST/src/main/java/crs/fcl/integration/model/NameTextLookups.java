package crs.fcl.integration.model;

import java.util.List;

import crs.fcl.integration.model.NameValueText;

public class NameTextLookups {
	String id;
	String name;
	List<NameValueText> nameValueTextList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<NameValueText> getNameValueTextList() {
		return nameValueTextList;
	}
	public void setNameValueTextList(List<NameValueText> nameValueTextList) {
		this.nameValueTextList = nameValueTextList;
	}
	
}
