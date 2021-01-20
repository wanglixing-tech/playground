package crs.fcl.integration.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import crs.fcl.integration.model.Assets;
import crs.fcl.integration.model.Categorisation;
import crs.fcl.integration.model.CategoryLevel;
import crs.fcl.integration.model.Data;
import crs.fcl.integration.model.Identity;
import crs.fcl.integration.model.Image;
import crs.fcl.integration.model.ItemTypeGroup;
import crs.fcl.integration.model.Language;
import crs.fcl.integration.model.LongTextItems;
import crs.fcl.integration.model.Memo;
import crs.fcl.integration.model.NameLookups;
import crs.fcl.integration.model.NameText;
import crs.fcl.integration.model.NameTextItems;
import crs.fcl.integration.model.NameTextLookups;
import crs.fcl.integration.model.NameValue;
import crs.fcl.integration.model.NameValueText;
import crs.fcl.integration.model.Nutrient;
import crs.fcl.integration.model.Product;
import crs.fcl.integration.model.Statement;
import crs.fcl.integration.model.StructuredNutrition;
import crs.fcl.integration.model.TextualNutrition;
import crs.fcl.integration.model.TextualNutritionNutrient;
import crs.fcl.integration.model.ValueGroupDefinition;
/**
 * This Java class is created for parsing a source XML file, which is supposed to be generated from  
 * Web Service: "https://api.brandbank.com/svc/feed/extractdata.asmx". And then it can be used for:
 * (1) Create a GTIN-PVID list from the source XML file;
 * (2) Find the product based on GTIN-PVID and return a self-defined "Product" object, from which 
 * your program can pickup any property from source XML file as Java object for your processing.
 * 
 * NOTE: The program is using DOM4J combined with XPATH technology for unmarshalling XML into Java
 * Object, the approach is similar with JAXB but use self-defined java classes.
 * 
 * @author Richard.Wang
 * @version 0.1
 */
public class ParseProductDataWithDOM4JXPath {
	static String sourceXMLFileName = null;
	static Document updDoc = null;

	public ParseProductDataWithDOM4JXPath() {
		super();
	}

	/**
	 * Constructor with source XML file
	 * @param sourseFileName Source XML file name
	 * @throws DocumentException Possible exception
	 */
	public ParseProductDataWithDOM4JXPath(String sourseFileName) throws DocumentException {
		sourceXMLFileName = sourseFileName;
		File sourceFile = new File(sourceXMLFileName);
		updDoc = parseXML(sourceFile);
	}

	/**
	 * Collect GTIN with PVID from Document
	 * @return List of GTIN:PVID string
	 */
	public List<String> findAllGTINWithPVID(){
		List <String> gtinWithpvidList = new ArrayList<String>();
		List<Node> gtinNodeList = updDoc.selectNodes("//*[@Scheme='GTIN']");
		
		for(Node gtinNode : gtinNodeList) {
			String gtin = gtinNode.getText();
			String pvid = gtinNode.getParent().selectSingleNode(".//*[@Scheme='BRANDBANK:PVID']").getText();
			gtinWithpvidList.add(gtin + ":" + pvid);
		}
		return gtinWithpvidList;
	}
	
