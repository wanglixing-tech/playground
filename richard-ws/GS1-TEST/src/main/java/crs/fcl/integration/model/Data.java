package crs.fcl.integration.model;

import java.util.List;

public class Data {
	List<Language> languageList;

	public List<Language> getLanguageList() {
		return languageList;
	}

	public void setLanguageList(List<Language> languageList) {
		this.languageList = languageList;
	}
	
	public Language getLanguageByCode(String code) {
		Language targetLanguage = null;
		for (Language l : languageList) {
			if (l.getCode().equals("en-CA")) {
				targetLanguage = l;
				break;
			}
		}
		return targetLanguage;
	}
}
