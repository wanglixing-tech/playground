package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 * @author Richard.Wang
 * @Date 2019/05/28
 */
public class GenerateSupplyCompressedCoverageReport {
	private static String retailerFeedbackReportFileName = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\retailerFeedbackReportFile-TIMESTAMP.xml";
	private static String retailerFeedbackReportFile = "";
	private static String GTINDataFile = null;
	private static String soapRequestMsgFile = null;
	private static Options options = new Options();
	private static Element itemElem  = null;
	private static Document soapRequestTemplateDoc = null;
	private static Document retailerFeedbackReportDoc = null;
	private static Document soapRequestMsgDoc = null;
	private static final Logger log = Logger.getLogger(GenerateSupplyCompressedCoverageReport.class.getName());

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
		System.out.println("RetailerFeedbackReportFile:" + retailerFeedbackReportFile);
		long start = System.currentTimeMillis();
		try {
			/*
			 *  Upload SOAP request template
			 */
			soapRequestTemplateDoc = DocumentHelper.parseText(getStrSOAPRequestTemplate());
			
			/*
			 * Create Document object from template
			 */
			retailerFeedbackReportDoc = DocumentHelper.createDocument();
			
			Element root1 = retailerFeedbackReportDoc.addElement("RetailerFeedbackReport", "http://www.brandbank.com/schemas/CoverageFeedback/2005/11");
			Element msgElem = root1.addElement("Message");			
			msgElem.addAttribute("DateTime", getCurrentTimeStamp()) ;
			msgElem.addAttribute("Domain", "API") ;

			/*
			 * Create "Item" node for each GTIN and insert it into the document
			 */
			
			Document itemNodeTemplateDoc = DocumentHelper.parseText(getStrItemNode());
			itemElem = getItemElement(itemNodeTemplateDoc);

			BufferedReader reader = new BufferedReader(new FileReader(GTINDataFile));
			String gtinNumber = reader.readLine();
			while (gtinNumber != null) {
				System.out.println(gtinNumber);
				replaceGTIN(itemNodeTemplateDoc, gtinNumber);
				msgElem.add(itemElem.createCopy());
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
			Element root2 = soapRequestTemplateDoc.getRootElement().createCopy();
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
	      long end = System.currentTimeMillis();
	      float sec = (end - start) / 1000F; 
	      System.out.println("Total " + sec + " seconds");
	}

	/**
	 * Parse command-line arguments
	 * @param args arguments
	 * @return true/false depends on the parsed result
	 */
	public static boolean parseArgs(String[] args) {
		options.addOption("h", "help", false, "show help.");
		options.addOption("i", "input", true, "Specify GTIN list file, all GTINs will be composed into 'SupplyCompressedCoverageReport'.");
		options.addOption("o", "output", true, "Specify file name, which will be created with template and GTIN data.");

		boolean retValue = true;
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("h"))
				help();
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
	
	public static Element getItemElement(Document doc) {
		Element itemElem = (Element) doc.selectSingleNode("//*[name() = 'Item']");
		return itemElem;
		
	}
	public static void replaceGTIN(Document itemDoc, String gtin) {
		Element gtinElem = (Element) itemDoc.selectSingleNode("//*[name() = 'GTIN']");
		gtinElem.addAttribute("Value", gtin);
	}

	/**
	 * Set attribute value and then return the Item node with that attribute
	 * @param doc SOAP request Message XML Document object 
	 * @param gtinNumber GTIN
	 * @return Item node
	 */
	public static void insertNewItem(Document doc, String gtinNumber) {		
	}

	/**
	 * Help foe CLI arguments
	 */
	private static void help() {
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
	    byte[] bytes = baos.toByteArray();
	    String encodedBase64 = new String(Base64.getEncoder().encodeToString(bytes));
	    return encodedBase64;
	}
	
	public static String removeXMLHeader(String xml) {
		if (xml != null) {
			xml = xml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
		}
	   	return xml;
    }
	
	public static String getStrItemNode() {
		String strItemNode = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				"<RetailerFeedbackReport xmlns=\"http://www.brandbank.com/schemas/CoverageFeedback/2005/11\">" + 
				"  <Message DateTime=\"2019-07-24T16:59:21.523\" Domain=\"API\">" + 
				" 		<Item>" + 
				"			<RetailerID>null</RetailerID>" + 
				"			<Description>.</Description>" + 
				"			<GTINS>" + 
				"				<GTIN Value=\"\">" + 
				"					<Suppliers>" + 
				"						<Supplier>" + 
				"							<SupplierCode>.</SupplierCode>" + 
				"							<SupplierName>.</SupplierName>" + 
				"						</Supplier>" + 
				"					</Suppliers>" + 
				"				</GTIN>" + 
				"			</GTINS>" + 
				"			<OwnLabel>false</OwnLabel>" + 
				"		</Item>" + 
				"  </Message>" + 
				"</RetailerFeedbackReport>";
		return strItemNode;
	}
	
	public static String getStrSOAPRequestTemplate() {
		String strItemNode = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ns=\"http://www.i-label.net/Partners/WebServices/DataFeed/2005/11\">" + 
				"   <soap:Header>" + 
				"      <ns:ExternalCallerHeader>" + 
				"         <ns:ExternalCallerId>9898797A-AE09-49BA-B4ED-2B75F528A964</ns:ExternalCallerId>" + 
				"      </ns:ExternalCallerHeader>" + 
				"   </soap:Header>" + 
				"   <soap:Body>" + 
				"      <ns:SupplyCompressedCoverageReport>" + 
				"         <!--Optional:-->" + 
				"         <ns:rawFileData>" + 
				"		 </ns:rawFileData>" + 
				"      </ns:SupplyCompressedCoverageReport>" + 
				"   </soap:Body>" + 
				"</soap:Envelope>";
		return strItemNode;
	}

}
