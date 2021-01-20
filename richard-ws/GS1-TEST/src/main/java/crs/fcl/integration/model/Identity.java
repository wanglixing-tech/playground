package crs.fcl.integration.model;

public class Identity {
	String targetMarketCode;
	String prodGtin;
	String prodPvid;
	String subscriptionId;
	String subscriptionCode;
	String subscription;
	String DiagDescCode;
	String DiagDesc;

	public String getTargetMarketCode() {
		return targetMarketCode;
	}
	public void setTargetMarketCode(String targetMarketCode) {
		this.targetMarketCode = targetMarketCode;
	}
	public String getProdGtin() {
		return prodGtin;
	}
	public void setProdGtin(String prodGtin) {
		this.prodGtin = prodGtin;
	}
	public String getProdPvid() {
		return prodPvid;
	}
	public void setProdPvid(String prodPvid) {
		this.prodPvid = prodPvid;
	}
	public String getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getSubscriptionCode() {
		return subscriptionCode;
	}
	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}
	public String getSubscription() {
		return subscription;
	}
	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
	public String getDiagDescCode() {
		return DiagDescCode;
	}
	public void setDiagDescCode(String diagDescCode) {
		DiagDescCode = diagDescCode;
	}
	public String getDiagDesc() {
		return DiagDesc;
	}
	public void setDiagDesc(String diagDesc) {
		DiagDesc = diagDesc;
	}
	public String getDefaultLanguage() {
		return defaultLanguage;
	}
	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	String defaultLanguage;
}
