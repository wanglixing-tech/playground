package crs.fcl.integration.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * This is a program for collecting information from a Business Exception XML
 * file, then sent it to System support team for analyzing and resolving.
 * 
 * JIRA ticket: https://fclweb.jira.com/browse/EEIM-80
 * 
 * @author Richard.Wang
 * @Date 2020/10/08
 */
public class CollectTargetInfoFromExceptionEvent {
	private static String businessExceptionFile = "C:\\Users\\Richard.Wang\\WS-HOME\\ws-dom4j\\exceptioon-parser\\BusinessException.txt";
	private static final Logger log = Logger.getLogger(CollectTargetInfoFromExceptionEvent.class.getName());
	private static Document pefDoc = null;

	/**
	 * Main method
	 * 
	 * @param args command-line arguments
	 * @throws JaxenException 
	 */
	public static void main(String[] args) throws JaxenException {
		try {
			/*
			 * Upload Unsent Product Data
			 */
			File bef = new File(businessExceptionFile);
			pefDoc = parseXML(bef);

			List<String> eventContents = getEventContents(pefDoc);
			int i = 0;
			for (String xp : eventContents) {
				System.out.println("xpath-" + i + ":" + xp);
				i++;
			}
		} catch (DocumentException e) {
			log.log(Level.SEVERE, "Document Exception");
			log.log(Level.SEVERE, e.getStackTrace().toString());
		}

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
	/**
	 * To collect all attributes in each "<bus:Activity> node
	 * @param businessExceptionDoc
	 * @return attributes list of all activity node
	 * @throws JaxenException 
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getEventContents(Document businessExceptionDoc) throws JaxenException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put( "bus", "http://fcl.crs/xmlns/integration/esb/1.0/event/business");
		  
		Dom4jXPath xpath = new Dom4jXPath( "//bus:Activity");
		xpath.setNamespaceContext( new SimpleNamespaceContext( map));
		List<String> activityList = new ArrayList<String>();
		List<Node> activityNodeList = xpath.selectNodes(businessExceptionDoc);

		for (Node activityNode : activityNodeList) {
			String sXPath2Node = activityNode.getUniquePath();
			System.out.println(sXPath2Node);
			activityList.add(sXPath2Node);
		}
		return activityList;
	}

}
