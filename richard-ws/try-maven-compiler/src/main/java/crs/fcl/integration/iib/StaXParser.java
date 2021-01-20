package crs.fcl.integration.iib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

public class StaXParser {

	private static boolean isProjectName;
	private static boolean isSubProject;
	private static boolean isBuildCommand;
	private static boolean isBuildCommandName;
	private static boolean isNature;

	public String parser(File file) throws FileNotFoundException, XMLStreamException {
		// Variables to make sure whether a element
		// in the xml is being accessed or not
		// if false that means elements is
		// not been used currently , if true the element or the
		// tag is being used currently
		String projectName = null;
		boolean hasAppNature = false;
		isProjectName = false;
		isSubProject = false;
		isBuildCommand = false;
		isBuildCommandName = false;
		isNature = false;

		// Instance of the class which helps on reading tags
		XMLInputFactory factory = XMLInputFactory.newInstance();

		// Initializing the handler to access the tags in the XML file
		XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(file));

		// Checking the availabilty of the next tag
		while (eventReader.hasNext()) {
			// Event is actually the tag . It is of 3 types
			// <name> = StartEvent
			// </name> = EndEvent
			// data between the StartEvent and the EndEvent
			// which is Characters Event
			XMLEvent event = eventReader.nextEvent();

			// This will trigger when the tag is of type <...>
			if (event.isStartElement()) {
				StartElement element = (StartElement) event;

				// Checking which tag needs to be opened for reading.
				// If the tag matches then the boolean of that tag
				// is set to be true.
				if (element.getName().toString().equalsIgnoreCase("buildcommand")) {
					isBuildCommand = true;
				}

				if (element.getName().toString().equalsIgnoreCase("name") && !isBuildCommand) {
					isProjectName = true;
				} else {
					isBuildCommandName = true;
				}

				if (element.getName().toString().equalsIgnoreCase("project")) {
					isSubProject = true;
				}

				if (element.getName().toString().equalsIgnoreCase("nature")) {
					isNature = true;
				}
			}

			// This will be triggered when the tag is of type </...>
			if (event.isEndElement()) {
				EndElement element = (EndElement) event;

				// Checking which tag needs to be closed after reading.
				// If the tag matches then the boolean of that tag is
				// set to be false.
				if (element.getName().toString().equalsIgnoreCase("buildcommand")) {
					isBuildCommand = false;
				}

				if (element.getName().toString().equalsIgnoreCase("name") && isProjectName) {
					isProjectName = false;
				}

				if (element.getName().toString().equalsIgnoreCase("name") && isBuildCommandName) {
					isBuildCommandName = false;
				}

				if (element.getName().toString().equalsIgnoreCase("project")) {
					isSubProject = false;
				}

				if (element.getName().toString().equalsIgnoreCase("nature")) {
					isNature = false;
				}
			}

			// Triggered when there is data after the tag which is currently opened.
			if (event.isCharacters()) {
				// Depending upon the tag opened the data is retrieved .
				Characters element = (Characters) event;
				if (isProjectName && !isBuildCommand) {
					projectName = element.getData();
				}

				if (isSubProject) {
					// System.out.println("Dependent project:" + element.getData());
				}
				if (isNature) {
					if (element.getData().equals("com.ibm.etools.msgbroker.tooling.applicationNature")) {
						// System.out.println("Nature:" + element.getData());
						hasAppNature = true;
					}
				}
			}
		}

		return ( hasAppNature ? projectName : null );
	}
}
