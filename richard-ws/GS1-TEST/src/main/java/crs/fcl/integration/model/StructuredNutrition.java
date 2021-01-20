package crs.fcl.integration.model;

import java.util.ArrayList;
import java.util.List;

public class StructuredNutrition {
	String id;
	String name;
	ArrayList<ValueGroupDefinition> ValueGroupDefinitionList;
	ArrayList<Nutrient> nutrientList;
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
	public ArrayList<Nutrient> getNutrientList() {
		return nutrientList;
	}
	public void setNutrientList(List<Nutrient> list) {
		this.nutrientList = (ArrayList<Nutrient>) list;
	}
	public ArrayList<ValueGroupDefinition> getValueGroupDefinitionList() {
		return ValueGroupDefinitionList;
	}
	public void setValueGroupDefinitionList(List<ValueGroupDefinition> list) {
		ValueGroupDefinitionList = (ArrayList<ValueGroupDefinition>) list;
	}
}
