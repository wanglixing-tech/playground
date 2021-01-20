package crs.fc.esb.devops;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

class CreateXPathFromXML extends DefaultHandler {
	static StringBuffer xpathBuffer = new StringBuffer();

	String xPath = "/";
	XMLReader xmlReader;
	CreateXPathFromXML parent;
	StringBuilder characters = new StringBuilder();
	Map<String, Integer> elementNameCount = new HashMap<String, Integer>();

	public CreateXPathFromXML(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
	}

	private CreateXPathFromXML(String xPath, XMLReader xmlReader, CreateXPathFromXML parent) {
		this(xmlReader);
		this.xPath = xPath;
		this.parent = parent;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		Integer count = elementNameCount.get(qName);
		if (null == count) {
			count = 1;
		} else {
			count++;
		}
		elementNameCount.put(qName, count);
		// String childXPath = xPath + "/" + qName + "[" + count + "]";
		String childXPath = xPath + "/" + qName;

		int attsLength = atts.getLength();
		if (attsLength > 0) {
			for (int x = 0; x < attsLength; x++) {
				if (atts.getQName(x) == "xmlns:xsi") {
					break;
				} else {
					childXPath = childXPath + "[@" + atts.getQName(x) + "='" + atts.getValue(x) + "']";
				}
			}
		} else {
			childXPath = childXPath + "[" + count + "]";
		}

		// System.out.println(childXPath);

		CreateXPathFromXML child = new CreateXPathFromXML(childXPath, xmlReader, this);
		xmlReader.setContentHandler(child);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String value = characters.toString().trim();
		if (value.length() > 0) {
			
			xpathBuffer = xpathBuffer.append(xPath + "\t" + characters.toString() + "\n");  
			//System.out.println(xPath + ",'" + characters.toString() + "'");
		}
		xmlReader.setContentHandler(parent);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}
	
	public static void main(String[] args) throws Exception {
		String inputFileName = "";
		String outputFileName = "";
		
		if (args.length < 4) {
			System.out.println("Arguments did not meet the required format, please try again with following Usage:");
			System.out.println("Usage: java -jar CreateXPathFromXML -i full-path-of-xml-file -o full-path-of-xpath-file");
			System.exit(100);			
		}
		if (!(args[0].isEmpty() ) && (args[0].equals("-i")) && (!args[1].isEmpty())) {
			inputFileName = args[1];
        }
		
		if (!(args[2].isEmpty() ) &&  ( args[2].equals("-o") && !args[3].isEmpty())) {
			outputFileName = args[3];
		}
		
		if (( inputFileName.isEmpty() || outputFileName.isEmpty() )) {
			System.out.println("Arguments did not meet the required format, please try again with following Usage:");
			System.out.println("Usage: java -jar CreateXPathFromXML -i full-path-of-xml-file -o full-path-of-xpath-file");
			System.exit(100);
		} else {
			System.out.println("Input File Name is: " + inputFileName);
			System.out.println("Output File Name is: " + outputFileName);
		}
		File file = new File(outputFileName);
		FileWriter fw = new FileWriter(file);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();

		xr.setContentHandler(new CreateXPathFromXML(xr));
		xr.parse(new InputSource(new FileInputStream(inputFileName)));
		
		System.out.println("Writing xpath list into file......");
		fw.write(xpathBuffer.toString());
        fw.close();
		System.out.println("Done.");

	}


}
