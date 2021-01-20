package crs.fcl.integration.main;

import java.util.List;

import org.dom4j.DocumentException;

import crs.fcl.integration.model.GS1;
import crs.fcl.integration.model.LongTextItems;
import crs.fcl.integration.model.NameLookups;
import crs.fcl.integration.model.NameText;
import crs.fcl.integration.model.NameTextItems;
import crs.fcl.integration.model.NameTextLookups;
import crs.fcl.integration.model.NameValueText;
import crs.fcl.integration.model.Product;

public class GenerateGS1DataByDOM4JXPath {

	public static void main(String[] args) throws DocumentException {
		final String unsentProductsData = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Data\\response-getUnsentProdData2_ISO.xml";
		ParseProductDataWithDOM4JXPath ppd = new ParseProductDataWithDOM4JXPath(unsentProductsData);
		//List<String> gtinWithPvidList = ppd.findAllGTINWithPVID();
		//showGTINWithPvid(gtinWithPvidList);
		String gtin = "0841058006633";
		String pvid = "5893694";
		//String gtin = "0060025768620";
		//String pvid = "5895255";
		
		Product targetProd = ppd.findTargetProduct(gtin, pvid);
		showTargetProd(targetProd);
	}

	public static void showGTINWithPvid(List<String> gtinWithPvidList) {
		for (String gtinPLusPvid : gtinWithPvidList) {
			String[] gtinPvid = gtinPLusPvid.split(":");
			System.out.println(gtinPvid[0] + "-" + gtinPvid[1]);
		}
	}

	public static void showTargetProd(Product targetProd) {
		
		GS1 gs1object = new GS1();
		gs1object.setBrandOwnerName(targetProd.getIdentity().getSubscription());
		
		gs1object.setGtin(targetProd.getIdentity().getProdGtin());
		
		gs1object.setNormalizedProdDesc(targetProd.getData().getLanguageByCode("en-CA").getDescription());
		
		List<NameLookups> nlList = targetProd.getData().getLanguageByCode("en-CA").getItemTypeGroup().getNameLookupsList();
		for (NameLookups nl : nlList) {
			if(nl.getName().equals("Size UOM")) {
				gs1object.setSizeUOM(nl.getNameValueList().get(0).getValue());
			}
			if (nl.getName().equals("Manufacturer Brand")) {
				gs1object.setManufacturerBrand(nl.getNameValueList().get(0).getValue());
			}
		}

		
		List<LongTextItems> ltiList = targetProd.getData().getLanguageByCode("en-CA").getItemTypeGroup().getLongTextItemsList();
		for (int i = 0; i < ltiList.size(); i++) {
			LongTextItems lti = ltiList.get(i);
			//System.out.println("LongTextItems Name=" + lti.getName());
			if (lti.getName().equals("Normalized Product Description")) {
				gs1object.setNormalizedProdDescLTI(lti.getText());
			} 
			
			if (lti.getName().equals("Ingredients/Key Active Ingredients")) {
				gs1object.setIngredients(lti.getText());
			} 
			
			if (lti.getName().equals("Brand Name")) {
				gs1object.setBrandName(lti.getText());
			} 
		}
		

		List<NameTextItems> ntiList = targetProd.getData().getLanguageByCode("en-CA").getItemTypeGroup()
				.getNameTextItemsList();
		for (NameTextItems nti : ntiList) {
			List<NameText> ntList = nti.getNameTextList();
			switch (nti.getName()) {
				case "Functional Description":
					for (NameText nt : ntList) {
						if (nt.getName().equals("Functional Name")) {
							gs1object.setFunctionalName(nt.getText());
						}
	
						if (nt.getName().equals("Variant")) {
							gs1object.setVariant(nt.getText());
						}
					}
					break;
					
				case "Net Content":
					for (NameText nt : ntList) {
						if (nt.getName().equals("Net Content")) {
							gs1object.setNetContent(nt.getText());
							break;
						}
					}
					break;
					
				case "Size":
					for (NameText nt : ntList) {
						if (nt.getName().equals("Size")) {
							gs1object.setSize(nt.getText());
							break;
						}
					}
					break;
			}	
		}		
		
		List<NameTextLookups> ntlList = targetProd.getData().getLanguageByCode("en-CA").getItemTypeGroup().getNameTextLookupsList();
		for (NameTextLookups ntl : ntlList) {
			if (ntl.getName().equals("Package Measurement")) {
				List<NameValueText> nvtList = ntl.getNameValueTextList();
				for (NameValueText nvt : nvtList ) {
					switch (nvt.getName()) {
						case "Height":
							gs1object.setHeight(nvt.getText());
							gs1object.setHeightUOM(nvt.getValue());
							break;
						case "Width":
							gs1object.setWidth(nvt.getText());
							gs1object.setWidthUOM(nvt.getValue());
							break;
						case "Depth":
							gs1object.setDepth(nvt.getText());
							gs1object.setDepthUOM(nvt.getValue());
							break;
						case "Gross Weight":
							gs1object.setGrossWeigth(nvt.getText());
							gs1object.setGrossWeigthUOM(nvt.getValue());
							break;
					}
				}
			}
		}
		System.out.print(gs1object.toString());
		System.out.println("----------------------------------------------------");
		System.exit(0);	}
}