	/**
	 * Find out unique product object by using GTIN and PVID
	 * @param gtin GTIN number
	 * @param pvid BRANDBANK:PVID number
	 * @return The product's object
	 */
	public Product findTargetProduct(String gtin, String pvid) {

		Document targetProddoc = DocumentHelper.createDocument();
		Element dummyRootElem = targetProddoc.addElement("root");
		Node targetProdNode = findTargetProdNode(updDoc, gtin, pvid);
		Element prodElem = ((Element) targetProdNode).createCopy();
		dummyRootElem.add(prodElem);

		Product product = new Product();

		/**
		 * Collect Identity INFO from source XML file and set into Java Object
		 */
		Identity identity = new Identity();
		identity.setTargetMarketCode(findTargetMarketCode(targetProddoc));
		identity.setSubscriptionId(findTargetSubscriptionId(targetProddoc));
		identity.setSubscriptionCode(findTargetSubscriptionCode(targetProddoc));
		identity.setSubscription(findTargetSubscription(targetProddoc));
		identity.setProdGtin(findTargetGTIN(targetProddoc));
		identity.setProdPvid(findTargetPVID(targetProddoc));
		identity.setDiagDescCode(findTargetDiagDescCode(targetProddoc));
		identity.setDiagDesc(findTargetDiagDesc(targetProddoc));
		identity.setDefaultLanguage(findDefaultLanguage(targetProddoc));

		product.setIdentity(identity);

		/**
		 * Collect Assets INFO from source XML file and set into Java Object
		 */
		Assets assets = new Assets();
		assets.setImageList(findImages(targetProddoc));
		product.setAssets(assets);
		/**
		 * Collect Data INFO from source XML file and set into Java object
		 */
		Data data = new Data();
		List<Language> languageList = new ArrayList<Language>();
		List<Node> languageNodeList = findLanguages(targetProddoc);
		for (Node node : languageNodeList) {
			Language language = new Language();
			language.setCode(findLanguageCode(node));
			language.setSource(findLanguageSource(node));
			language.setGroupingSetId(findLanguageGroupingSetId(node));
			language.setGroupingSetName(findLanguageGroupingSetName(node));
			language.setDescription(findLanguageDescription(node));
			language.setCategorisation(findCategorisation(node));
			language.setItemTypeGroup(findItemTypeGroup(node));
			languageList.add(language);
		}
		data.setLanguageList(languageList);
		product.setData(data);
		return product;
	}

	/**
	 * To parse source XML file and upload it as a Document object
	 * @param file XML file
	 * @return Document object
	 * @throws DocumentException possible exceptions
	 */
	private Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	/**
	 * Find out the node in source XML Document by using gtin and pvid as working Document.
	 * @param doc source XML Document
	 * @param gtin GTIN number
	 * @param pvid BRANDBANK:PVID number
	 * @return the target Node in XML Document
	 */
	public Node findTargetProdNode(Document doc, String gtin, String pvid) {
		Node targetProdNode = null;
		String sXPathWithGtin = String.format("//*[@Scheme='GTIN' and text()='%s']", gtin);

		List<Node> nodeListWithGTIN = doc.selectNodes(sXPathWithGtin);
		System.out.println("Total Nodes with " + gtin + "=" + nodeListWithGTIN.size());
		for (Node node : nodeListWithGTIN) {
			String sXPathWithPvid = node.getParent().getUniquePath() + "/Code[@Scheme='BRANDBANK:PVID']";
			Node nodeWithPVID = doc.selectSingleNode(sXPathWithPvid);
			if (nodeWithPVID.getText().equals(pvid)) {
				targetProdNode = nodeWithPVID.getParent().getParent().getParent();
			}
		}
		return targetProdNode;
	}

