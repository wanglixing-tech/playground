package crs.fcl.integration.iib;

import java.util.List;
import java.util.Map;

public class IIBComponent {
	private String name;
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String remoteBranch;
    private String localBranch;
    private String localPath;
    private String mvnPom;
    private String mvnPomJava;
    @SuppressWarnings("rawtypes")
	private List<Map> applications;
	public String getRemoteBranch() {
		return remoteBranch;
	}
	public void setRemoteBranch(String remoteBranch) {
		this.remoteBranch = remoteBranch;
	}
	public String getLocalBranch() {
		return localBranch;
	}
	public void setLocalBranch(String localBranch) {
		this.localBranch = localBranch;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
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
	@SuppressWarnings("rawtypes")
	public List<Map> getApplications() {
		return applications;
	}
	@SuppressWarnings("rawtypes")
	public void setApplications(List<Map> applications) {
		this.applications = applications;
	}
}
