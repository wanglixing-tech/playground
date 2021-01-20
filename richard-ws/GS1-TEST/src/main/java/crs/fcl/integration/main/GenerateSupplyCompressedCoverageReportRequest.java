package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * This is a program used to help evaluating GS1 Web Service capacity and performance
 * created based on [JIRA] (ESB-189) PoC for GK/GS1 Web Interface
 * Usage:
 * java GenerateSupplyCompressedCoverageReportRequest 
 * 		-i C:\Users\Richard.Wang\Documents\My_DailyWork\GS1\gtinList45482.txt 
 * 		-o C:\Users\Richard.Wang\Documents\My_DailyWork\GS1\coverageReportRequest45482.xml 
 * 		-t C:\Users\Richard.Wang\Documents\My_DailyWork\GS1\SupplyCompressedCoverageReportRequestTemplate.xml
 * @author Richard.Wang
 * @Date 2019/05/28
 */
public class GenerateSupplyCompressedCoverageReportRequest {
	private static String retailerFeedbackReportFileName = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\retailerFeedbackReportFile-TIMESTAMP.xml";
	private static String retailerFeedbackReportZipFileName = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\retailerFeedbackReportFile-TIMESTAMP.zip";
	static String retailerFeedbackReportFile = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\retailerFeedbackReportFile-TIMESTAMP.xml";
	static String retailerFeedbackReportZipFile = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\retailerFeedbackReportFile-TIMESTAMP.zip";
	private static String template = null;
	private static String GTINDataFile = null;
	private static String soapRequestMsgFile = null;
	private static final Logger log = Logger.getLogger(GenerateSupplyCompressedCoverageReportRequest.class.getName());
	private static Options options = new Options();
	private static Document templateDoc = null;
	private static Document retailerFeedbackReportDoc = null;
	private static Document soapRequestMsgDoc = null;

