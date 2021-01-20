package crs.fcl.integration.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlStringImpl;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import noNamespace.MessageDocument;
import noNamespace.NutrientType;
import noNamespace.ProductType;
import noNamespace.ValuesType;
import noNamespace.impl.LookupTypeImpl;
import noNamespace.impl.LookupWithCodeTypeImpl;
import noNamespace.impl.NutrientTypeImpl;
import noNamespace.impl.ProductCodeTypeImpl;
import noNamespace.impl.SubscriptionTypeImpl;
import noNamespace.impl.TextTypeImpl;
import noNamespace.impl.TextualNutritionTypeImpl;
import noNamespace.impl.ValuesTypeImpl;

public class GenerateUnsentProductDataByXMLBeans {
	public final static String languageCode = "en-CA";
	public final static String nsDeclaration = "declare namespace xs='http://www.w3.org/2001/XMLSchema';";

	/**
	 * TODO The dataFilesHome need to be changed if you are dealing with different
	 * data!!!!!
	 */
	public final static String dataFilesHome = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Data-2019.07.25.15.19.28";
	public final static String dataFilesPrefixName = "response-getUnsentProdData";
	public final static String workingXmlFile = "C:\\Temp\\working.xml";
	public final static String OUTPUT_FILE_NAME = "output.txt";
	public final static Charset ENCODING = StandardCharsets.UTF_8;

