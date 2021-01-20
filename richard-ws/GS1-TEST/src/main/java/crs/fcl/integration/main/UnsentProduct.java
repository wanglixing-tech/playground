package crs.fcl.integration.main;

import java.util.Formatter;

public class UnsentProduct {
	/**
	 * RECORD INFO
	 */
	String brandOwnerName;
	String gtin;
	String brandbankPvid;
	String subscriberGln;
	String subscriptionCode;
	/**
	 * GENERAL
	 */

	String normalizedProdDescLTI;
	String manufacturerBrand;
	String brandName;
	String subBrandName;
	String functionalName;
	String variant;
	/**
	 * CONTENTS
	 */
	String netContent;
	String size;
	String sizeUOM;
	/**
	 * MEASURED PACK DATA
	 */
	String height;
	String heightUOM;
	String width;
	String widthUOM;
	String depth;
	String depthUOM;
	String grossWeight;
	String grossWeightUOM;
	/**
	 * NUTRITION
	 */
	String TextualNutritionNutrientSodium;
	String ingredients;
	String servingSizeDescAsIs;

	public String getBrandOwnerName() {
		return brandOwnerName;
	}

	public void setBrandOwnerName(String brandOwnerName) {
		this.brandOwnerName = brandOwnerName;
	}

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public String getNormalizedProdDesc() {
		return normalizedProdDesc;
	}

	public void setNormalizedProdDesc(String normalizedProdDesc) {
		this.normalizedProdDesc = normalizedProdDesc;
	}

	public String getManufacturerBrand() {
		return manufacturerBrand;
	}

