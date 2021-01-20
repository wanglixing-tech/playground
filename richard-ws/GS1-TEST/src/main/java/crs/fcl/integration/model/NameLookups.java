package crs.fcl.integration.model;

import java.util.List;

public class NameLookups {
	String id;
	String Name;
	List<NameValue> nameValueList;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public List<NameValue> getNameValueList() {
		return nameValueList;
	}
	public void setNameValueList(List<NameValue> nameValueList) {
		this.nameValueList = nameValueList;
	}

}
