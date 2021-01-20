package crs.fcl.integration.main;

import java.io.*;
import java.math.*;
import org.apache.xmlbeans.*;

import noNamespace.MessageDocument;

public class TestXMLBeans {

	public static void main(String[] args) {
		File xmlFile = new File("C:\\Temp\\working.xml");
		try {
		MessageDocument msgDoc = MessageDocument.Factory.parse(xmlFile);
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
