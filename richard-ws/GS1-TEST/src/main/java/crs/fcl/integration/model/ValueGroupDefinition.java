package crs.fcl.integration.model;

public class ValueGroupDefinition {
	String id;
	String name;
	String amountHeader;
	String referenceIntakeHeader;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAmountHeader() {
		return amountHeader;
	}
	public void setAmountHeader(String amountHeader) {
		this.amountHeader = amountHeader;
	}
	public String getReferenceIntakeHeader() {
		return referenceIntakeHeader;
	}
	public void setReferenceIntakeHeader(String referenceIntakeHeader) {
		this.referenceIntakeHeader = referenceIntakeHeader;
	}
}