	public static void main(String args[]) {
		try {
			File dir = new File(dataFilesHome);
			RegexFileFilter filter = new RegexFileFilter(dataFilesPrefixName + "[0-9]?[0-9]?[0-9]?.xml");
			List<File> unsentDataFiles = (List<File>) FileUtils.listFiles(dir, filter, TrueFileFilter.INSTANCE);
			Collections.sort(unsentDataFiles);
			for (File file : unsentDataFiles) {
				System.out.println("file: " + file.getCanonicalPath());

				/**
				 * !!!! Here, we can't directly pass the file to XMLBeans parser as it is
				 * because the data is in the envelop. We have to tear the envelop down and save
				 * the content in the body to a temporary XML file or create XMLInputStream
				 */
				buildMessageDocument(file);

				MessageDocument msgDoc = MessageDocument.Factory.parse(new File(workingXmlFile));

				// cursor = msgDoc.newCursor();
				// System.out.println(cursor.getTextValue());
				ProductType[] productArray = findAllProducts(msgDoc);

				System.out.println("Total products number:" + productArray.length);
				if (productArray.length > 0) {
					String absolutePath2OutputFile = dataFilesHome + File.separator + OUTPUT_FILE_NAME;
					writeTextFile(absolutePath2OutputFile, UnsentProduct.titleRecord());
					for (int i = 0; i < productArray.length; i++) {
						UnsentProduct gs1 = new UnsentProduct();
						gs1.setGtin(getGTIN(productArray[i]));
						gs1.setBrandbankPvid(getBrandbankPvid(productArray[i]));
						gs1.setSubscriberGln(getSubscriberGln(productArray[i]));
						gs1.setSubscriptionCode(getSubscriptionCode(productArray[i]));

						gs1.setBrandOwnerName(getBrandOwnerName(productArray[i]));
						gs1.setNormalizedProdDesc(getNormalizedProdDesc(productArray[i]));
						gs1.setManufacturerBrand(getManufacturerBrand(productArray[i]));
						gs1.setBrandName(getBrandName(productArray[i]));
						gs1.setSubBrandName(getSubBrandName(productArray[i]));
						gs1.setFunctionalName(getFunctionalName(productArray[i]));
						gs1.setVariant(getVariant(productArray[i]));
						gs1.setNetContent(getNetContent(productArray[i]));
						gs1.setSize(getSize(productArray[i]));
						gs1.setSizeUOM(getSizeUOM(productArray[i]));
						PackageMeasurement pm = getPackageMeasurement(productArray[i]);
						gs1.setHeight(pm.getHeight());
						gs1.setHeightUOM(pm.getHeightUOM());
						gs1.setWidth(pm.getWidth());
						gs1.setWidthUOM(pm.getWidthUOM());
						gs1.setDepth(pm.getDepth());
						gs1.setDepthUOM(pm.getDepthUOM());
						gs1.setGrossWeight(pm.getGrossWeight());
						gs1.setGrossWeightUOM(pm.getGrossWeightUOM());
						gs1.setTextualNutritionNutrientSodium(getTextualNutritionNutrientSodium(productArray[i]));
						gs1.setIngredients(getIngredients(productArray[i]));
						// System.out.println(gs1.toString());
						writeTextFile(absolutePath2OutputFile, gs1.toFalttenRecord());
						// System.out.println("-----------------------------------------------");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static ProductType[] findAllProducts(MessageDocument msgDoc) {
		return msgDoc.getMessage().getProductArray();
	}

	public static String getGTIN(XmlObject prodDoc) {
		String gtin = "N/A";
		String pathExpression = "$this/Identity/ProductCodes/Code[@Scheme='GTIN']";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			gtin = "N/A";
		} else {
			ProductCodeTypeImpl node = (ProductCodeTypeImpl) results[0];
			gtin = node.getStringValue();
		}
		return gtin;
	}

	public static String getBrandbankPvid(XmlObject prodDoc) {
		String brandbankPvid = "N/A";
		String pathExpression = "$this/Identity/ProductCodes/Code[@Scheme='BRANDBANK:PVID']";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			brandbankPvid = "N/A";
		} else {
			ProductCodeTypeImpl node = (ProductCodeTypeImpl) results[0];
			brandbankPvid = node.getStringValue();
		}
		return brandbankPvid;
	}

	public static String getSubscriberGln(XmlObject prodDoc) {
		String subscriberGln = "N/A";
		String pathExpression = "$this/Identity/ProductCodes/Code[@Scheme='SUBSCRIBER:GLN']";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			subscriberGln = "N/A";
		} else {
			ProductCodeTypeImpl node = (ProductCodeTypeImpl) results[0];
			subscriberGln = node.getStringValue();
		}
		return subscriberGln;
	}

	public static String getSubscriptionCode(XmlObject prodDoc) {
		String subscriptionCode = "N/A";
		String pathExpression = "$this/Identity/Subscription";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			subscriptionCode = "N/A";
		} else {
			SubscriptionTypeImpl node = (SubscriptionTypeImpl) results[0];
			subscriptionCode = node.getCode();
		}
		return subscriptionCode;
	}

