package com.fcl.esb;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.BasicWatchers;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.Lists;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MyJiraRESTClient {
	/**
	 * A sample code how to use JRJC library
	 *
	 * @since v0.1
	 */
	private static URI jiraServerUri = URI.create("http://localhost:8080/");
	private static boolean quiet = false;

	public static void main(String[] args) throws URISyntaxException, JSONException, IOException {
		parseArgs(args);

		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri,
				"richarc.wang", "Welcome1");
		try {
			//final int buildNumber = restClient.getMetadataClient().getServerInfo().claim().getBuildNumber();

			// first let's get and print all visible projects (only jira4.3+)
			//if (buildNumber >= ServerVersionConstants.BN_JIRA_4_3) {
			//	final Iterable<BasicProject> allProjects = restClient.getProjectClient().getAllProjects().claim();
			//	for (BasicProject project : allProjects) {
			//		println(project);
			//	}
			//}

			// let's now print all issues matching a JQL string (here: all assigned issues)
			//if (buildNumber >= ServerVersionConstants.BN_JIRA_4_3) {
			//	final SearchResult searchResult = restClient.getSearchClient().searchJql("assignee is not EMPTY")
			//			.claim();
			//	for (BasicIssue issue : searchResult.getIssues()) {
			//		println(issue.getKey());
			//	}
			//}

			final Issue issue = restClient.getIssueClient().getIssue("MEP-1").claim();

			println(issue);
			
			// now let's vote for it
			//restClient.getIssueClient().vote(issue.getVotesUri()).claim();

			// now let's watch it
			//final BasicWatchers watchers = issue.getWatchers();
			//if (watchers != null) {
			//	restClient.getIssueClient().watch(watchers.getSelf()).claim();
			//}

			// now let's start progress on this issue
			//final Iterable<Transition> transitions = restClient.getIssueClient()
			//		.getTransitions(issue.getTransitionsUri()).claim();
			//final Transition startProgressTransition = getTransitionByName(transitions, "Start Progress");
			//restClient.getIssueClient()
			//		.transition(issue.getTransitionsUri(), new TransitionInput(startProgressTransition.getId()))
			//		.claim();

			// and now let's resolve it as Incomplete
			//final Transition resolveIssueTransition = getTransitionByName(transitions, "Resolve Issue");
			//final Collection<FieldInput> fieldInputs;

			// Starting from JIRA 5, fields are handled in different way -
			//if (buildNumber > ServerVersionConstants.BN_JIRA_5) {
			//	fieldInputs = Arrays
			//			.asList(new FieldInput("resolution", ComplexIssueInputFieldValue.with("name", "Incomplete")));
			//} else {
			//	fieldInputs = Arrays.asList(new FieldInput("resolution", "Incomplete"));
			//}
			//final TransitionInput transitionInput = new TransitionInput(resolveIssueTransition.getId(), fieldInputs,
			//		Comment.valueOf("My comment"));
			//restClient.getIssueClient().transition(issue.getTransitionsUri(), transitionInput).claim();
		} finally {
			restClient.close();
		}
	}

	private static void println(Object o) {
		if (!quiet) {
			System.out.println(o);
		}
	}

	private static void parseArgs(String[] argsArray) throws URISyntaxException {
		final List<String> args = Lists.newArrayList(argsArray);
		if (args.contains("-q")) {
			quiet = true;
			args.remove(args.indexOf("-q"));
		}

		if (!args.isEmpty()) {
			jiraServerUri = new URI(args.get(0));
		}
	}

	private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) {
		for (Transition transition : transitions) {
			if (transition.getName().equals(transitionName)) {
				return transition;
			}
		}
		return null;
	}
}
