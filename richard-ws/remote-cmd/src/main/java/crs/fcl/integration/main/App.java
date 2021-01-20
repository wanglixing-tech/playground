package crs.fcl.integration.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class App {
	
	static final Logger logger = Logger.getLogger(App.class);
	
	public static void main(String[] args) throws IOException {
		
		Properties config = App.getConfig(args);
		
		final PublicKeySshSession session = new PublicKeySshSession.Builder(
						config.getProperty("host"), 
						config.getProperty("username"), 
						Integer.parseInt(config.getProperty("port")), 
						config.getProperty("privateKeyPath")).logger(new JschLogger()).build();
		
		if (session == null) {
			System.exit(-1);
		}
		
		session.execute("ls -ltr");
		
	}
	
	public static Properties getConfig(String[] args) {
		String profile = "local";
		
		if (args != null && args.length > 0) {
			profile = args[0];
		}
        
		Properties properties = new Properties();
        
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("config/" + profile + "/ssh.config");
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load properties.", e);
		}
        
		return properties;
	}
}