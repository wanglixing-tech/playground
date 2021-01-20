package crs.fcl.integration.model;

import java.util.List;


public class ItemTypeGroup {
	String id;
	String Name;
	List<Statement> statementList;
	List<Memo>	memoList;
	List<TextualNutrition>	textualNutritionList;
	List<StructuredNutrition>	StructuredNutritionList;
	List<NameLookups> nameLookupsList;
	List<NameTextLookups> nameTextLookupsList;
	List<NameTextItems> nameTextItemsList;
	List<LongTextItems> longTextItemsList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return Name;
	}
	public List<NameTextLookups> getNameTextLookupsList() {
		return nameTextLookupsList;
	}
	public void setNameTextLookupsList(List<NameTextLookups> nameTextLookupsList) {
		this.nameTextLookupsList = nameTextLookupsList;
	}
	
	public List<StructuredNutrition> getStructuredNutritionList() {
		return StructuredNutritionList;
	}
	public void setStructuredNutritionList(List<StructuredNutrition> structuredNutritionList) {
		StructuredNutritionList = structuredNutritionList;
	}
	public void setName(String name) {
		Name = name;
	}
	public List<Statement> getStatementList() {
		return statementList;
	}
	public void setStatementList(List<Statement> list) {
		this.statementList = list;
	}
	public List<Memo> getMemoList() {
		return memoList;
	}
	public void setMemoList(List<Memo> memoList) {
		this.memoList = memoList;
	}
	public List<NameLookups> getNameLookupsList() {
		return nameLookupsList;
	}
	public void setNameLookupsList(List<NameLookups> nameLookupsList) {
		this.nameLookupsList = nameLookupsList;
	}
	public List<LongTextItems> getLongTextItemsList() {
		return longTextItemsList;
	}
	public void setLongTextItemsList(List<LongTextItems> longTextItemsList) {
		this.longTextItemsList = longTextItemsList;
	}
	public List<TextualNutrition> getTextualNutritionList() {
		return textualNutritionList;
	}
	public void setTextualNutritionList(List<TextualNutrition> textualNutritionList) {
		this.textualNutritionList = textualNutritionList;
	}
	public List<NameTextItems> getNameTextItemsList() {
		return nameTextItemsList;
	}
	public void setNameTextItemsList(List<NameTextItems> nameTextItemsList) {
		this.nameTextItemsList = nameTextItemsList;
	}
}
