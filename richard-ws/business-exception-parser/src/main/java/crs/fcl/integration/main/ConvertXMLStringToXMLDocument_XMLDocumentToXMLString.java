package crs.fcl.integration.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ConvertXMLStringToXMLDocument_XMLDocumentToXMLString {

	/**
	 * Convert XML Document To XML String, XML String to XML Document In Java
	 */
	public static void main(String[] args) {
		String XMLString = convertXMLDocumentToString();
		System.out.println("XMLString %%%%%%% " + XMLString);
		Document doc = convertStringToXMLDocument(XMLString);
		System.out.println("Document %%%%%%% " + doc);
	}

	// convert a String into XMLDocument
	private static Document convertStringToXMLDocument(String XMLString) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(XMLString)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// convert a XMLDocument into String
	private static String convertXMLDocumentToString() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			InputStream iStream = new FileInputStream(new File("student.xml"));
			org.w3c.dom.Document document = documentBuilderFactory.newDocumentBuilder().parse(iStream);
			StringWriter stringWriter = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
			transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
			String output = stringWriter.toString();
			System.out.println(output.replaceAll("\n|\r|\\s+", ""));
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}