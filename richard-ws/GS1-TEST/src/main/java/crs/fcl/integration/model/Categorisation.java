package crs.fcl.integration.model;

import java.util.List;

public class Categorisation {
	String Scheme;
	List<CategoryLevel> LevelList;
	public String getScheme() {
		return Scheme;
	}
	public void setScheme(String scheme) {
		Scheme = scheme;
	}
	public List<CategoryLevel> getLevelList() {
		return LevelList;
	}
	public void setLevelList(List<CategoryLevel> levelList) {
		LevelList = levelList;
	}
}