	/**
	 * Main method
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		/*
		 * Parse command-line arguments
		 */
		if (parseArgs(args) == false) {
			help();
		}
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
		System.out.println("Current Date and time:" + timeStamp);
		retailerFeedbackReportFile = retailerFeedbackReportFileName.replaceAll("TIMESTAMP", timeStamp);
		retailerFeedbackReportZipFile = retailerFeedbackReportZipFileName.replaceAll("TIMESTAMP", timeStamp);
		System.out.println("RetailerFeedbackReportFile:" + retailerFeedbackReportFile);
		System.out.println("RetailerFeedbackReportZipFile:" + retailerFeedbackReportZipFile);
		try {
			/*
			 *  Upload SOAP request template
			 */
			File soapTemplateFile = new File(template);
			templateDoc = parseXML(soapTemplateFile);
			
			/*
			 * Create Document object from template
			 */
			retailerFeedbackReportDoc = DocumentHelper.createDocument();
			//Element root = templateDoc.getRootElement().createCopy();
			//soapRequestMsgDoc.add(root);
			
			Element root1 = retailerFeedbackReportDoc.addElement("RetailerFeedbackReport", "http://www.brandbank.com/schemas/CoverageFeedback/2005/11");
			Element msgElem = root1.addElement("Message");			
			msgElem.addAttribute("DateTime", getCurrentTimeStamp()) ;
			msgElem.addAttribute("Domain", "API") ;

			/*
			 * Create "Item" node for each GTIN and insert it into the document
			 */
			BufferedReader reader = new BufferedReader(new FileReader(GTINDataFile));
			String gtinNumber = reader.readLine();
			while (gtinNumber != null) {
				System.out.println(gtinNumber);
				insertNewItem(retailerFeedbackReportDoc, gtinNumber);
				gtinNumber = reader.readLine();
			}
			
			reader.close();
			
			/*
			 * Output RetailerFeedbackReport to an XML file
			 */
			write(retailerFeedbackReportDoc, retailerFeedbackReportFile);
			
			/*
			 * get a string generated from above XML file after zipped  and encoded with Base 64
			 */
			String strZippedAndEncoded = zipB64(new File(retailerFeedbackReportFile));
			
			/*
			 * Create request from template
			 */
			soapRequestMsgDoc = DocumentHelper.createDocument();
			Element root2 = templateDoc.getRootElement().createCopy();
			soapRequestMsgDoc.add(root2);
			Element rawFileDataElem = getRawFileDataNode(soapRequestMsgDoc);
			rawFileDataElem.setText(strZippedAndEncoded);

			/*
			 * Output request message to an XML file
			 */
			write(soapRequestMsgDoc, soapRequestMsgFile);

		} catch (DocumentException e) {
			log.log(Level.SEVERE, "Document Exception");
			log.log(Level.SEVERE, e.getStackTrace().toString());
		} catch (IOException e) {
			log.log(Level.SEVERE, "I/O Exception");
			log.log(Level.SEVERE, e.getStackTrace().toString());
		}

	}

	/**
	 * Parse command-line arguments
	 * @param args arguments
	 * @return true/false depends on the parsed result
	 */
	public static boolean parseArgs(String[] args) {
		options.addOption("h", "help", false, "show help.");
		options.addOption("t", "template", true, "Specify SOAP request message template.");
		options.addOption("i", "input", true, "Specify GTIN list file, all GTINs will be composed into 'SupplyCompressedCoverageReport'.");
		options.addOption("o", "output", true, "Specify file name, which will be created with template and GTIN data.");

		boolean retValue = true;
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("h"))
				help();
			if (cmd.hasOption("t")) {
				log.log(Level.INFO, "Using cli argument -t=" + cmd.getOptionValue("t"));
				template = cmd.getOptionValue("t");
			} else {
				log.log(Level.SEVERE, "SOAP request message template file must be specified from command-line");
				retValue = false;
			}
			if (cmd.hasOption("i")) {
				log.log(Level.INFO, "Using cli argument -i=" + cmd.getOptionValue("i"));
				GTINDataFile = cmd.getOptionValue("i");
			} else {
				log.log(Level.SEVERE,
						"Testing data file must be specified from command-line, which will be combined in the BODY of SOAP request message");
				retValue = false;
			}
			if (cmd.hasOption("o")) {
				log.log(Level.INFO, "Using cli argument -o=" + cmd.getOptionValue("o"));
				soapRequestMsgFile = cmd.getOptionValue("o");
			} else {
				log.log(Level.SEVERE, "You have to specify a file name for generated SOAP request message.");
				retValue = false;
			}
		} catch (ParseException e) {
			log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			retValue = false;
		}
		return retValue;
	}

	/**
	 * To parse XML and upload it as a Document object
	 * @param file XML file
	 * @return Document object
	 * @throws DocumentException possible exceptions
	 */
	public static Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	/**
	 * Output XML document contents to standard output
	 * @param xmldoc XML document
	 * @throws IOException possible exception
	 */
	public void outputXML2Std(Document xmldoc) throws IOException {
		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(System.out, format);
		writer.write(xmldoc);
	}

	/**
	 * Output XML document contents to file
	 * 
	 * @param document  Document of IIB Dynamic Configuration XML file, this is just
	 *                  for keeping a record of what has been left
	 * @param sFileName should always be IIB Dynamic Configuration XML file name
	 * @throws IOException Any IO related exception
	 */
	public void outputXML2File(Document document, String sFileName) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(sFileName), format);
		writer.write(document);
		writer.close();
	}

	/**
	 * This will be used to output the generated SOAP request message xml file.
	 * 
	 * @param document  XML Document object
	 * @param sFileName XML File name to be written to
	 * @throws IOException possible exception
	 */
	public static void write(Document document, String sFileName) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setSuppressDeclaration(true);
		// lets write to a file
		log.log(Level.INFO, "Output SOAP request message to XML file......" + sFileName);
		XMLWriter writer = new XMLWriter(new FileWriter(sFileName), format);
		writer.write(document);
		writer.close();

		// Pretty print the document to System.out
		if ((log.getLevel() != null) && log.getLevel().equals(Level.FINEST) == true) {
			log.log(Level.INFO, "Display SOAP request message XML file......");
			writer = new XMLWriter(System.out, format);
			writer.write(document);
		}
	}

	/**
	 * String xPath to the location where new Item nodes will go to
	 * @param doc SOAP request message XML Document object
	 * @return String xPath
	 */
	public static String getXPath2MessageData(Document doc) {
		Element itemElem = (Element) doc.selectSingleNode("//*[name() = 'Message']");
		//return itemElem.getUniquePath();
		//return itemElem.getPath();
		return itemElem.getParent().getUniquePath();
	}

	public static Element getRawFileDataNode(Document doc) {
		return (Element) doc.selectSingleNode("//*[name() = 'ns:rawFileData']");
	}

	/**
	 * String xPath to the existing Item node, which is copied from themplat
	 * @param doc SOAP request message XML Document object
	 * @return String xPath
	 */
	public static String getXPath2Item(Document doc) {
		Element itemElem = (Element) doc.selectSingleNode("//*[name() = 'Item']");
		return itemElem.getUniquePath();
	}

	/**
	 * Set attribute value and then return the Item node with that attribute
	 * @param doc SOAP request Message XML Document object 
	 * @param gtinNumber GTIN
	 * @return Item node
	 */
	public static void insertNewItem(Document doc, String gtinNumber) {
		/*
		 * sXpath2ItemParent will be used to indicate where the new Item node to go
		 */
		String sXpath2RawFileData = getXPath2MessageData(doc);
		Node rawFileDataNode = doc.selectSingleNode(sXpath2RawFileData);
		Element rawFileDataElem = (Element)rawFileDataNode;
		Element itemElem = rawFileDataElem.addElement("Item");
		Element retailerIDElem = itemElem.addElement("RetailerID");
		retailerIDElem.setText("null");
		Element descriptionElem = itemElem.addElement("Description");
		descriptionElem.setText(".");
		
		Element gtinsElem = itemElem.addElement("GTINs");
		Element gtinElem = gtinsElem.addElement("GTIN");
		gtinElem.addAttribute("Value", gtinNumber);
		Element suppliersElem = gtinElem.addElement("Suppliers");
		Element supplierElem = suppliersElem.addElement("Supplier");
		Element supplierCodeElem = supplierElem.addElement("SupplierCode");
		supplierCodeElem.setText(".");
		Element supplierNameElem = supplierElem.addElement("SupplierName");
		supplierNameElem.setText(".");
		
		Element ownLabelElem = itemElem.addElement("OwnLabel");
		ownLabelElem.setText("false");;
	}

	/**
	 * Help foe CLI arguments
	 */
	private static void help() {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("java -jar WebServiceEvaluation.jar ", options);
		System.exit(0);
	}
	
	public static String getCurrentTimeStamp() {
	    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
	}
	
	private static String zipB64(File file) throws IOException {
	    byte[] buffer = new byte[1024];
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
	    try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            try (FileInputStream fis = new FileInputStream(file)) {
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }
        }
	    /**
	     * For double checking the zipped data before encoding with file format
	     */
	    write2FilefromByteStream(baos, retailerFeedbackReportZipFile);
	    byte[] bytes = baos.toByteArray();
	    String encodedBase64 = new String(Base64.getEncoder().encodeToString(bytes));
	    return encodedBase64;
	}
	
	private static void write2FilefromByteStream(ByteArrayOutputStream byteOutStream, String outputFile) throws IOException {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(outputFile);
			// writing bytes in to byte output stream
			byteOutStream.writeTo(outStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outStream.close();
		}
	}
	
	public static String removeXMLHeader(String xml) {
		if (xml != null) {
			xml = xml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
		}
	   	return xml;
    }
}
