package crs.fcl.integration.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReorganizeProjectImportSequence {
	private static final Logger LOG = LoggerFactory.getLogger(ReorganizeProjectImportSequence.class);
	public static void main(String[] args) throws DocumentException {
		ArrayList<String> domainWorktrees = new ArrayList<String> (
				Arrays.asList("C:\\Users\\Richard.Wang\\WORKTREES\\CORE\\release\\core-1.0A",
								"C:\\Users\\Richard.Wang\\WORKTREES\\SYNERGY\\master-synergy"));

		ReorganizeProjectImportSequence iRPIS = new ReorganizeProjectImportSequence();	
		
		Stack<Set<File>> stackOfProjectSets = iRPIS.getWellOrganizedProjectList(domainWorktrees);
		/**
		 * display all re-ordered projects
		 */
		
		while (!stackOfProjectSets.empty()) {
			System.out.println("--------------------------------------------");
			Set<File> onePrjSet = stackOfProjectSets.pop();
			for (File onePrj : onePrjSet){
				System.out.println("Project Name:" + onePrj);
			}
		}
	}
	
	 /** All projects in all required working trees will be collected into a virtual
	 * Workspace, then they will be put into an hashSet to avoid any possible
	 * duplicated projects.
	 * 
	 * The the projectSet will become the base, then it will be recursively shuffled
	 * and every time the same level projects without dependencies will be grouped
	 * to a new projectSet, which will be managed in Java Stack.
	 * 	
	 * @param projectList
	 * @return
	 * @throws DocumentException
	 */
	public Stack<Set<File>> getWellOrganizedProjectList(ArrayList<String> projectList) throws DocumentException {
		Stack<Set<File>> stackOfProjectSets = new Stack<Set<File>>();

		//Convert all String projects to File Set
		Set<File> projectSet = new HashSet<File>();
		for (String project : projectList) {
			projectSet.add(new File(project));
		}
		
		// Initialize stackOfProjectSets by using all selected projects
		stackOfProjectSets.push(projectSet);
		shuffleProjects(projectSet, stackOfProjectSets);

		return stackOfProjectSets;
	}

	public void shuffleProjects(Set<File> projectSet, Stack<Set<File>> stackOfProjectSets) throws DocumentException {
		Set<File> subProjectSet = new HashSet<File>();
		for (File prj : projectSet) {
			//LOG.debug(prj.getAbsolutePath());
			if (prj.exists()) {
				Set<File> onePrjDependencies = uploadDotProjectFile(projectSet, prj);
				for (File onedp : onePrjDependencies) {
					//LOG.debug("\tDependencies:" + onedp);
				}
				subProjectSet.addAll(onePrjDependencies);
			} else {
				continue;
			}
		}

		if (subProjectSet.size() > 0) {
			// Remove same projects from projectSet
			for (File subProject : subProjectSet) {
				projectSet.remove(subProject);
			}
			stackOfProjectSets.push(subProjectSet);
			shuffleProjects(subProjectSet, stackOfProjectSets);
		}
		return;
	}

	public Set<File> uploadDotProjectFile(Set<File> projectSet, File targetProjectFile) throws DocumentException {
		File dotProjectFile = new File(targetProjectFile.getPath().toString() + File.separator + ".project");
		if (dotProjectFile.exists()) {
			Document dotProjectDoc = parseXML(dotProjectFile);
			String sXPath2targetNode = "//*[name()='project']";
			List<org.dom4j.Node> projectNodeList = dotProjectDoc.selectNodes(sXPath2targetNode);
			Set<File> dependentProjectSet = new HashSet<File>();
			for (org.dom4j.Node n : projectNodeList) {
				Element project = (Element) n;
				File targetReferredProject = findTargetProjectByName(projectSet, project.getText());
				if (targetReferredProject == null) {
					LOG.error(targetProjectFile + " is referring to a non-exist project.");
					System.exit(999);
				} else {
					dependentProjectSet.add(targetReferredProject);
				}
			}
			return dependentProjectSet;
		} else {
			LOG.warn("No .project exist in project:" + dotProjectFile);
			return new HashSet<File>();
		}
	}

	private Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	private File findTargetProjectByName(Set<File> projectSet, String projectName) {
		for (File project : projectSet) {
			if (project.getName().equals(projectName)) {
				// System.out.println("Full path of " + projectName + project);
				return project;
			}
		}
		LOG.error("The project '" + projectName
				+ "' is being referred by other project but does not exist in this domain, please fix this problem and try again.");
		return null;
	}
}