package crs.fcl.integration.model;

public class Image {
	String shotTypeId;
	String shotType;
	String mimeType;
	String specDimUnit;
	int specDimWidth;
	int specDimHeight;
	int specQuality;
	int specResolution;
	boolean specIsCropped;
	int specCropPadding;
	int specMaxSizeInByte;
	String url;
	String thumbprint;
	public String getShotTypeId() {
		return shotTypeId;
	}
	public void setShotTypeId(String shotTypeId) {
		this.shotTypeId = shotTypeId;
	}
	public String getShotType() {
		return shotType;
	}
	public void setShotType(String shotType) {
		this.shotType = shotType;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getSpecDimUnit() {
		return specDimUnit;
	}
	public void setSpecDimUnit(String specDimUnit) {
		this.specDimUnit = specDimUnit;
	}
	public int getSpecDimWidth() {
		return specDimWidth;
	}
	public void setSpecDimWidth(int specDimWidth) {
		this.specDimWidth = specDimWidth;
	}
	public int getSpecDimHeight() {
		return specDimHeight;
	}
	public void setSpecDimHeight(int specDimHeight) {
		this.specDimHeight = specDimHeight;
	}
	public int getSpecQuality() {
		return specQuality;
	}
	public void setSpecQuality(int specQuality) {
		this.specQuality = specQuality;
	}
	public int getSpecResolution() {
		return specResolution;
	}
	public void setSpecResolution(int specResolution) {
		this.specResolution = specResolution;
	}
	public boolean getSpecIsCropped() {
		return specIsCropped;
	}
	public void setSpecIsCropped(boolean specIsCropped) {
		this.specIsCropped = specIsCropped;
	}
	public int getSpecCropPadding() {
		return specCropPadding;
	}
	public void setSpecCropPadding(int specCropPadding) {
		this.specCropPadding = specCropPadding;
	}
	public int getSpecMaxSizeInByte() {
		return specMaxSizeInByte;
	}
	public void setSpecMaxSizeInByte(int specMaxSizeInByte) {
		this.specMaxSizeInByte = specMaxSizeInByte;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getThumbprint() {
		return thumbprint;
	}
	public void setThumbprint(String thumbprint) {
		this.thumbprint = thumbprint;
	}
	
}
