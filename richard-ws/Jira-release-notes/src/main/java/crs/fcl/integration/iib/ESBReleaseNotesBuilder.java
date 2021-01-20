package crs.fcl.integration.iib;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class ESBReleaseNotesBuilder {
	private static boolean quiet = false;
	// private static URI jiraServerUri = URI.create("http://localhost:8080");
	private static URI jiraServerUri;
	private static Hashtable<String, String> params = null;
	private static String defaultJiraServer = "https://fclweb.jira.com";
	private static String path2ReleaseNotes = "";
	private static String jiraUser = "";
	private static String jiraPassword = "";
	private static String projectKey = "";
	private static String projectName = "";
	private static String releaseVersion = "";
	private static String statusIds = "Resolved, Done, Closed";
	private static JiraRestClient client = null;
	private static SearchRestClient searchClient;

	public static void main(String[] args) throws URISyntaxException, IOException, IllegalArgumentException {
		try {
			println("<START>");
			ESBReleaseNotes ern = new ESBReleaseNotes();
			HtmlNotesCreator hnc = new HtmlNotesCreator();
			ArrayList<Issue> esbIssueList = new ArrayList<Issue>();
			JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

			String separator = System.getProperty("file.separator");

			/**
			 * Option-1: Use JVM arguments -Dxxx=yyy ......(properties from command-line
			 */
			String jiraServer = System.getProperty("url");
			if (jiraServer != null) {
				jiraServerUri = URI.create(jiraServer);
			} else {
				jiraServerUri = URI.create(defaultJiraServer);
			}
			println("jiraServer=" + jiraServerUri);
			ern.setJiraServerURI(jiraServerUri);
			jiraUser = System.getProperty("user");
			println("jiraUser=" + jiraUser);
			jiraPassword = System.getProperty("pass");
			println("jiraPassword=" + jiraPassword);

			// For searching, can be project name or project key
			projectKey = System.getProperty("project");
			println("projectKey=" + projectKey);

			releaseVersion = System.getProperty("version");
			println("releaseVersion=" + releaseVersion);
			ern.setFixVersion(releaseVersion);

			/**
			 * Option-2: Use program arguments -xxx yyy ......
			 */
			// cesbrn.parseArgs(args);
			if (isJiraAuthenticationConfigured()) {
				client = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUser, jiraPassword);
				searchClient = client.getSearchClient();
			} else {
				throw new IllegalArgumentException();
			}
			// CONSTRUIMOS LA PETICION

			// SearchResult result =
			// searchClient.searchJql("reporter=richarc.wang").claim();
			// SearchResult result = searchClient.searchJql("fixversion=1.1").claim();
			String jqlQuery = new JqlQueryBuilderImpl().urlEncode(false).project(projectKey).fixVersion(releaseVersion)
					.statusIds(statusIds).build();

			SearchResult result = searchClient.searchJql(jqlQuery).claim();
			for (Issue issue : result.getIssues()) {
				//println(issue.getKey());
				projectName = issue.getProject().getName();
				projectKey = issue.getProject().getKey();
				esbIssueList.add(issue);
			}
			ern.setProjectKey(projectKey);
			ern.setProjectDesc(projectName);
			Collections.sort(esbIssueList, new SortByIssueTypeAndId());
			ern.setIssueList(esbIssueList);
			path2ReleaseNotes = System.getProperty("path");
			if (path2ReleaseNotes == null) {
				path2ReleaseNotes = System.getProperty("user.home") + separator + projectKey.replace(" ", "-") + "["
						+ releaseVersion.replace(" ", "-") + "].html";
			}

			hnc.createReport(path2ReleaseNotes, ern);
			println("<END>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void println(Object o) {
		if (!quiet) {
			System.out.println(o);
		}
	}

	@SuppressWarnings("unused")
	private void parseArgs(String[] args) throws IllegalArgumentException {
		params = new Hashtable<String, String>();
		if (args.length > 0 && (args.length % 2) == 0) {
			for (int i = 0; i < args.length; i += 2) {
				params.put(args[i], args[i + 1]);
			}
		} else {
			throw new IllegalArgumentException();
		}

		if (allParamsPresent()) {
			jiraUser = (String) params.get("-user");
			jiraPassword = (String) params.get("-pw");
			projectKey = (String) params.get("-p");
			releaseVersion = (String) params.get("-v");

		} else {
			throw new IllegalArgumentException();
		}
	}

	private boolean allParamsPresent() {
		boolean b = params.containsKey("-user") && params.containsKey("-pw") && params.containsKey("-p")
				&& params.containsKey("-v");

		return b;
	}

	protected static boolean isJiraAuthenticationConfigured() {
		return (jiraUser != null) && (jiraUser.length() > 0) && (jiraPassword != null);
	}
}

class SortByIssueTypeAndId implements Comparator<Issue> {
	// Used for sorting in ascending order of Issues in one release number

	public int compare(Issue a, Issue b) {
		int result = 0;
		if (a.getIssueType().getId() < b.getIssueType().getId()) {
			result = -1;
		} else {
			if (a.getIssueType().getId() > b.getIssueType().getId()) {
				result = 1;
			} else {
				result = 0;
			}
		}

		if (result == 0) {
			result = a.getKey().compareTo(b.getKey());
		}
		return result;
	}
}