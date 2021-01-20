package crs.fcl.integration.model;

public class Product {
	Identity 	identity;
	Assets		assets;
	Data		data;
	public Identity getIdentity() {
		return identity;
	}
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	public Assets getAssets() {
		return assets;
	}
	public void setAssets(Assets assets) {
		this.assets = assets;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
}
