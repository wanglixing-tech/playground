package crs.fcl.integration.iib;

import java.net.URI;
import java.util.ArrayList;
import com.atlassian.jira.rest.client.api.domain.Issue;

public class ESBReleaseNotes {
	private URI jiraServerURI;
	private String projectKey;
	private String projectDesc;
	private String fixVersion;
	private String releaseDate;
	private ArrayList<Issue> issueList;

	public ESBReleaseNotes() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the jiraServerURI
	 */
	public URI getJiraServerURI() {
		return jiraServerURI;
	}

	/**
	 * @param jiraServerURI
	 *            the jiraServerURI to set
	 */
	public void setJiraServerURI(URI jiraServerURI) {
		this.jiraServerURI = jiraServerURI;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @param projectId
	 *            the projectId to set
	 */
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	/**
	 * @return the projectDesc
	 */
	public String getProjectDesc() {
		return projectDesc;
	}

	/**
	 * @param projectDesc
	 *            the projectDesc to set
	 */
	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	/**
	 * @return the fixVersion
	 */
	public String getFixVersion() {
		return fixVersion;
	}

	/**
	 * @param fixVersion
	 *            the fixVersion to set
	 */
	public void setFixVersion(String fixVersion) {
		this.fixVersion = fixVersion;
	}

	/**
	 * @return the releaseDate
	 */
	public String getReleaseDate() {
		return releaseDate;
	}

	/**
	 * @param releaseDate
	 *            the releaseDate to set
	 */
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	/**
	 * @return the issueList
	 */
	public ArrayList<Issue> getIssueList() {
		return issueList;
	}

	/**
	 * @param esbIssueList
	 *            the issueList to set
	 */
	public void setIssueList(ArrayList<Issue> esbIssueList) {
		this.issueList = esbIssueList;
	}
}
