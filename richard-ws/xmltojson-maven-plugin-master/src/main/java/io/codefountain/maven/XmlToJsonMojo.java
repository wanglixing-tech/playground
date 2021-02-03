package io.codefountain.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.IOException;

@Mojo(name="XmlToJson", defaultPhase = LifecyclePhase.COMPILE)
public class XmlToJsonMojo extends AbstractMojo {

    @Parameter(required = true)
    private String sourceXmlFilePath;

    @Parameter(required = true)
    private String destinationJsonFilePath;

    private static int PRETTY_PRINT_INDENT_FACTOR = 4;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String xmlContent = FileUtils.readFileToString(new File(sourceXmlFilePath), "UTF-8");
            JSONObject xmlJSONObj = XML.toJSONObject(xmlContent);
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
            FileUtils.writeStringToFile(new File(destinationJsonFilePath), jsonPrettyPrintString, "UTF-8");
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to execute plugin", e);
        }
    }
}
