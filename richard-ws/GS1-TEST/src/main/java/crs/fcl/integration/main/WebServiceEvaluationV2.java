package crs.fcl.integration.main;
	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileReader;
	import java.io.FileWriter;
	import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
	import java.util.logging.Logger;
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
	 * 
	 * @author Richard.Wang
	 * @Date 2019/05/28
	 */
public class WebServiceEvaluationV2 {
		private static String template = null;
		private static String prdDataFile = null;
		//private static String testDataFile = null;
		private static String soapRequestMsgFile = null;
		private static final Logger log = Logger.getLogger(WebServiceEvaluation.class.getName());
		private static Options options = new Options();
		private static Document templateDoc = null;
		private static Document prdDataDoc = null;
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

			try {
				/*
				 *  Upload SOAP request template
				 */
				File soapTemplateFile = new File(template);
				templateDoc = parseXML(soapTemplateFile);

				/*
				 *  Upload Unsent Product Data 
				 */
				File unsentPrdDataFile = new File(prdDataFile);
				prdDataDoc = parseXML(unsentPrdDataFile);

				/*
				 * Create Document object from template
				 */
				soapRequestMsgDoc = DocumentHelper.createDocument();
				Element root = templateDoc.getRootElement().createCopy();
				soapRequestMsgDoc.add(root);
				/*
				 * sXpath2ItemParent will be used to indicate where the new Item node to go
				 */
				String sXpath2ItemParent = getXPath2ParentOfItem(soapRequestMsgDoc);
				
				/*
				 * remove the only one Item node from Document 
				 */
				Node ItemItemNode = soapRequestMsgDoc.selectSingleNode(getXPath2Item(soapRequestMsgDoc));
				ItemItemNode.detach();
				
				/*
				 * Create "Item" node for each GTIN (GTINs are collected from unsent product data XML file)
				 */				
				List<String> gtinList = collectGTINWithPVID(prdDataDoc);
				for(String gtin : gtinList){
					System.out.println("GTIN:" + gtin);
					Element newItem = createNewItem(templateDoc, gtin);
					Element ItemParentElem = (Element)soapRequestMsgDoc.selectSingleNode(sXpath2ItemParent);
					ItemParentElem.add(newItem.createCopy());				
				}	
				
				/**
				 * Create "Item" node for each GTIN (GTINs are created and saved in a TEXT file previously)
				 *
					BufferedReader reader = new BufferedReader(new FileReader(testDataFile));
					String gtinNumber = reader.readLine();
					while (gtinNumber != null) {
						System.out.println(gtinNumber);
						Element newItem = createNewItem(templateDoc, gtinNumber);
						Element ItemParentElem = (Element)soapRequestMsgDoc.selectSingleNode(sXpath2ItemParent);
						ItemParentElem.add(newItem.createCopy());
						gtinNumber = reader.readLine();
					}
					reader.close();
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
			options.addOption("i", "input", true, "Specify unsent product data file, in which all GTIN numbers will go to SOAP envelop.");
			options.addOption("o", "output", true, "Specify file name, which will be created with template and test data.");

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
					//testDataFile = cmd.getOptionValue("i");
					prdDataFile = cmd.getOptionValue("i");
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
		public static String getXPath2ParentOfItem(Document doc) {
			Element itemElem = (Element) doc.selectSingleNode("//*[name() = 'Item']");
			//return itemElem.getUniquePath();
			//return itemElem.getPath();
			return itemElem.getParent().getUniquePath();
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
		public static Element createNewItem(Document doc, String gtinNumber) {
			Element gtinElem = (Element)doc.selectSingleNode("//*[name() = 'GTIN']");
			gtinElem.addAttribute("Value", gtinNumber);
			Element itemElem = (Element) doc.selectSingleNode("//*[name() = 'Item']");
			return itemElem;
		}

		/**
		 * Collect GTIN with PVID from Document
		 * @param prdDataDoc
		 * @return
		 */
		public static List<String> collectGTINWithPVID(Document prdDataDoc){
			List <String> gtinList = new ArrayList<String>();
			List<Node> prdNodeList = prdDataDoc.selectNodes("//*[@Scheme='GTIN']");
			
			for(Node prdNode : prdNodeList) {
				String sXPath2gtinNode = prdNode.getUniquePath();
				//System.out.println("GTIN=" + prdNode.selectSingleNode(sXPath2gtinNode).getText());
				gtinList.add(prdNode.selectSingleNode(sXPath2gtinNode).getText());
			}
			return gtinList;
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
	}


