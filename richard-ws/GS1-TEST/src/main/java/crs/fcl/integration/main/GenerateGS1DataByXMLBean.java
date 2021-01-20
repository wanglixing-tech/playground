package crs.fcl.integration.main;

import java.io.File;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlStringImpl;

import noNamespace.MessageDocument;
import noNamespace.ProductType;
import noNamespace.impl.LookupTypeImpl;
import noNamespace.impl.LookupWithCodeTypeImpl;
import noNamespace.impl.ProductCodeTypeImpl;
import noNamespace.impl.SubscriptionTypeImpl;
import noNamespace.impl.TextTypeImpl;

public class GenerateGS1DataByXMLBean {
	public final static String languageCode = "en-CA";
	public final static String nsDeclaration = "declare namespace xs='http://www.w3.org/2001/XMLSchema';";

	public static void main(String args[]) {
		XmlCursor cursor = null;
		try {
			String filePath = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Data\\response-getUnsentProdData1.xml";
			File inputXMLFile = new File(filePath);
			MessageDocument msgDoc = MessageDocument.Factory.parse(inputXMLFile);

			cursor = msgDoc.newCursor();
			// System.out.println(cursor.getTextValue());
			ProductType[] productArray = findAllProducts(msgDoc);

			System.out.println("Total products number:" + productArray.length);
			for (int i = 0; i < productArray.length; i++) {
				GS1 gs1 = new GS1();
				gs1.setGtin(getGTIN(productArray[i]));
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
				gs1.setIngredients(getIngredients(productArray[i]));
				System.out.println(gs1.toString());
				System.out.println("-----------------------------------------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.dispose();
		}
	}

	public static ProductType[] findAllProducts(MessageDocument msgDoc) {
		return msgDoc.getMessage().getProductArray();
	}

	public static String getGTIN(XmlObject prodDoc) {
		String pathExpression = "$this/Identity/ProductCodes/Code[@Scheme='GTIN']";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		ProductCodeTypeImpl node = (ProductCodeTypeImpl) results[0];
		return node.getStringValue();
	}

	public static String getBrandOwnerName(XmlObject prodDoc) {
		String pathExpression = "$this/Identity/Subscription";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		SubscriptionTypeImpl node = (SubscriptionTypeImpl) results[0];
		return node.getStringValue();
	}

	public static String getNormalizedProdDesc(XmlObject prodDoc) {
		String pathExpression = "$this/Data/Language[@Code='en-CA']/Description";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		XmlStringImpl node = (XmlStringImpl) results[0];
		return node.getStringValue();
	}

	public static String getManufacturerBrand(XmlObject prodDoc) {
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameLookups[@Name='Manufacturer Brand']/NameValue/Value";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		// System.out.println("Results length=" + results.length);
		LookupWithCodeTypeImpl node = (LookupWithCodeTypeImpl) results[0];
		return node.getStringValue();
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

	public static PackageMeasurement getPackageMeasurement(XmlObject prodDoc) {
		PackageMeasurement pm = new PackageMeasurement();
		String pathExpression = "$this/Data/Language[@Code='en-CA']/ItemTypeGroup/NameTextLookups[@Name='Package Measurement']/NameValueText";
		XmlObject[] results = prodDoc.selectPath(pathExpression);
		if (results == null || results.length == 0) {
			return pm;
		} else {
			for (XmlObject nvtObj : results) {
				String pmName = getPackageMeasurementName(nvtObj);
				//System.out.println("Package Measurement Name: " + pmName);
				String pmValue = getPackageMeasurementValue(nvtObj);
				//System.out.println("Package Measurement Value: " + pmValue);
				String pmText = getPackageMeasurementText(nvtObj);
				//System.out.println("Package Measurement Text: " + pmText);
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

}
