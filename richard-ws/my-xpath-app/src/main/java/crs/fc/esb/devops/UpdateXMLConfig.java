package crs.fc.esb.devops;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UpdateXMLConfig {
	private static BufferedReader br;

	public static void main(String[] args) {
		String inputXMLFileName = "";
		String inputXPathFileName = "";
		String outputXMLFileName = "";

		System.out.println("Arguments No.:" + args.length);
		if (args.length < 6) {
			System.out.println("Arguments did not meet the required format, please try again with following Usage:");
			System.out.println(
					"Usage: java -jar UpdateXMLConfig -i full-path-of-base-file -x full-path-of-xpath-file -o full-path-of-new-file");
			System.exit(100);
		}
		if (!(args[0].isEmpty()) && (args[0].equals("-i")) && (!args[1].isEmpty())) {
			inputXMLFileName = args[1];
		}

		if (!(args[2].isEmpty()) && (args[2].equals("-x") && !args[3].isEmpty())) {
			inputXPathFileName = args[3];
		}

		if (!(args[4].isEmpty()) && (args[4].equals("-o") && !args[5].isEmpty())) {
			outputXMLFileName = args[5];
		}

		if ((inputXMLFileName.isEmpty()) || (outputXMLFileName.isEmpty() || inputXPathFileName.isEmpty())) {
			System.out.println("Arguments did not meet the required format, please try again with following Usage:");
			System.out.println(
					"Usage: java -jar UpdateXMLConfig -i full-path-of-base-file -x full-path-of-xpath-file -o full-path-of-new-file");
			System.exit(100);
		} else {
			System.out.println("Input File Name is: " + inputXMLFileName);
			System.out.println("Input File Name is: " + inputXPathFileName);
			System.out.println("Output File Name is: " + outputXMLFileName);
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(inputXMLFileName);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Modify all of those defined in XPath List file
		try {

			br = new BufferedReader(new FileReader(inputXPathFileName));
			String line;
			while ((line = br.readLine()) != null) {
				// process the line.
				String[] nameValuePair = line.split("\t");
				XPath xpath = XPathFactory.newInstance().newXPath();
				XPathExpression expr;
				expr = xpath.compile(nameValuePair[0]);
				Node targetNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
				targetNode.setTextContent(nameValuePair[1]);
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Write updated XML
		System.out.println("Save updated configuration to " + outputXMLFileName);
		saveToXML(doc, outputXMLFileName);
		System.out.println("Done.");
	}

	public static void saveToXML(Document doc, String outFileName) {
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			// send DOM to file
			tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(outFileName)));

		} catch (TransformerException te) {
			System.out.println(te.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
}
