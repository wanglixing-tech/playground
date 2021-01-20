package crs.fcl.integration.model;

import java.util.ArrayList;

public class Language {
	String Code;
	String Source;
	String GroupingSetId;
	String GroupingSetName;
	String description;
	String categoryScheme;
	Categorisation categorisation;
	ItemTypeGroup itemTypeGroup;
	
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getSource() {
		return Source;
	}
	public void setSource(String source) {
		Source = source;
	}
	public String getGroupingSetId() {
		return GroupingSetId;
	}
	public void setGroupingSetId(String groupingSetId) {
		GroupingSetId = groupingSetId;
	}
	public String getGroupingSetName() {
		return GroupingSetName;
	}
	public void setGroupingSetName(String groupingSetName) {
		GroupingSetName = groupingSetName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategoryScheme() {
		return categoryScheme;
	}
	public void setCategoryScheme(String categoryScheme) {
		this.categoryScheme = categoryScheme;
	}
	public Categorisation getCategorisation() {
		return categorisation;
	}
	public void setCategorisation(Categorisation categorisation) {
		this.categorisation = categorisation;
	}
	public ItemTypeGroup getItemTypeGroup() {
		return itemTypeGroup;
	}
	public void setItemTypeGroup(ItemTypeGroup itemTypeGroup) {
		this.itemTypeGroup = itemTypeGroup;
	}
}
