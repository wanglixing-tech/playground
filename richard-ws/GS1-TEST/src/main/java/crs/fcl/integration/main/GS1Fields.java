package crs.fcl.integration.main;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;

import crs.fcl.integration.model.Assets;
import crs.fcl.integration.model.CategoryLevel;
import crs.fcl.integration.model.Data;
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

public class GS1Fields {

	public static void main(String[] args) throws DocumentException {
		final String unsentProductsData = "C:\\Users\\Richard.Wang\\Documents\\MyDailyWork\\GS1\\Data\\response-getUnsentProdData2_ISO.xml";
		ParseProductData ppd = new ParseProductData(unsentProductsData);
		List<String> gtinWithPvidList = ppd.findAllGTINWithPVID();
		showGTINWithPvid(gtinWithPvidList);
		String gtin = "0068100047578";
		String pvid = "5894189";
		Product targetProd = ppd.findTargetProduct(gtin, pvid);
		showTargetProd(targetProd);
	}
	
	public static void showGTINWithPvid(List<String> gtinWithPvidList) {
		for (String gtinPLusPvid : gtinWithPvidList ) {
			String[] gtinPvid = gtinPLusPvid.split(":");
			System.out.println(gtinPvid[0] + "-" + gtinPvid[1]);
		}
	}

