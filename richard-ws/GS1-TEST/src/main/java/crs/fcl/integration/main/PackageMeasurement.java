package crs.fcl.integration.main;

public class PackageMeasurement {
	public String height;
	public String heightUOM;
	public String width;
	public String widthUOM;
	public String depth;
	public String depthUOM;
	public String grossWeight;
	public String grossWeightUOM;

	public PackageMeasurement() {
		this.height = "N/A";
		this.heightUOM = "N/A";
		this.width = "N/A";
		this.widthUOM = "N/A";
		this.depth = "N/A";
		this.depthUOM = "N/A";
		this.grossWeight = "N/A";
		this.grossWeightUOM = "N/A";
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

}
