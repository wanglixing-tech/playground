package crs.fcl.integration.iib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

public class TestLoadDocs {
	String domainName = null;

	public static void main(String[] args) throws IOException {
		TestLoadDocs tld = new TestLoadDocs();
		Application myApp = tld.testLoadManyDocuments(
				new File("C:\\Users\\Richard.Wang\\WS-HOME\\ws-yaml\\parse-yaml-configuration\\release-tronia-3.0.yml"),
				"DBLogger");
		if (myApp != null) {
			System.out.println("Target applcaition's appName is " + myApp.getAppName());
			System.out.println("Target applcaition's category is " + myApp.getCategory());
			System.out.println("Target applcaition's MAVEN pom is " + myApp.getMvnPom());
			System.out.println("Target applcaition's MAVEN pom for Jave is " + myApp.getMvnPomJava());
			System.out.println("Target applcaition's previous version is " + myApp.getPreviousVersion());
			System.out.println("Target applcaition's current version is " + myApp.getCurrentVersion());
		}else {
			System.out.println("No application can be found from this domain");
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public Application testLoadManyDocuments(File inf, String appName) throws IOException {
		Application targetApp = new Application();
		int gotIt = 0;
		InputStream input = new FileInputStream(inf);
		Yaml yaml = new Yaml();
		Iterable<Object> itr = yaml.loadAll(input);
		Collection<Object> col = iterableToCollection(itr);
		for (Object c1 : col) {
			System.out.println("Loaded object type:" + c1.getClass());
			if (c1 instanceof java.util.LinkedHashMap) {
				// Get Domain Name
				System.out.println("Domain Name: " + ((java.util.LinkedHashMap) c1).get("domainName"));
				targetApp.setDomainName((String) ((java.util.LinkedHashMap) c1).get("domainName"));
				Object allApps = ((java.util.LinkedHashMap) c1).get("applications");
				//
				if (allApps instanceof java.util.ArrayList) {
					List appList = (java.util.ArrayList) allApps;
					for (Object oneApp : appList) {
						//System.out.println(oneApp);
						System.out.println("Loaded object type:" + oneApp.getClass());
						String myAppName = (String)((LinkedHashMap) oneApp).get("appName");
						if (myAppName.equals(appName)) {
							gotIt = 1;
							targetApp.setAppName((String) ((LinkedHashMap) oneApp).get("appName"));
							targetApp.setCategory((String) ((LinkedHashMap) oneApp).get("category"));
							targetApp.setMvnPom((String) ((LinkedHashMap) oneApp).get("mvnPom"));
							targetApp.setMvnPomJava((String) ((LinkedHashMap) oneApp).get("mvnPomJava"));
							targetApp.setPreviousVersion((String) ((LinkedHashMap) oneApp).get("previousVersion"));
							targetApp.setCurrentVersion((String) ((LinkedHashMap) oneApp).get("currentVersion"));
							break;
						}
					}
				}
			}
			if (gotIt == 1) break;
		}
		if (gotIt == 0) {
			return null;
		}
		return targetApp;
	}

	public static <T> Collection<T> iterableToCollection(Iterable<T> iterable) {
		Collection<T> collection = new ArrayList<>();
		iterable.forEach(collection::add);
		return collection;
	}
}