	public static void showTargetProd(Product targetProd) {

		/**
		 * Get Brand Owner Name
		 */
		System.out.println("Subscription Id:" + targetProd.getIdentity().getSubscriptionId());
		System.out.println("Subscription Code:" + targetProd.getIdentity().getSubscriptionCode());
		System.out.println("Subscription:" + targetProd.getIdentity().getSubscription());

		/**
		 * Get GTIN and PVID
		 */
		System.out.println("GTIN:" + targetProd.getIdentity().getProdGtin());
		System.out.println("PVID:" + targetProd.getIdentity().getProdPvid());

		/**
		 * Normalized Product Description
		 */
		/**
		 * Get Assets displayed
		 */
		Assets assets = targetProd.getAssets();
		List<Image> imageList = assets.getImageList();
		for (int i = 0; i < imageList.size(); i++) {
			System.out.println("-----------------------------Image No:" + i + "--------------------------------");
			Image image = imageList.get(i);
			System.out.println(image.getMimeType());
			System.out.println(image.getShotType());
			System.out.println(image.getShotTypeId());
			System.out.println(image.getSpecCropPadding());
			System.out.println(image.getSpecDimUnit());
			System.out.println(image.getSpecDimHeight());
			System.out.println(image.getSpecDimWidth());
			System.out.println(image.getSpecIsCropped());
			System.out.println(image.getSpecMaxSizeInByte());
			System.out.println(image.getSpecQuality());
			System.out.println(image.getSpecResolution());
			System.out.println(image.getThumbprint());
			System.out.println(image.getUrl());
		}


		/**
		 * Get DATA INFO displayed
		 */
		Data data = targetProd.getData();
		/**
		 * Per Languages
		 */
		List<Language> languageList = data.getLanguageList();
		for (Language l : languageList) {
			if (l.getCode().equals("en-CA")) {
				System.out.println("Language Code: " + l.getCode());
				System.out.println("Language Description: " + l.getDescription());

				ItemTypeGroup itg = l.getItemTypeGroup();
				// Display Long Text Items
				List<LongTextItems> ltiList = itg.getLongTextItemsList();
				System.out.println("-------------------Long Text Items----------------------------------------");
				for (LongTextItems lti : ltiList) {
					System.out.println("Long Text Items Id: " + lti.getId());
					System.out.println("Long Text Items Name: " + lti.getName());
					System.out.println("Long Text Items: " + lti.getTextContent());
				}

				// Display Name Text Items
				List<NameTextItems> ntiList = itg.getNameTextItemsList();
				System.out.println("-------------------Name Text Items----------------------------------------");
				for (NameTextItems nti : ntiList) {
					System.out.println("Name Text Items Id: " + nti.getId());
					System.out.println("Name Text Items Name: " + nti.getName());
					List<NameText> ntList = nti.getNameTextList();
					for (NameText nt : ntList) {
						System.out.println("Name Text Name Id: " + nt.getNameId());
						System.out.println("Name Text Name: " + nt.getName());
						System.out.println("Name Text Text: " + nt.getTextContent());
					}
				}

				// Display NameLookups
				List<NameLookups> nlList = itg.getNameLookupsList();
				System.out.println("-------------------Name Lookups----------------------------------------");
				for (NameLookups nl : nlList) {
					System.out.println("Name lookups Id: " + nl.getId());
					System.out.println("Name lookups Name: " + nl.getName());
					List<NameValue> nvList = nl.getNameValueList();
					for (NameValue nv : nvList) {
						System.out.println("Name Value Name Id: " + nv.getNameId());
						System.out.println("Name Value Name: " + nv.getName());
						System.out.println("Name Value Value Id: " + nv.getValueId());
						System.out.println("Name Value Value: " + nv.getValue());
					}
				}

				// Display Name Text Lookups
				List<NameTextLookups> ntlList = itg.getNameTextLookupsList();
				System.out.println("-------------------Name Text Lookups----------------------------------");
				for (NameTextLookups nl : ntlList) {
					System.out.println("Name Text lookups Id: " + nl.getId());
					System.out.println("Name Text lookups Name: " + nl.getName());
					List<NameValueText> nvtList = nl.getNameValueTextList();
					for (NameValueText nvt : nvtList) {
						System.out.println("Name Value Text Name Id: " + nvt.getNameId());
						System.out.println("Name Value Text Name: " + nvt.getName());
						System.out.println("Name Value Text Value Id: " + nvt.getValueId());
						System.out.println("Name Value Text Value: " + nvt.getValue());
						System.out.println("Name Value Text Text: " + nvt.getText());
					}
				}

				// Display Structured Nutrition
				System.out.println("---------------------Structured Nutrition-----------------------------------");
				List<StructuredNutrition> snList = itg.getStructuredNutritionList();
				for (StructuredNutrition sn : snList) {
					System.out.println("Structured Nutrition Id: " + sn.getId());
					System.out.println("Structured Nutrition Name: " + sn.getName());
					ArrayList<ValueGroupDefinition> vgdList = sn.getValueGroupDefinitionList();
					System.out.println("--------------------------------------------------------------");
					for (ValueGroupDefinition vgd : vgdList) {
						System.out.println("Structured Nutrition ValueGroupDefinition Id: " + vgd.getId());
						System.out.println("Structured Nutrition ValueGroupDefinition Name: " + vgd.getName());
						System.out.println(
								"Structured Nutrition ValueGroupDefinition Amount Header: " + vgd.getAmountHeader());
						System.out.println("Structured Nutrition ValueGroupDefinition Reference Intake Header: "
								+ vgd.getReferenceIntakeHeader());
					}
					System.out.println("--------------------------------------------------------------");
					ArrayList<Nutrient> nuList = sn.getNutrientList();
					System.out.println("--------------------------------------------------------------");
					for (Nutrient nu : nuList) {
						System.out.println("Structured Nutrition Nutrient Id: " + nu.getId());
						System.out.println("Structured Nutrition Nutrient Name: " + nu.getName());
						System.out.println("Structured Nutrition Unit Id: " + nu.getUnitId());
						System.out.println("Structured Nutrition Unit Name: " + nu.getUnitName());
						System.out.println("Structured Nutrition Unit Abbrivation: " + nu.getUnitAbbr());
						System.out.println("Structured Nutrition ValueGroup Id: " + nu.getValueGroupId());
						System.out.println("Structured Nutrition ValueGroup Name: " + nu.getValueGroupName());
						System.out.println("Structured Nutrition ValueGroup Amount: " + nu.getValueGroupAmountValue());
						System.out.println("Structured Nutrition ReferenceIntake Value: "
								+ nu.getValueGroupReferenceIntakeValue());
						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					}
				}

				// Display Textual Nutrition
				System.out.println("---------------------Textual Nutrition-----------------------------------------");
				List<TextualNutrition> tnList = itg.getTextualNutritionList();
				for (TextualNutrition tn : tnList) {
					System.out.println("Textual Nutrition Id: " + tn.getId());
					System.out.println("Textual Nutrition Name: " + tn.getName());
					List<String> headings = tn.getHeadings();
					for (String heading : headings) {
						System.out.println("Textual Nutrition Heading: " + heading);
					}

					List<TextualNutritionNutrient> textualNutritionNutrientList = tn.getTextualNutritionNutrientList();
					for (TextualNutritionNutrient tnn : textualNutritionNutrientList) {
						System.out.println("Textual Nutrition Nutrient Name: " + tnn.getName());
						List<String> valueList = tnn.getValueList();
						for (String value : valueList) {
							System.out.println("Textual Nutrition Nutrient Value: " + value);
						}
					}
				}

				// Display Memos
				List<Memo> mList = itg.getMemoList();
				System.out.println("-----------------------------MEMO--------------------------------");
				for (Memo memo : mList) {
					System.out.println("Memo Id: " + memo.getId());
					System.out.println("Memo Name: " + memo.getName());
					System.out.println("Memo Content: " + memo.getContent());
					System.out.println("--------------------------------------------------------------");
				}
			}
		}
	}
}
