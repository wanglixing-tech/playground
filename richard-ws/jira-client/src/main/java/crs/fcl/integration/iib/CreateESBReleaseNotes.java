package crs.fcl.integration.iib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.Lists;

public class CreateESBReleaseNotes {
	private static boolean quiet = false;
	//private static URI jiraServerUri = URI.create("http://localhost:8080");
	private static URI jiraServerUri;
	private static Hashtable<String, String> params = null;
	private static String defaultJiraServer = "http://localhost:8080";
	private static String jiraUser = "";
	private static String jiraPassword = "";
	private static String projectName = "";
	private static String releaseVersion = "";
	private static JiraRestClient client = null;
	private static FileWriter fw;
	private static StringWriter sw;

	public static void main(String[] args) throws URISyntaxException, JSONException, IOException {
		try {
			println("START");
			CreateESBReleaseNotes cesbrn = new CreateESBReleaseNotes();
			ArrayList<Issue> esbIssueList = new ArrayList<Issue>();
			JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			/**
			 * Option-1: Use JVM arguments -Dxxx=yyy ......(properties from command-line
			 */
			String jiraServer = System.getProperty("url");
			println("jiraServer=" + jiraServer);
			if (jiraServer != null) {
				jiraServerUri = URI.create(jiraServer);
			} else {
				jiraServerUri = URI.create(defaultJiraServer);
			}
			jiraUser = System.getProperty("user");
			println("jiraUser=" + jiraUser);
			jiraPassword = System.getProperty("pass");
			println("jiraPassword=" + jiraPassword);
			projectName = System.getProperty("project");
			println("projectName=" + projectName);
			releaseVersion = System.getProperty("version");
			println("releaseVersion=" + releaseVersion);
			/**
			 * Option-2: Use program arguments -xxx yyy ......
			 */
			// cesbrn.parseArgs(args);
			client = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUser, jiraPassword);
			SearchRestClient searchClient = client.getSearchClient();
			// CONSTRUIMOS LA PETICION

			// SearchResult result =
			// searchClient.searchJql("reporter=richarc.wang").claim();
			// SearchResult result = searchClient.searchJql("fixversion=1.1").claim();
			SearchResult result = searchClient.searchJql(buildJQLString(projectName, releaseVersion)).claim();
			for (Issue issue : result.getIssues()) {
				println(issue.getKey());
				esbIssueList.add(issue);
			}
			Collections.sort(esbIssueList, new SortByKey());
			cesbrn.createReport(esbIssueList);
			println("END");

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
			projectName = (String) params.get("-p");
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

	private int createReport(ArrayList<Issue> issueList) {
		try {
			VelocityEngine engine = new VelocityEngine();
			/*
			 * register this class as a logger with the Velocity singleton (NOTE: this would
			 * not work for the non-singleton method.)
			 */
			engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			engine.init();

			// Properties p = new Properties();
			// p.setProperty("file.resource.loader.path",
			// "C:\\Users\\richard.wang\\ws-java-velocity\\velocity-test\\src\\main\\resources\\templates");
			// engine.init( p );

			// Template template = engine.getTemplate("templates/userinfo.vm");
			Template template = engine.getTemplate("templates/esb-releasenotes-html.vm");

			VelocityContext vc = new VelocityContext();
			vc.put("jiraServerUri", jiraServerUri);
			vc.put("projectName", projectName);
			vc.put("releaseVersion", releaseVersion);
			vc.put("issueList", issueList);
			sw = new StringWriter();
			template.merge(vc, sw);
			File file = new File("C:\\Users\\richard.wang\\Desktop\\esb-release-note.html");
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(sw.toString());
			fileWriter.flush();
			fileWriter.close();
			// System.out.println(sw);

		} catch (

		Exception e) {
			System.out.println(e.getMessage());
			return 1;
		}
		return 0;
	}

	private static String buildJQLString(String projectName, String version) {
		String strJQL = "project=" + projectName + " AND status in (Resolved, Done, Closed) AND " + "fixversion=\""
				+ version + "\"";
		return strJQL;

	}
}

class SortByKey implements Comparator<Issue> {
	// Used for sorting in ascending order of
	// roll number
	public int compare(Issue a, Issue b) {
		return a.getKey().compareTo(b.getKey());
	}
}
