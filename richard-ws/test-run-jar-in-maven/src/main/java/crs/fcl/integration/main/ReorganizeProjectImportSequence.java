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

public class ReorganizeProjectImportSequence {
	private static List<File> projectList = new ArrayList<File>();
	private static Stack<Set<File>> stackOfProjectSets = new Stack<Set<File>>();
	private static ArrayList<String> virtualWorkSpace = new ArrayList<String>();
	private static String wtHome1 = "C:\\Users\\Richard.Wang\\WORKTREES\\CORE\\release\\core-1.0A";
	private static String wtHome2 = "C:\\Users\\Richard.Wang\\WORKTREES\\SYNERGY\\master-synergy";

	public static void main(String[] args) throws DocumentException {
		Set<File> projectSet = new HashSet<File>();

		virtualWorkSpace.add(wtHome1);
		virtualWorkSpace.add(wtHome2);

		/**
		 * All projects in all required working trees will be collected into a virtual
		 * Workspace, then they will be put into an hashSet to avoid any possible
		 * duplicated projects.
		 * 
		 * The the projectSet will become the base, then it will be recursively shuffled
		 * and every time the same level projects without dependencies will be grouped
		 * to a new projectSet, which will be managed in Java Stack.
		 * 
		 */
		virtualWorkSpace.forEach(wt -> {
			File[] directories = new File(wt).listFiles(File::isDirectory);
			projectList.addAll(Arrays.asList(directories));
			projectList.forEach(prj -> {
				projectSet.add(prj);
			});
		});

		int group = 0;
		stackOfProjectSets.push(projectSet);
		shuffleProjects(projectSet, group);

		/**
		 * display all re-ordered projects
		 */
		while (!stackOfProjectSets.empty()) {
			System.out.println("--------------------------------------------");
			Set<File> onePrj = stackOfProjectSets.pop();
			onePrj.forEach(prj -> {
				System.out.println("Project Name:" + prj);
			});
		}
	}

	public static void shuffleProjects(Set<File> projectSet, int group) throws DocumentException {
		Set<File> subProjectSet = new HashSet<File>();
		for (File prj : projectSet) {
			if (prj == null) {
				System.out.println("null depenedncies existing in " + projectSet.toString() + "," + group);
				return;
			} else {
				System.out.println(prj);
				if (prj.exists()) {
					Set<File> onePrjDependencies = uploadDotProjectFile(prj);
					for (File onedp : onePrjDependencies) {
						System.out.println("\tDependency:" + onedp);
					}
					subProjectSet.addAll(onePrjDependencies);
				} else {
					continue;
				}
			}
		}

		if (subProjectSet.size() > 0) {
			// Remove same projects from projectSet
			for (File subProject : subProjectSet) {
				projectSet.remove(subProject);
			}
			stackOfProjectSets.push(subProjectSet);
			shuffleProjects(subProjectSet, group + 1);
		}
		return;
	}

	public static Set<File> uploadDotProjectFile(File targetProjectFile) throws DocumentException {
		File dotProjectFile = new File(targetProjectFile.getPath().toString() + File.separator + ".project");
		if (dotProjectFile.exists()) {
			Document dotProjectDoc = parseXML(dotProjectFile);
			String sXPath2targetNode = "//*[name()='project']";
			List<org.dom4j.Node> projectNodeList = dotProjectDoc.selectNodes(sXPath2targetNode);
			Set<File> dependentProjectSet = new HashSet<File>();
			for (org.dom4j.Node n : projectNodeList) {
				Element project = (Element) n;
				File targetReferredProject = findTargetProjectByName(project.getText());
				if (targetReferredProject == null) {
					System.out.println(targetProjectFile + " is referring to a non-exist project.");
					System.exit(999);
				} else {
					dependentProjectSet.add(targetReferredProject);
				}
			}
			return dependentProjectSet;
		} else {
			System.out.println("No .project exist in project:" + dotProjectFile);
			return new HashSet<File>();
		}
	}

	private static Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	private static File findTargetProjectByName(String projectName) {
		for (File project : projectList) {
			;
			if (project.getName().equals(projectName)) {
				// System.out.println("Full path of " + projectName + project);
				return project;
			}
		}
		System.out.println("The project '" + projectName
				+ "' is being referred by other project but does not exist in this domain, please fix this problem and try again.");
		return null;
	}
}
