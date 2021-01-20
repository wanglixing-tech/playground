package crs.fcl.integration.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
 * This is a program used to help evaluating GS1 Web Service capacity and
 * performance created based on [JIRA] (ESB-189) PoC for GK/GS1 Web Interface
 * 
 * @author Richard.Wang
 * @Date 2019/05/28
 */
public class CollectGTINSFromProdXMLData {
	private static Options options = new Options();
	private static String prdDataFile = null;
	private static final Logger log = Logger.getLogger(CollectGTINSFromProdXMLData.class.getName());
	private static Document prdDataDoc = null;

	/**
	 * Main method
	 * 
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
			 * Upload Unsent Product Data
			 */
			File unsentPrdDataFile = new File(prdDataFile);
			prdDataDoc = parseXML(unsentPrdDataFile);

			List<String> gtinList = collectGTINWithPVID(prdDataDoc);
			int i = 0;
			for (String gtin : gtinList) {
				System.out.println("GTIN-" + i + ":" + gtin);
				i++;
			}
		} catch (DocumentException e) {
			log.log(Level.SEVERE, "Document Exception");
			log.log(Level.SEVERE, e.getStackTrace().toString());
		}

	}

	/**
	 * Parse command-line arguments
	 * 
	 * @param args arguments
	 * @return true/false depends on the parsed result
	 */
	public static boolean parseArgs(String[] args) {
		options.addOption("h", "help", false, "show help.");
		options.addOption("i", "input", true, "Specify data file, in which all data will go to SOAP envelop.");

		boolean retValue = true;
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("h")) {
				help();
			}
			if (cmd.hasOption("i")) {
				log.log(Level.INFO, "Using cli argument -i=" + cmd.getOptionValue("i"));
				// testDataFile = cmd.getOptionValue("i");
				prdDataFile = cmd.getOptionValue("i");
			} else {
				log.log(Level.SEVERE,
						"Testing data file must be specified from command-line, which will be combined in the BODY of SOAP request message");
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
	 * 
	 * @param file XML file
	 * @return Document object
	 * @throws DocumentException possible exceptions
	 */
	public static Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	public static List<String> collectGTINWithPVID(Document prdDataDoc) {
		List<String> gtinList = new ArrayList<String>();
		List<Node> prdNodeList = prdDataDoc.selectNodes("//*[@Scheme='GTIN']");

		for (Node prdNode : prdNodeList) {
			String sXPath2gtinNode = prdNode.getUniquePath();
			// System.out.println("GTIN=" +
			// prdNode.selectSingleNode(sXPath2gtinNode).getText());
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
