package crs.fcl.integration.iib;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

public class GeneratePom {
	private static final String targetFileName = ".project";
	private static final String superPomFileName = "iib-mvn-super-pom.xml";
	private static final String pomNewFileName = "test-pom.xml";
	private static String branchName = null;
	private static String bootStrapHome = null;
	private static Model model = null;

	public static void main(String[] args) throws Exception {
		List<String> projectList = new ArrayList<String>();
		String cwd = "C:\\IIB\\master-core";
		//String cwd = System.getProperty("user.dir");
		System.out.println("Current working directory : " + cwd);
		
		// Deal with System Properties from JVM Arguments
		branchName = System.getProperty("branch");
		bootStrapHome = System.getProperty("bootStrapHome");
		if (branchName == null ) {
			System.out.println("BitBucket branch name must be provided.");
			System.exit(1);
		}
		
		if (bootStrapHome == null ) {
			System.out.println("Bootstrap's home directory must be specified.");
			System.exit(1);
		}

		// Convert String Array to List
		List<String> list = Arrays.asList(args);

		// Deal with Program Arguments
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals("-pl") || list.get(i).equals("--projects")) {
				String[] items = list.get(i + 1).split(",");
				projectList = Arrays.asList(items);
				break;
			}
		}

		if (projectList.isEmpty()) {
			System.out.println(
					"No projects specified as command-line arguments, try to collect projects from current working directory......");
			List<String> folderList = findFoldersInDirectory(cwd);
			for (String oneFolder : folderList) {
				java.nio.file.Path path = java.nio.file.Paths.get(cwd, oneFolder, targetFileName);
				boolean fileExists = java.nio.file.Files.exists(path);
				if (fileExists) {
					try {
						StaXParser pjparser = new StaXParser();
						String projectName = pjparser.parser(path.toFile());
						if (projectName != null)
							projectList.add(projectName);
					} catch (FileNotFoundException | XMLStreamException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					continue;
				}
			}
		} 
		
		// Create a branch pom.xml with super pom.xml as its parent
		model = new Model();
		File superPomFile = new File(bootStrapHome + File.separator + superPomFileName);
		ParentData parentPom = new ParentData(superPomFile);
		parentPom.write(model);
		POMData branchPom = new POMData(model, "crs.fcl.integration.iib", branchName, "0.0.1-SNAPSHOT" );
		branchPom.parseMavenPomModelToXmlFile(pomNewFileName, model);
	}

	public static List<String> findFoldersInDirectory(String directoryPath) {
		File directory = new File(directoryPath);

		FileFilter directoryFileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		File[] directoryListAsFile = directory.listFiles(directoryFileFilter);
		List<String> foldersInDirectory = new ArrayList<String>(directoryListAsFile.length);
		for (File directoryAsFile : directoryListAsFile) {
			foldersInDirectory.add(directoryAsFile.getName());
		}

		return foldersInDirectory;
	}
}
