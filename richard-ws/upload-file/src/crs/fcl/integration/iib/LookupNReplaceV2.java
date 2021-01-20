package crs.fcl.integration.iib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.xpath.DefaultXPath;

import de.slackspace.openkeepass.KeePassDatabase;
import de.slackspace.openkeepass.domain.Entry;
import de.slackspace.openkeepass.domain.KeePassFile;

public class LookupNReplaceV2 {
	private static String configRepoDBFile = null;
	private static String outPutFile = "config.xml";
	private static String keepassDBFile = null;
	private static String keyFile = null;	
	private static String envName = null;
	private static String egName = null;
	private static String domainName = null;
	private static String appName = null;
	private static KeePassFile database = null;
	private static Document configRepo = null;	
	
	final static Logger logger = Logger.getLogger(LookupNReplaceV2.class);

	public static void main(String[] args) {

		try {
			//logs a debug message
			if(logger.isDebugEnabled()){
			    logger.debug("This is debug");
			}
			
			//Get system properties
			keepassDBFile = System.getProperty("keepass");
			keyFile = System.getProperty("key");
			configRepoDBFile = System.getProperty("dbfile");		
			envName = System.getProperty("env");
			egName = System.getProperty("eg");
			domainName = System.getProperty("domain");
			appName = System.getProperty("app");
			
			if (isNullOrEmpty(keepassDBFile) 
					|| isNullOrEmpty(keepassDBFile)
					|| isNullOrEmpty(keyFile)
					|| isNullOrEmpty(configRepoDBFile)
					|| isNullOrEmpty(envName)
					|| isNullOrEmpty(egName)
					|| isNullOrEmpty(domainName)
					|| isNullOrEmpty(appName)) {
				logger.error("At lease one mandatory argument has not been passed with a valid value.");
				System.exit(1);
			}
			
			// Upload Repository DB
			File dbFile = new File(configRepoDBFile);
			configRepo = parseXML(dbFile);
			
			// Upload Keepass DB
			database = KeePassDatabase.getInstance(keepassDBFile)
					.openDatabase(new File(keyFile));

			// Get plate ready for target flows
			Document configDocument = buildAppFlowsConfigDoc(egName, domainName);
			Element whereFlows2Go = (Element) configDocument.selectSingleNode("/executionGroup/domain/flows");
			// Select flow nodes from Repo
			XPath xPath4Repo = getXPath4DB(envName, egName, domainName, appName);
			System.out.println("\nCurrent Element's path:" + xPath4Repo.getText());
			List<Node> nodes = configRepo.selectNodes(xPath4Repo.getText());
			System.out.println("---------Deal with eacho of flows in one application-------------------");
			
			XPath xPath = getXPath4CDXML(egName, domainName, appName);
			for (Node node : nodes) {
				// Add the flow element to configDocument
				Node clonedFlowNode = (Node)node.detach();
				whereFlows2Go.add((Element)clonedFlowNode);
				
				System.out.println("\nCurrent Element :" + node.getName());
				System.out.println("appName : " + node.valueOf("@applicationName"));
				System.out.println("flowName : " + node.valueOf("@flowName"));
				System.out.println("xPath : " + node.getUniquePath());
				
				// Parse subscribers of target flow
				XPath subscribersXPath = DocumentHelper.createXPath(xPath.getText() + "/subscribers");
				if (isEmpty(configDocument, subscribersXPath.getText())) {
					System.out.println("No subscriers node found.");
				} else {
					doSubscribers(configDocument, subscribersXPath);
				}
				
				// Parse providers of the target flow
				XPath providersXPath = DocumentHelper.createXPath(xPath.getText() + "/providers");
				if (isEmpty(configDocument, providersXPath.getText())) {
					System.out.println("No providers node found.");
				} else {
					doProviders(configDocument, providersXPath);
				}
			}
			
			// create configuration file to verify the result
			// or send the configuration Document to the MQ directly
			write(configDocument, outPutFile);
			
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
    
	public static Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}
	
	public static Document buildAppFlowsConfigDoc(String egName, String domainName) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "executionGroup" ).addAttribute("EGName", egName);
        Element domainElement = root.addElement("domain").addAttribute("domainName", domainName);
        domainElement.addElement("flows");
        return document;

	}

	/**
	 * THis will be used to verify/backup the generated xml result, instead, the
	 * result should be sent to MQ directly with the string/stream format
	 * 
	 * @param document
	 */
	public static void write(Document document, String sFileName) throws IOException {

		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter(sFileName));
		writer.write(document);
		writer.close();

		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		writer = new XMLWriter(System.out, format);
		writer.write(document);

		// Compact format to System.out
		format = OutputFormat.createCompactFormat();
		writer = new XMLWriter(System.out, format);
		writer.write(document);
	}

	public static XPath getXPath4CDXML(String egName, String domainName, String appName) {
		String xpathStr = String.format("/executionGroup[@EGName='%s']/domain[@domainName='%s']/flows/flow[@applicationName='%s']",
				egName, domainName, appName);
		XPath xpath = new DefaultXPath(xpathStr);
		return xpath;
	}
	
	public static XPath getXPath4DB(String envName, String egName, String domainName, String appName) {
		String xpathStr = String.format("/configuration/Environment[@envName='%s']/executionGroup[@EGName='%s']/domain[@domainName='%s']/flows/flow[@applicationName='%s']",
				envName, egName, domainName, appName);
		XPath xpath = new DefaultXPath(xpathStr);
		return xpath;
	}

	/**
	 * This is to parse all subscribers in target flow, replace all empty fields with valid values.
	 * @param subscribers
	 * @param xpath
	 * @throws DocumentException
	 */
	public static void doSubscribers(Document subscribers, XPath xpath) throws DocumentException {
		List<Node> sub0NodeList = subscribers.selectNodes(xpath.getText());
		for (Node node : sub0NodeList) {
			System.out.println("\nCurrent Element :" + node.getName());
			Element element = (Element) node;
			Iterator<Element> iterator = element.elementIterator("subscriber");
			while (iterator.hasNext()) {
				Element subscriberElement = (Element) iterator.next();
				List<Node> sub1NodeList = subscriberElement.selectNodes("*");
				for (Node sub1Node : sub1NodeList) {
					if (sub1Node.getNodeType() == Node.ELEMENT_NODE) {
						Element element1 = (Element) sub1Node;
						String sXpath1 = sub1Node.getUniquePath();
						System.out.println("XPATH1=" + sXpath1);
						if(element1.isTextOnly()) {
							System.out.println(element1.getName() + "=" + element1.getText());
						} 
						if (isEmpty(subscribers, sXpath1)) {
							if (element1.getName().toLowerCase().equals("password")) {
								System.out.println("-" + element1.getName() + " is empty.");
								System.out.println("lookup from keepass DB.....");	
								System.out.println(sub1Node.getParent().getStringValue() +  "/userName");
								String userName = subscribers.selectSingleNode(sub1Node.getParent().getStringValue() +  "/userName").getText();
								System.out.println("Username=" + userName);								
								// Search for single entry
								Entry userEntry = database.getEntryByTitle(userName);
								System.out.println("Title: " + userEntry.getTitle() + " Password: " + userEntry.getPassword());
							}
							// Convert to Repository file's xPath
							//String sRepoXpath = getXPath4DB(egName, domainName, flowName, appName).getText();
						    //String[] splitedXpath = sXpath1.split("flow\\[\\d\\]\\/");
						    //System.out.println(sRepoXpath + "/" + splitedXpath[1]);
						    //Node nodeInConfigRepo = configRepo.selectSingleNode(sRepoXpath + "/" + splitedXpath[1]);
						    //System.out.println(nodeInConfigRepo.getName() + ":" + nodeInConfigRepo.getText());
						    //Node nodeInconfigRepo = configRepo.selectSingleNode(sXpath1);
						    //nodeInconfigRepo.setText(nodeInConfigRepo.getText());
						} else {
							List<Node> sub2NodeList = element1.selectNodes("*");
							for (Node sub2Node : sub2NodeList) {
								if (sub2Node.getNodeType() == Node.ELEMENT_NODE) {
									Element element2 = (Element) sub2Node;
									if(element2.isTextOnly()) {
										System.out.println(element2.getName() + "=" + element2.getText());
									} 
									String sXpath2 = element2.getUniquePath();
									//System.out.println("XPATH2=" + sXpath2);
									if (isEmpty(subscribers, sXpath2)) {
										System.out.println("===>" + element2.getName() + " is empty.");
										if (element2.getName().toLowerCase().equals("password")) {
											System.out.println("lookup from keepass DB.....");	
											//System.out.println(element2.getParent().getUniquePath() + "/userName");
											String userName = subscribers.selectSingleNode(element2.getParent().getUniquePath() + "/userName").getText();
											//System.out.println("Username=" + userName);								
											// Search for single entry
											Entry userEntry = database.getEntryByTitle(userName);
											System.out.println("Title: " + userEntry.getTitle() + " Password: " + userEntry.getPassword());
										    Node pwNode = subscribers.selectSingleNode(sXpath2);
										    pwNode.setText(userEntry.getPassword());

										}
										// Convert to DB file's xPath
										//String sRepoXpath = getXPath4DB(egName, domainName, flowName, appName).getText();
									    //String[] splitedXpath = sXpath2.split("flow\\[\\d\\]\\/");
									    //System.out.println(sRepoXpath + "/" + splitedXpath[1]);
									    //Node nodeInConfigRepo = configRepo.selectSingleNode(sRepoXpath + "/" + splitedXpath[1]);
									    //System.out.println(nodeInConfigRepo.getName() + ":" + nodeInConfigRepo.getText());
									    //Node nodeInconfigRepo = configRepo.selectSingleNode(sXpath2);
									    //nodeInconfigRepo.setText(nodeInConfigRepo.getText());
									} 
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * THis is to parse all providers in target flow, replace all fields with real valid values
	 * @param providers
	 * @param xpath
	 * @throws DocumentException
	 */
	public static void doProviders(Document providers, XPath xpath) throws DocumentException {
		List<Node> pro0NodeList = providers.selectNodes(xpath.getText());
		for (Node node : pro0NodeList) {
			System.out.println("\nCurrent Element :" + node.getName());
			Element element = (Element) node;
			Iterator<Element> iterator = element.elementIterator("provider");
			while (iterator.hasNext()) {
				Element providerElement = (Element) iterator.next();
				List<Node> pro1NodeList = providerElement.selectNodes("*");
				for (Node pro1Node : pro1NodeList) {
					if (pro1Node.getNodeType() == Node.ELEMENT_NODE) {
						Element element1 = (Element) pro1Node;
						String sXpath1 = pro1Node.getUniquePath();
						//System.out.println("XPATH1=" + sXpath1);
						if(element1.isTextOnly()) {
							System.out.println(element1.getName() + "=" + element1.getText());
						} 
						if (isEmpty(providers, sXpath1)) {
							System.out.println("====" + element1.getName() + " is empty.");
							// Convert to Repository file's xPath
							//String sRepoXpath = getXPath4DB(egName, domainName, flowName, appName).getText();
						    //String[] splitedXpath = sXpath1.split("flow\\[\\d\\]\\/");
						    //System.out.println(sRepoXpath + "/" + splitedXpath[1]);
						    //Node nodeInConfigRepo = configRepo.selectSingleNode(sRepoXpath + "/" + splitedXpath[1]);
						    //System.out.println(nodeInConfigRepo.getName() + ":" + nodeInConfigRepo.getText());
						    //Node nodeInconfigRepo = configRepo.selectSingleNode(sXpath1);
						    //nodeInconfigRepo.setText(nodeInConfigRepo.getText());
						} else {
							List<Node> pro2NodeList = element1.selectNodes("*");
							for (Node pro2Node : pro2NodeList) {
								if (pro2Node.getNodeType() == Node.ELEMENT_NODE) {
									Element element2 = (Element) pro2Node;
									if(element2.isTextOnly()) {
										System.out.println(element2.getName() + "=" + element2.getText());
									} 
									String sXpath2 = element2.getUniquePath();
									System.out.println("XPATH2=" + sXpath2);
									if (isEmpty(providers, sXpath2)) {
										System.out.println("====" + element2.getName() + " is empty.");
										// Convert to Repository file's xPath
										//String sRepoXpath = getXPath4DB(egName, domainName, flowName, appName).getText();
									    //String[] splitedXpath = sXpath2.split("flow\\[\\d\\]\\/");
									    //System.out.println(sRepoXpath + "/" + splitedXpath[1]);
									    //Node nodeInConfigRepo = configRepo.selectSingleNode(sRepoXpath + "/" + splitedXpath[1]);
									    //System.out.println(nodeInConfigRepo.getName() + ":" + nodeInConfigRepo.getText());
									    //Node nodeInconfigRepo = configRepo.selectSingleNode(sXpath2);
									    //nodeInconfigRepo.setText(nodeInConfigRepo.getText());
									} 
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Only those element nodes without attributes and contents can return true. If the node has 
	 * attributes but does not have contents, we have to ignore it and won't try to fill in any contents.
	 * @param doc
	 * @param xpath
	 * @return
	 */
	public static boolean isEmpty(Document doc, String xpath) {

		// No xpath string specified
		if (xpath == null || xpath.trim().length() == 0)
			return false;
		
		Node node = doc.selectSingleNode(xpath);

		// Invalid xpath
		if (node == null) {
			//System.out.println(" ... couldn't find node at " + xpath + " returning false");
			return false;
		}

		if (node.getNodeType() != Node.ELEMENT_NODE) {
			//System.out.println("  ...  called with an unknown type of node - returning false");
			return false;
		} 

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element elemNode = (Element) node;
			if (elemNode.attributeCount() > 0) {
				return false;
			}
			
			//System.out.println("Element size="  + elemNode.elements().size());
			// Has children but may not have text
			//System.out.println(elemNode.getName() + "=" + elemNode.getTextTrim());
			if ((elemNode.elements().size() == 0) && (elemNode.getTextTrim().length()== 0)) {
				return true;
			} 
		} 
		return false;
	}

	/**
	 * Check a node has children or not
	 * @param doc
	 * @param xpath
	 * @return
	 */
	public static boolean hasChildren(Document doc, String xpath) {
		boolean retValue = true;
		
		// No xpath string specified
		if (xpath == null || xpath.trim().length() == 0)
			retValue = false;
		
		// Invalid xpath string
		Node node = doc.selectSingleNode(xpath);
		if (node == null) {
			System.out.println("hasChildren() could not find node: (" + xpath + ")");
			retValue = false;
		}
		
		// Node is not an element node
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			System.out.println("hasChildern() called with an non-Element - returning false");
			retValue = false;
		}
		
		// Most common case: element node
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element elemNode = (Element) node;
			// Has text contents as children 
			//if (elemNode.getTextTrim() != null && elemNode.getTextTrim().length() > 0) {
			//	return true;
			//}
			// Has children but may not have text
			retValue = elemNode.elements().size() > 0 || elemNode.attributeCount() > 0;
		}
		return retValue;
	}
}
