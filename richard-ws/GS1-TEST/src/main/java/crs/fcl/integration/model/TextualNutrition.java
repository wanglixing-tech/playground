package crs.fcl.integration.model;

import java.util.List;

public class TextualNutrition {
	String id;
	String name;
	List<String> headings;
	List<TextualNutritionNutrient> textualNutritionNutrientList;
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
	public List<String> getHeadings() {
		return headings;
	}
	public void setHeadings(List<String> headings) {
		this.headings = headings;
	}
	public List<TextualNutritionNutrient> getTextualNutritionNutrientList() {
		return textualNutritionNutrientList;
	}
	public void setTextualNutritionNutrientList(List<TextualNutritionNutrient> textualNutritionNutrientList) {
		this.textualNutritionNutrientList = textualNutritionNutrientList;
	}
}