	private String findTargetMarketCode(Document doc) {
		String sXPath2targetNode = "//*[name()='TargetMarket']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Code");
	}

	private String findTargetSubscriptionId(Document doc) {
		String sXPath2targetNode = "//*[name()='Subscription']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Id");
	}

	private String findTargetSubscriptionCode(Document doc) {
		String sXPath2targetNode = "//*[name()='Subscription']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Code");
	}

	private String findTargetSubscription(Document doc) {
		String sXPath2targetNode = "//*[name()='Subscription']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.getText();
	}

	private String findTargetGTIN(Document doc) {
		String sXPath2targetNode = "//*[name()='Code' and @Scheme='GTIN']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.getText();
	}

	private String findTargetPVID(Document doc) {
		String sXPath2targetNode = "//*[name()='Code' and @Scheme='BRANDBANK:PVID']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.getText();
	}

	private String findTargetDiagDescCode(Document doc) {
		String sXPath2targetNode = "//*[name()='DiagnosticDescription']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Code");
	}

	private String findTargetDiagDesc(Document doc) {
		String sXPath2targetNode = "//*[name()='DiagnosticDescription']";
		Element element = (Element) doc.selectSingleNode(sXPath2targetNode);
		return element.getText();
	}

	private String findDefaultLanguage(Document doc) {
		String sXPath2targetNode = "//*[name()='DefaultLanguage']";
		return doc.selectSingleNode(sXPath2targetNode).getText();
	}

	private List<Image> findImages(Document doc) {
		String sXPath2targetNode = "//*[name()='Image']";
		List<Image> imageList = new ArrayList<Image>();
		List<Node> imageNodeList = doc.selectNodes(sXPath2targetNode);
		System.out.println("Total images: " + imageNodeList.size());
		for (Node node : imageNodeList) {
			Image image = new Image();
			image.setMimeType(findImageMimeType(node));
			image.setShotType(findImageShotType(node));
			image.setShotTypeId(findImageShotTypeId(node));
			image.setSpecCropPadding(findImageSpecCropPadding(node));
			image.setSpecDimUnit(findImageSpecDimUnit(node));
			image.setSpecDimHeight(findImageSpecDimHeight(node));
			image.setSpecDimWidth(findImageSpecDimWidth(node));
			image.setSpecIsCropped(findImageSpecIsCropped(node));
			image.setSpecMaxSizeInByte(findImageSpecMaxSizeInByte(node));
			image.setSpecQuality(findImageSpecQuality(node));
			image.setSpecResolution(findImageSpecResolution(node));
			image.setThumbprint(findImageThumbprint(node));
			image.setUrl(findImageUrl(node));
			imageList.add(image);
		}
		return imageList;
	}

	private String findImageMimeType(Node node) {
		Element element = (Element) node;
		return element.attributeValue("MimeType");
	}

	private String findImageShotType(Node node) {
		Element element = (Element) node;
		return element.attributeValue("ShotType");
	}

	private String findImageShotTypeId(Node node) {
		Element element = (Element) node;
		return element.attributeValue("ShotTypeId");
	}

	private int findImageSpecCropPadding(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/CropPadding");
		return Integer.parseInt(element.getTextTrim());
	}

	private String findImageSpecDimUnit(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/RequestedDimensions");
		return element.attributeValue("Units");
	}

	private int findImageSpecDimHeight(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/RequestedDimensions/Height");
		return Integer.parseInt(element.getTextTrim());
	}

	private int findImageSpecDimWidth(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/RequestedDimensions/Width");
		return Integer.parseInt(element.getTextTrim());
	}

	private int findImageSpecQuality(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/Quality");
		return Integer.parseInt(element.getTextTrim());
	}

	private int findImageSpecMaxSizeInByte(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/MaxSizeInBytes");
		return Integer.parseInt(element.getTextTrim());
	}

	private int findImageSpecResolution(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/Resolution");
		return Integer.parseInt(element.getTextTrim());
	}

	private boolean findImageSpecIsCropped(Node node) {
		Element element = (Element) node.selectSingleNode(".//Specification/IsCropped");
		return element.getTextTrim().equals("true") ? true : false;
	}

	private String findImageThumbprint(Node node) {
		Element element = (Element) node.selectSingleNode(".//Thumbprint");
		return element.getTextTrim();
	}

	private String findImageUrl(Node node) {
		Element element = (Element) node.selectSingleNode(".//Url");
		return element.getTextTrim();
	}

	/***********************************************************************************
	 * Data Language
	 ***********************************************************************************/
	private String findLanguageCode(Node node) {
		Element element = (Element) node;
		return element.attributeValue("Code");
	}
	private String findLanguageSource(Node node) {
		Element element = (Element) node;
		return element.attributeValue("Source");
	}

	private String findLanguageGroupingSetId(Node node) {
		Element element = (Element) node;
		return element.attributeValue("GroupingSetId");
	}

	private String findLanguageGroupingSetName(Node node) {
		Element element = (Element) node;
		return element.attributeValue("GroupingSetName");
	}

	private String findLanguageDescription(Node node) {
		String sXPath2targetNode = ".//*[name()='Description']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.getText();
	}

	private Categorisation findCategorisation(Node node) {
		Categorisation categorisation = new Categorisation();
		categorisation.setScheme(findCategorisationScheme(node));
		categorisation.setLevelList(findCategorisationLevels(node));
		return categorisation;
	}

	private String findCategorisationScheme(Node node) {
		String sXPath2targetNode = ".//Categorisation";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Scheme");
	}

	private List<CategoryLevel> findCategorisationLevels(Node node) {
		String sXPath2targetNode = ".//Categorisation/Level";
		List<CategoryLevel> CategoryLevelList = new ArrayList<CategoryLevel>();
		List<Node> categoryLevelNodeList = node.selectNodes(sXPath2targetNode);
		for (Node clNode : categoryLevelNodeList) {
			CategoryLevel categoryLevel = new CategoryLevel();
			categoryLevel.setCode(((Element) clNode).attributeValue("Code"));
			categoryLevel.setLevel(clNode.getText());
			CategoryLevelList.add(categoryLevel);
		}
		return CategoryLevelList;
	}

	private ItemTypeGroup findItemTypeGroup(Node node) {
		ItemTypeGroup itemTypeGroup = new ItemTypeGroup();
		itemTypeGroup.setId(findItemTypeGroupId(node));
		itemTypeGroup.setName(findItemTypeGroupName(node));
		itemTypeGroup.setStatementList(findStatementList(node));
		itemTypeGroup.setNameLookupsList(findNameLookupsList(node));
		itemTypeGroup.setNameTextItemsList(findNameTextItemList(node));
		itemTypeGroup.setMemoList(findMemoList(node));
		itemTypeGroup.setLongTextItemsList(findLongTextItemsList(node));
		itemTypeGroup.setNameTextLookupsList(findNameTextLookupsList(node));
		itemTypeGroup.setTextualNutritionList(findTextualNutritionList(node));
		itemTypeGroup.setStructuredNutritionList(findStructuredNutritionList(node));
		return itemTypeGroup;
	}

	private List<NameTextItems> findNameTextItemList(Node node){
		String sXPath2targetNode = ".//*[name()='NameTextItems']";
		List<NameTextItems> nameTextItemList = new ArrayList<NameTextItems>();
		List<Node> ntiNodeList = node.selectNodes(sXPath2targetNode);
		for (Node ntiNode : ntiNodeList) {
			NameTextItems nameTextItems = new NameTextItems();
			Element ntiElem = (Element)ntiNode;
			nameTextItems.setId(ntiElem.attributeValue("Id"));
			nameTextItems.setName(ntiElem.attributeValue("Name"));
			nameTextItems.setNameTextList(findNameText(ntiNode));
			nameTextItemList.add(nameTextItems);
		}
		return nameTextItemList;
		
	}
	private List<NameText> findNameText(Node node) {
		String sXPath2targetNode = ".//*[name()='NameText']";
		List<Node> ntNodeList = node.selectNodes(sXPath2targetNode);
		List<NameText> nameTextList = new ArrayList<NameText>();		
		for (Node ntn : ntNodeList) {
			NameText nt = new NameText();
			Element ntnElem = (Element)ntn.selectSingleNode(".//*[name()='Name']");
			nt.setName(ntnElem.getText());
			nt.setNameId(ntnElem.attributeValue("Id"));
			nt.setText(ntn.selectSingleNode(".//*[name()='Text']").getText());
			nameTextList.add(nt);
		}
		return nameTextList;
	}

	private String findItemTypeGroupId(Node node) {
		String sXPath2targetNode = ".//*[name()='ItemTypeGroup']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Id");

	}

	private String findItemTypeGroupName(Node node) {
		String sXPath2targetNode = ".//*[name()='ItemTypeGroup']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Name");

	}

	private List<Statement> findStatementList(Node node) {
		String sXPath2targetNode = ".//*[name()='Statement']";
		List<Statement> statementList = new ArrayList<Statement>();
		List<Node> statementNodeList = node.selectNodes(sXPath2targetNode);
		for (Node sNode : statementNodeList) {
			Statement statement = new Statement();
			statement.setId(findStatementId(sNode));
			statement.setContent(findStatementContent(sNode));
			statementList.add(statement);
		}
		return statementList;
	}

	private List<NameLookups> findNameLookupsList(Node node) {
		String sXPath2targetNode = ".//*[name()='NameLookups']";
		List<NameLookups> nameLookupsList = new ArrayList<NameLookups>();
		List<Node> nameLookupsNodeList = node.selectNodes(sXPath2targetNode);
		for (Node nlNode : nameLookupsNodeList) {
			NameLookups nl = new NameLookups();
			Element nlElem = (Element) nlNode;
			nlElem.attributeValue("Id");
			nl.setId(nlElem.attributeValue("Id"));
			nl.setName(nlElem.attributeValue("Name"));
			nl.setNameValueList(findNameValueList(nlNode));
			nameLookupsList.add(nl);
		}
		return nameLookupsList;
	}

	private List<NameValue> findNameValueList(Node node) {
		String sXPath2targetNode = ".//*[name()='NameValue']";
		List<Node> nameValueNodeList = node.selectNodes(sXPath2targetNode);
		List<NameValue> nameValueList = new ArrayList<NameValue>();
		for (Node nvNode : nameValueNodeList) {
			NameValue nv = new NameValue();
			Element vnnElem = (Element) nvNode.selectSingleNode(".//*[name()='Name']");
			nv.setName(vnnElem.getText());
			nv.setNameId(vnnElem.attributeValue("Id"));
			Element vnvElem = (Element) nvNode.selectSingleNode(".//*[name()='Value']");
			nv.setValue(vnvElem.getText());
			nv.setValueId(vnvElem.attributeValue("Id"));
			nameValueList.add(nv);
		}
		return nameValueList;
	}

	private List<NameTextLookups> findNameTextLookupsList(Node node) {
		String sXPath2targetNode = ".//*[name()='NameTextLookups']";
		List<NameTextLookups> nameTextLookupList = new ArrayList<NameTextLookups>();
		List<Node> nameTextLookupsNodeList = node.selectNodes(sXPath2targetNode);
		for (Node ntlNode : nameTextLookupsNodeList) {
			NameTextLookups ntl = new NameTextLookups();
			Element ntlElem = (Element) ntlNode;
			ntl.setId(ntlElem.attributeValue("Id"));
			ntl.setName(ntlElem.attributeValue("Name"));
			ntl.setNameValueTextList(findNameValueTextList(node));
			nameTextLookupList.add(ntl);
		}
		return nameTextLookupList;
	}

	private List<LongTextItems> findLongTextItemsList(Node node) {
		String sXPath2targetNode = ".//*[name()='LongTextItems']";
		List<LongTextItems> longTextItemsList = new ArrayList<LongTextItems>();
		List<Node> longTextItemsNodeList = node.selectNodes(sXPath2targetNode);
		for (Node ltiNode : longTextItemsNodeList) {
			LongTextItems lti = new LongTextItems();
			Element ltiElem = (Element) ltiNode;
			lti.setId(ltiElem.attributeValue("Id"));
			lti.setName(ltiElem.attributeValue("Name"));
			lti.setText(findLongTextItemsText(ltiNode));

			longTextItemsList.add(lti);
		}
		return longTextItemsList;
	}

	private List<Memo> findMemoList(Node node) {
		String sXPath2targetNode = ".//*[name()='Memo']";
		List<Memo> memoList = new ArrayList<Memo>();
		List<Node> memoNodeList = node.selectNodes(sXPath2targetNode);
		for (Node mNode : memoNodeList) {
			Memo memo = new Memo();
			memo.setId(findMemoId(mNode));
			memo.setName(findMemoName(mNode));
			memo.setContent(findMemoContent(mNode));
			memoList.add(memo);
		}
		return memoList;
	}

	private List<TextualNutrition> findTextualNutritionList(Node node) {
		String sXPath2targetNode = ".//*[name()='TextualNutrition']";
		List<TextualNutrition> textualNutritionList = new ArrayList<TextualNutrition>();
		List<Node> textualNutritionNodeList = node.selectNodes(sXPath2targetNode);
		for (Node tnNode : textualNutritionNodeList) {
			TextualNutrition tn = new TextualNutrition();
			Element tnElem = (Element) tnNode;
			tn.setId(tnElem.attributeValue("Id"));
			tn.setName(tnElem.attributeValue("Name"));
			tn.setHeadings(findHeadings(node));
			tn.setTextualNutritionNutrientList(findTextualNutritionNutrients(node));
			textualNutritionList.add(tn);
		}
		return textualNutritionList;
	}

	private List<String> findHeadings(Node node) {
		String sXPath2targetNode = ".//*[name()='Heading']";
		List<Node> hNodeList = node.selectNodes(sXPath2targetNode);
		ArrayList<String> headingList = new ArrayList<String>();

		for (Node hNode : hNodeList) {
			headingList.add(hNode.getText());
		}

		return headingList;
	}

	private List<TextualNutritionNutrient> findTextualNutritionNutrients(Node node) {
		String sXPath2targetNode = ".//*[name()='Nutrient']";
		ArrayList<TextualNutritionNutrient> tnNutrientList = new ArrayList<TextualNutritionNutrient>();
		List<Node> tnNodeList = node.selectNodes(sXPath2targetNode);
		for (Node tn : tnNodeList) {
			TextualNutritionNutrient tnn = new TextualNutritionNutrient();

			String nutrientName = tn.selectSingleNode(".//*[name()='Name']").getText();
			List<Node> valueNodeList = tn.selectNodes(".//*[name()='Value']");
			ArrayList<String> valueList = new ArrayList<String>();
			for (Node valueNode : valueNodeList) {
				String destXPath = valueNode.getUniquePath();
				if (!hasChildren(valueNode, destXPath)) {
					valueList.add("NULL");
				} else {
					valueList.add(valueNode.getText());
				}
			}
			tnn.setName(nutrientName);
			tnn.setValueList(valueList);
			tnNutrientList.add(tnn);
		}
		return tnNutrientList;
	}

	private String findStatementId(Node node) {
		Element element = (Element) node;
		return element.attributeValue("Id");

	}

	private String findStatementContent(Node node) {
		Element element = (Element) node;
		return element.getText();

	}

	private List<Node> findLanguages(Document doc) {
		String sXPath2targetNode = ".//*[name()='Language']";
		List<Node> languageNodeList = doc.selectNodes(sXPath2targetNode);
		return languageNodeList;
	}

	private String findMemoId(Node node) {
		Element element = (Element) node;
		return element.attributeValue("Id");

	}

	private String findMemoName(Node node) {
		Element element = (Element) node;
		return element.attributeValue("Name");

	}

	private String findMemoContent(Node node) {
		Element element = (Element) node;
		return element.getText();

	}

	private String findLongTextItemsText(Node node) {
		String sXPath2targetNode = ".//*[name()='Text']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.getText();
	}

	private List<StructuredNutrition> findStructuredNutritionList(Node node) {
		String sXPath2targetNode = ".//*[name()='StructuredNutrition']";
		List<StructuredNutrition> structuredNutritionList = new ArrayList<StructuredNutrition>();
		List<Node> structuredNutritionNodeList = node.selectNodes(sXPath2targetNode);
		for (Node snNode : structuredNutritionNodeList) {
			StructuredNutrition sn = new StructuredNutrition();
			Element tnElem = (Element) snNode;
			sn.setId(tnElem.attributeValue("Id"));
			sn.setName(tnElem.attributeValue("Name"));
			sn.setNutrientList(findNutrients(snNode));
			sn.setValueGroupDefinitionList(findValueGroupDefinitions(snNode));
			structuredNutritionList.add(sn);
		}
		return structuredNutritionList;
	}

	private List<Nutrient> findNutrients(Node node) {
		ArrayList<Nutrient> nutrientList = new ArrayList<Nutrient>();
		String sXPath2targetNode = ".//*[name()='Nutrient']";
		List<Node> NutrientNodeList = node.selectNodes(sXPath2targetNode);
		for (Node nutrientNode : NutrientNodeList) {
			Nutrient nu = new Nutrient();
			Element nutrientElem = (Element) nutrientNode;
			nu.setId(nutrientElem.attributeValue("Id"));
			nu.setName(nutrientElem.attributeValue("Name"));
			nu.setUnitId(findUnitId(nutrientNode));
			nu.setUnitAbbr(findUnitAbbr(nutrientNode));
			nu.setUnitName(findUnitName(nutrientNode));
			nu.setValueGroupId(findValueGroupId(nutrientNode));
			nu.setValueGroupName(findValueGroupName(nutrientNode));
			nu.setValueGroupAmountValue(findValueGroupAmountValue(nutrientNode));
			nu.setValueGroupReferenceIntakeValue(findValueGroupReferenceIntakeValue(nutrientNode));
			nutrientList.add(nu);
		}
		return nutrientList;
	}

	private String findUnitId(Node node) {
		String sXPath2targetNode = ".//*[name()='Unit']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Id");
	}

	private String findUnitAbbr(Node node) {
		String sXPath2targetNode = ".//*[name()='Unit']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Abbreviation");
	}

	private String findUnitName(Node node) {
		String sXPath2targetNode = ".//*[name()='Unit']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Name");
	}

	private String findValueGroupId(Node node) {
		String sXPath2targetNode = ".//*[name()='ValueGroup']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Id");
	}

	private String findValueGroupName(Node node) {
		String sXPath2targetNode = ".//*[name()='ValueGroup']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.attributeValue("Name");
	}

	private String findValueGroupAmountValue(Node node) {
		String sXPath2targetNode = ".//*[name()='Amount']";
		Node vgaNode = node.selectSingleNode(sXPath2targetNode);
		String destXPath = vgaNode.getUniquePath() + "/Value";
		if (!hasChildren(vgaNode, destXPath)) {
			return "NULL";
		} else {
			Node vgrvNode = vgaNode.selectSingleNode(".//*[name()='Value']");
			return vgrvNode.getText();
		}
	}

	private String findValueGroupReferenceIntakeValue(Node node) {
		String sXPath2targetNode = ".//*[name()='ReferenceIntake']";
		Node vgrNode = node.selectSingleNode(sXPath2targetNode);
		String destXPath = vgrNode.getUniquePath() + "/Value";
		if (!hasChildren(vgrNode, destXPath)) {
			return "NULL";
		} else {
			Node vgrvNode = vgrNode.selectSingleNode(".//*[name()='Value']");
			return vgrvNode.getText();
		}
	}

	private List<ValueGroupDefinition> findValueGroupDefinitions(Node node) {
		ArrayList<ValueGroupDefinition> valueGroupDefinitionList = new ArrayList<ValueGroupDefinition>();
		String sXPath2targetNode = ".//*[name()='ValueGroupDefinition']";
		List<Node> ValueGroupDefinitionList = node.selectNodes(sXPath2targetNode);
		for (Node vgNode : ValueGroupDefinitionList) {
			ValueGroupDefinition vg = new ValueGroupDefinition();
			Element nutrientElem = (Element) vgNode;
			vg.setId(nutrientElem.attributeValue("Id"));
			vg.setName(nutrientElem.attributeValue("Name"));
			vg.setAmountHeader(findAmountHeader(vgNode));
			vg.setReferenceIntakeHeader(findReferenceIntakeHeader(vgNode));
			valueGroupDefinitionList.add(vg);
		}
		return valueGroupDefinitionList;
	}

	private String findAmountHeader(Node node) {
		String sXPath2targetNode = ".//*[name()='AmountHeader']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.getText();

	}

	private String findReferenceIntakeHeader(Node node) {
		String sXPath2targetNode = ".//*[name()='ReferenceIntakeHeader']";
		Element element = (Element) node.selectSingleNode(sXPath2targetNode);
		return element.getText();

	}

	private List<NameValueText> findNameValueTextList(Node node) {
		String sXPath2targetNode = ".//*[name()='NameValueText']";
		ArrayList<NameValueText> nvtList = new ArrayList<NameValueText>();
		List<Node> nvtNodeList = node.selectNodes(sXPath2targetNode);
		for (Node nvtNode : nvtNodeList) {
			NameValueText nvt = new NameValueText();
			nvt.setName(nvtNode.selectSingleNode(".//*[name()='Name']").getText());
			nvt.setNameId(((Element) nvtNode.selectSingleNode(".//*[name()='Name']")).attributeValue("Id"));
			nvt.setValue(nvtNode.selectSingleNode(".//*[name()='Value']").getText());
			nvt.setValueId(((Element) nvtNode.selectSingleNode(".//*[name()='Value']")).attributeValue("Id"));
			nvt.setText(nvtNode.selectSingleNode(".//*[name()='Text']").getText());
			nvtList.add(nvt);
		}
		return nvtList;
	}

	/**
	 * Return true if the specified node is an Element and has either a sub-element,
	 * or an attribute (even if they are empty), OR content.
	 *
	 * @param xpath xpath to the node to be evaluated for children
	 * @return true if sub-elements, or attributes, false otherwise or if node is
	 *         not an Element
	 */
	public boolean hasChildren(Node node, String xpath) {
		if (xpath == null || xpath.trim().length() == 0)
			return false;
		Node childNode = node.selectSingleNode(xpath);
		if (childNode == null) {
			System.out.println("Could not find the target node: (" + xpath + ")");
			return false;
		}

		if (childNode.getNodeType() != Node.ELEMENT_NODE) {
			System.out.println("Target node " + xpath + " does not have any element");
			return false;
		}

		Element e = (Element) childNode;
		boolean hasText = (e.getTextTrim() != null && e.getTextTrim().length() > 0);
		if (hasText)
			return true;
		//System.out.println("Target node " + xpath + " does not have any element");

		return (e.elements().size() > 0 || e.attributeCount() > 0);
	}

	/**
	 * Returns true if an element (recursively) has no textual content, no children,
	 * and no attributes with values.
	 * <p>
	 *
	 * Note: returns FALSE if no node exists at the given path.
	 *
	 * @param xpath Description of the Parameter
	 * @return true if empty, false if any errors are encountered
	 */
	public boolean isEmpty(Node node, String xpath) {
		Node childNode = node.selectSingleNode(xpath);
		if (childNode == null) {
			System.out.println(" ... couldn't find node at " + xpath);
			return false;
		}

		if (childNode.getNodeType() == Node.ATTRIBUTE_NODE) {
			String content = node.getText();
			return (content == null || content.length() == 0);
		}

		if (childNode.getNodeType() == Node.ELEMENT_NODE) {
			String content = node.getText();
			return (content == null || content.length() == 0);
		} else {
			System.out.println(" This is a node with unknown node type:" + xpath);
			return false;
		}
	}
}
