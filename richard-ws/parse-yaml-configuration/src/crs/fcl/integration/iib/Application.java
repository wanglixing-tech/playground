package crs.fcl.integration.iib;

public class Application {
	private String domainName;
	private String appName;
	private String category;
	private String mvnPom;
	private String mvnPomJava;
	private String previousVersion;
	private String currentVersion;

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMvnPom() {
		return mvnPom;
	}

	public void setMvnPom(String mvnPom) {
		this.mvnPom = mvnPom;
	}

	public String getMvnPomJava() {
		return mvnPomJava;
	}

	public void setMvnPomJava(String mvnPomJava) {
		this.mvnPomJava = mvnPomJava;
	}

	public String getPreviousVersion() {
		return previousVersion;
	}

	public void setPreviousVersion(String previousVersion) {
		this.previousVersion = previousVersion;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

}
