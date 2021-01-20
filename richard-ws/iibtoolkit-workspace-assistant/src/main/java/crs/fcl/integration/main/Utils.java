package crs.fcl.integration.main;

import java.io.InputStream;

public class Utils {
	private Utils() { // to prevent public instantiation
	}
	
	public static InputStream getConfResource(String propertyFile) {
		return Utils.class.getClassLoader().getResourceAsStream("conf/" + propertyFile);
	}

}
