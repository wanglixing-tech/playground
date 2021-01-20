package crs.fcl.integration.model;

import java.util.List;

public class NameTextItems {
	String id;
	String name;
	List<NameText> nameTextList;
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
	public List<NameText> getNameTextList() {
		return nameTextList;
	}
	public void setNameTextList(List<NameText> list) {
		this.nameTextList = list;
	}

}
