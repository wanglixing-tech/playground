package crs.fcl.integration.iib;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Version;

public class HtmlNotesCreator {

	public HtmlNotesCreator() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int createReport(String path2NotesFile, ESBReleaseNotes ern) {
		try {
			VelocityEngine engine = new VelocityEngine();
			/*
			 * register this class as a logger with the Velocity singleton (NOTE: this would
			 * not work for the non-singleton method.)
			 */
			engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			engine.init();
			Template template = engine.getTemplate("templates/esb-releasenotes.vm");

			VelocityContext vc = new VelocityContext();
			//System.out.println(ern.getFixVersion());
			//System.out.println(ern.getProjectDesc());
			//System.out.println(ern.getProjectKey());
			//ArrayList<Issue> eil = ern.getIssueList();
			//for (Issue i : eil) {
				//System.out.println(i.getKey());
				//System.out.println(i.getCreationDate());
				//System.out.println(i.getIssueType().getName());
				//Iterable<Version> fixVersions = i.getFixVersions();
				//for (Version fv : fixVersions) {
				//	System.out.println(fv.getName());
				//	System.out.println(fv.isReleased() ? "Released" : "Not Released");
				//}
			//}
			vc.put("releaseNotes", ern);
			StringWriter sw = new StringWriter();
			template.merge(vc, sw);
			File file = new File(path2NotesFile);
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

}
