package crs.fcl.integration.iib;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class POMData {
    private String groupId;
    private final String artifactId;
    private String version;
	private String packaging;
    private String modules;
    
    public String getPackaging() {
		return packaging;
	}
    
	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getModules() {
		return modules;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setVersion(String version) {
		this.version = version;
	}

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    private final String xml = null;

    public String getXml() {
        return xml;
    }

    public POMData(Model model, String groupId, String artifactId, String versionId ) {
        model.setGroupId(groupId);
        model.setArtifactId(artifactId);
        model.setVersion(versionId);
        model.setModelVersion("4.0.0");
        ArrayList<String> modules = new ArrayList<String>();
        model.setModules(modules);
        model.setPackaging("pom");
        Properties properties = new Properties();
        properties.setProperty("encoding", "UTF-8");       
        model.setProperties(properties);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = versionId;
        this.packaging = "pom";
   }

	public Model parsePomXmlFileToMavenPomModel(String path) throws Exception {

		Model model = null;
		FileReader reader = null;
		MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		reader = new FileReader(path);
		model = mavenreader.read(reader);
		return model;

	}

	public void parseMavenPomModelToXmlFile(String path, Model model) throws Exception {
		MavenXpp3Writer mavenWriter = new MavenXpp3Writer();
		Writer writer = new FileWriter(path);
		mavenWriter.write(writer, model);
	}


    public static void main(String... args) throws Exception {
    	//File parentPomFil = new File("C:\\MASTER-CORE\\iib\\iib-core-mvn-pom.xml");
		//System.out.println(new POMData(new ParentData(parentPomFil), "crs.fcl.integration.iib", "ProjectName", "0.0.1-SNAPSHOT"));
    }

    @Override
    public String toString() {
        return this.xml;
    }

}