	public void setManufacturerBrand(String manufacturerBrand) {
		this.manufacturerBrand = manufacturerBrand;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getSubBrandName() {
		return subBrandName;
	}

	public void setSubBrandName(String subBrandName) {
		this.subBrandName = subBrandName;
	}

	public String getFunctionalName() {
		return functionalName;
	}

	public void setFunctionalName(String functionalName) {
		this.functionalName = functionalName;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getNetContent() {
		return netContent;
	}

	public void setNetContent(String netContent) {
		this.netContent = netContent;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSizeUOM() {
		return sizeUOM;
	}

	public void setSizeUOM(String sizeUOM) {
		this.sizeUOM = sizeUOM;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getHeightUOM() {
		return heightUOM;
	}

	public void setHeightUOM(String heightUOM) {
		this.heightUOM = heightUOM;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getWidthUOM() {
		return widthUOM;
	}

	public void setWidthUOM(String widthUOM) {
		this.widthUOM = widthUOM;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getDepthUOM() {
		return depthUOM;
	}

	public void setDepthUOM(String depthUOM) {
		this.depthUOM = depthUOM;
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public String getServingSizeDescAsIs() {
		return servingSizeDescAsIs;
	}

	public void setServingSizeDescAsIs(String servingSizeDescAsIs) {
		this.servingSizeDescAsIs = servingSizeDescAsIs;
	}

	public String getNormalizedProdDescLTI() {
		return normalizedProdDescLTI;
	}

	public void setNormalizedProdDescLTI(String normalizedProdDescLTI) {
		this.normalizedProdDescLTI = normalizedProdDescLTI;
	}

	public String getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(String grossWeight) {
		this.grossWeight = grossWeight;
	}

	public String getGrossWeightUOM() {
		return grossWeightUOM;
	}

	public void setGrossWeightUOM(String grossWeightUOM) {
		this.grossWeightUOM = grossWeightUOM;
	}

	String normalizedProdDesc;

	public String getBrandbankPvid() {
		return brandbankPvid;
	}

	public void setBrandbankPvid(String brandbankPvid) {
		this.brandbankPvid = brandbankPvid;
	}

	public String getSubscriberGln() {
		return subscriberGln;
	}

	public void setSubscriberGln(String subscriberGln) {
		this.subscriberGln = subscriberGln;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	
	public String getTextualNutritionNutrientSodium() {
		return TextualNutritionNutrientSodium;
	}

	public void setTextualNutritionNutrientSodium(String textualNutritionNutrientSodium) {
		TextualNutritionNutrientSodium = textualNutritionNutrientSodium;
	}

	public static String titleRecord() {
		StringBuilder sbuf = new StringBuilder();
		@SuppressWarnings("resource")
		Formatter fmt = new Formatter(sbuf);
		fmt.format("Brand Owner Name%s", ";");
		fmt.format("GTIN%s", ";");
		fmt.format("BRANDBANK-PVID%s", ";");
		fmt.format("SUBSCRIBER-GLN%s", ";");
		fmt.format("SUBSCRIPTION-CODE%s", ";");
		fmt.format("Normallized Product Description%s", ";");
		fmt.format("Manufacturer Brand%s", ";");
		fmt.format("Brand Name%s", ";");
		fmt.format("Functional Name%s", ";");
		fmt.format("Variant%s", ";");
		fmt.format("Net Content%s", ";");
		fmt.format("Size%s", ";");
		fmt.format("Size UOM%s", ";");
		fmt.format("Height%s", ";");
		fmt.format("Height UOM%s", ";");
		fmt.format("Width%s", ";");
		fmt.format("Width UOM%s", ";");
		fmt.format("Depth%s", ";");
		fmt.format("Depth UOM%s", ";");
		fmt.format("Gross Weigth%s", ";");
		fmt.format("Gross Weigth UOM%s", ";");
		fmt.format("Sodium%s", ";");
		fmt.format("Ingredients/Key Active Ingredients%s", "\n");
		return sbuf.toString();
	}

	public String toFalttenRecord() {
		StringBuilder sbuf = new StringBuilder();
		@SuppressWarnings("resource")
		Formatter fmt = new Formatter(sbuf);
		fmt.format("%s;", this.brandOwnerName);
		fmt.format("%s;", this.gtin);
		fmt.format("%s;", this.brandbankPvid);
		fmt.format("%s;", this.subscriberGln);
		fmt.format("%s;", this.subscriptionCode);
		fmt.format("%s;", this.normalizedProdDesc);
		fmt.format("%s;", this.manufacturerBrand);
		fmt.format("%s;", this.brandName);
		fmt.format("%s;", this.functionalName);
		fmt.format("%s;", this.variant);
		fmt.format("%s;", this.netContent);
		fmt.format("%s;", this.size);
		fmt.format("%s;", this.sizeUOM);
		fmt.format("%s;", this.height);
		fmt.format("%s;", this.heightUOM);
		fmt.format("%s;", this.width);
		fmt.format("%s;", this.widthUOM);
		fmt.format("%s;", this.depth);
		fmt.format("%s;", this.depthUOM);
		fmt.format("%s;", this.grossWeight);
		fmt.format("%s;", this.grossWeightUOM);
		fmt.format("%s;", this.TextualNutritionNutrientSodium);		
		fmt.format("%s\n", this.ingredients);
		return sbuf.toString();
	}

	public String toString() {
		StringBuilder sbuf = new StringBuilder();
		@SuppressWarnings("resource")
		Formatter fmt = new Formatter(sbuf);
		fmt.format("Brand Owner Name = %s%n", this.brandOwnerName);
		fmt.format("GTIN = %s%n", this.gtin);
		fmt.format("BRANDBANK-PVID = %s%n", this.brandbankPvid);
		fmt.format("SUBSCRIBER-GLN = %s%n", this.subscriberGln);
		fmt.format("SUBSCRIPTION-CODE = %s%n", this.subscriptionCode);
		fmt.format("Normallized Product Description = %s%n", this.normalizedProdDesc);
		fmt.format("Manufacturer Brand = %s%n", this.manufacturerBrand);
		fmt.format("Brand Name = %s%n", this.brandName);
		fmt.format("Functional Name = %s%n", this.functionalName);
		fmt.format("Variant = %s%n", this.variant);
		fmt.format("Net Content = %s%n", this.netContent);
		fmt.format("Size = %s%n", this.size);
		fmt.format("Size UOM = %s%n", this.sizeUOM);
		fmt.format("Height = %s%n", this.height);
		fmt.format("Height UOM = %s%n", this.heightUOM);
		fmt.format("Width = %s%n", this.width);
		fmt.format("Width UOM = %s%n", this.widthUOM);
		fmt.format("Depth = %s%n", this.depth);
		fmt.format("Depth UOM= %s%n", this.depthUOM);
		fmt.format("Gross Weigth = %s%n", this.grossWeight);
		fmt.format("Gross Weigth UOM = %s%n", this.grossWeightUOM);
		fmt.format("Sodium = %s%n", this.TextualNutritionNutrientSodium);
		fmt.format("Ingredients/Key Active Ingredients = %s%n", this.ingredients);
		return sbuf.toString();
	}
}