	public static String getBrandOwnerName(XmlObject prodDoc) {
		String brandOwnerName = "N/A";
		String pathExpression = "$this/Identity/Subscription";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			brandOwnerName = "N/A";
		} else {
			SubscriptionTypeImpl node = (SubscriptionTypeImpl) results[0];
			brandOwnerName = node.getStringValue();
		}
		return brandOwnerName;
	}

	public static String getNormalizedProdDesc(XmlObject prodDoc) {
		String normalizedProdDesc = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/Description";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			normalizedProdDesc = "N/A";
		} else {
			XmlStringImpl node = (XmlStringImpl) results[0];
			normalizedProdDesc = node.getStringValue();
		}
		return normalizedProdDesc;
	}

	public static String getManufacturerBrand(XmlObject prodDoc) {
		String manufacturerBrand = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameLookups[@Name='Manufacturer Brand']/NameValue/Value";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			manufacturerBrand = "N/A";
		} else {
			LookupWithCodeTypeImpl node = (LookupWithCodeTypeImpl) results[0];
			manufacturerBrand = node.getStringValue();
		}
		return manufacturerBrand;
	}

	public static String getBrandName(XmlObject prodDoc) {
		String brandName = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/LongTextItems[@Name='Brand Name']/Text";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			brandName = "N/A";
		} else {
			TextTypeImpl node = (TextTypeImpl) results[0];
			brandName = node.getStringValue();
		}
		return brandName;
	}

	public static String getSubBrandName(XmlObject prodDoc) {
		String subBrandName = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameTextItems[@Name='Sub Brand Name']/NameText/Text";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			subBrandName = "N/A";
		} else {
			TextTypeImpl node = (TextTypeImpl) results[0];
			subBrandName = node.getStringValue();
		}
		return subBrandName;
	}

	public static String getFunctionalName(XmlObject prodDoc) {
		String functionalName = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameTextItems[@Name='Functional Description']/NameText/Text";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			functionalName = "N/A";
		} else {
			TextTypeImpl node = (TextTypeImpl) results[0];
			functionalName = node.getStringValue();
		}
		return functionalName;
	}

	public static String getVariant(XmlObject prodDoc) {
		String variant = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameTextItems[@Name='Functional Description']/NameText/Text";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			variant = "N/A";
		} else {
			TextTypeImpl node = (TextTypeImpl) results[0];
			variant = node.getStringValue();
		}
		return variant;
	}

	public static String getNetContent(XmlObject prodDoc) {
		String netContent = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameTextItems[@Name='Net Content']/NameText/Text";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			netContent = "N/A";
		} else {
			TextTypeImpl node = (TextTypeImpl) results[0];
			netContent = node.getStringValue();
		}
		return netContent;
	}

	public static String getSize(XmlObject prodDoc) {
		String size = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameTextItems[@Name='Size']/NameText/Text";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			size = "N/A";
		} else {
			TextTypeImpl node = (TextTypeImpl) results[0];
			size = node.getStringValue();
		}
		return size;
	}

	public static String getSizeUOM(XmlObject prodDoc) {
		String sizeUOM = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameLookups[@Name='Size UOM']/NameValue/Value";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			sizeUOM = "N/A";
		} else {
			LookupWithCodeTypeImpl node = (LookupWithCodeTypeImpl) results[0];
			sizeUOM = node.getStringValue();
		}
		return sizeUOM;
	}

	public static String getTextualNutritionNutrientSodium(XmlObject prodDoc) {
		StringBuilder str = new StringBuilder();;
		String finalResult = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/TextualNutrition/Nutrient";
		XmlObject[] results = prodDoc.selectPath(pathExpression);

		if (results == null || results.length == 0) {
			finalResult = "N/A";
		} else {
			for (XmlObject nt : results) {
				String nutrientName = getNutrientName(nt);
				if (nutrientName.equals("Sodium")) {
					List<String> soliumValues = getNutrientValues(nt);
					//System.out.println("Nutrient Content:" + soliumValues.get(0));
					//System.out.println("Nutrient Percentage:" + soliumValues.get(1));
					for (String v : soliumValues) {
						str.append(v);
						str.append('|');
					}
					// Remove last '|'
					finalResult = str.deleteCharAt(str.lastIndexOf("|")).toString();
				}
			}
		}
		return finalResult;
	}

	public static PackageMeasurement getPackageMeasurement(XmlObject prodDoc) {
		PackageMeasurement pm = new PackageMeasurement();
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameTextLookups[@Name='Package Measurement']/NameValueText";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		if (results == null || results.length == 0) {
			return pm;
		} else {
			for (XmlObject nvtObj : results) {
				String pmName = getPackageMeasurementName(nvtObj);
				// System.out.println("Package Measurement Name: " + pmName);
				String pmValue = getPackageMeasurementValue(nvtObj);
				// System.out.println("Package Measurement Value: " + pmValue);
				String pmText = getPackageMeasurementText(nvtObj);
				// System.out.println("Package Measurement Text: " + pmText);
				switch (pmName) {
				case "Height":
					pm.setHeight(pmText);
					pm.setHeightUOM(pmValue);
					break;
				case "Width":
					pm.setWidth(pmText);
					pm.setWidthUOM(pmValue);
					break;
				case "Depth":
					pm.setDepth(pmText);
					pm.setDepthUOM(pmValue);
					break;
				case "Gross Weight":
					pm.setGrossWeight(pmText);
					pm.setGrossWeightUOM(pmValue);
					break;
				}
			}
		}
		return pm;
	}

	private static String getPackageMeasurementName(XmlObject nvtObj) {
		String pathExpression = "$this/Name";
		XmlObject[] results = nvtObj.selectPath(pathExpression);
		LookupTypeImpl node = (LookupTypeImpl) results[0];
		return node.getStringValue();
	}

	private static String getPackageMeasurementValue(XmlObject nvtObj) {
		String pathExpression = "$this/Value";
		XmlObject[] results = nvtObj.selectPath(pathExpression);
		LookupWithCodeTypeImpl node = (LookupWithCodeTypeImpl) results[0];
		return node.getStringValue();
	}

	private static String getPackageMeasurementText(XmlObject nvtObj) {
		String pathExpression = "$this/Text";
		XmlObject[] results = nvtObj.selectPath(pathExpression);
		TextTypeImpl node = (TextTypeImpl) results[0];
		return node.getStringValue();
	}

	private static String getNutrientName(XmlObject nvtObj) {
		String pathExpression = "$this/Name";
		XmlObject[] results = nvtObj.selectPath(pathExpression);
		XmlStringImpl node = (XmlStringImpl) results[0];
		return node.getStringValue();
	}

	private static List<String> getNutrientValues(XmlObject nvtObj) {
		List<String> sodiumValues = new ArrayList<String>();
		String pathExpression = "$this/Values";
		XmlObject[] results = nvtObj.selectPath(pathExpression);
		for (XmlObject v : results) {
			ValuesType vt = (ValuesType)v;
			String[] svs = vt.getValueArray();
			for (String sv : svs ) {
				//System.out.println("Sodium Value=" +sv);				
				sodiumValues.add(sv);
			}
		}
		return sodiumValues;
	}

	public static String getIngredients(XmlObject prodDoc) {
		String ingredient = "N/A";
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/LongTextItems[@Name='Ingredients/Key Active Ingredients']/Text";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		if (results == null || results.length == 0) {
			ingredient = "N/A";
		} else {
			TextTypeImpl node = (TextTypeImpl) results[0];
			ingredient = node.getStringValue();
		}
		return ingredient;
	}

	private static void buildMessageDocument(File file) throws DocumentException {
		try {
			SAXReader reader = new SAXReader();
			Document unsentProductSOAPResult = reader.read(file);

			String sXPath2targetDocument = ".//*[name()='Message']";
			Element element = ((Element) unsentProductSOAPResult.selectSingleNode(sXPath2targetDocument)).createCopy();
			Document document = DocumentHelper.createDocument(element);
			// document.setXMLEncoding("ISO-8859-1");
			// Create a temporary xml file
			FileOutputStream fos = new FileOutputStream(workingXmlFile);
			// Create the pretty print of xml document.
			// OutputFormat format = OutputFormat.createCompactFormat();
			// OutputFormat outFormat = new OutputFormat();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setNewLineAfterDeclaration(false);
			format.setIndentSize(2);
			format.setEncoding("ISO-8859-1");

			// Create the xml writer by passing outputstream and format
			XMLWriter writer = new XMLWriter(fos, format);
			// Write to the xml document
			writer.write(document);
			// Flush after done
			writer.flush();
			writer.close();
			fos.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void writeTextFile(String fileName, String oneProduct) throws IOException {
		File file = new File(fileName);
		FileWriter fr = new FileWriter(file, true);
		BufferedWriter br = new BufferedWriter(fr);
		br.write(oneProduct);
		br.close();
		fr.close();
	}

}
