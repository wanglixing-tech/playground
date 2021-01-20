package crs.fcl.integration.iib;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
//import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.log4j.Logger;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.RuntimeServices;
//import org.apache.log4j.BasicConfigurator;

public class MyVelocityExample {

	/** Property key for specifying the name for the log instance */
	public static final String LOGCHUTE_COMMONS_LOG_NAME = "runtime.log.logsystem.commons.logging.name";

	/** Default name for the commons-logging instance */
	public static final String DEFAULT_LOG_NAME = "org.apache.velocity";

	/** the commons-logging Log instance */
	protected static Log log;

	public static void main(String args[]) throws Exception {

		Map<String, String> userDataMap = new HashMap<String, String>();
		userDataMap.put("1001", "Sarathy");
		userDataMap.put("1002", "Kumar");
		userDataMap.put("1003", "Raja");
		userDataMap.put("1004", "Gopal");
		try {
			VelocityEngine engine = new VelocityEngine();
			/*
			 * register this class as a logger with the Velocity singleton (NOTE: this would
			 * not work for the non-singleton method.)
			 */
			engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			engine.init();

			// Properties p = new Properties();
			// p.setProperty("file.resource.loader.path",
			// "C:\\Users\\richard.wang\\ws-java-velocity\\velocity-test\\src\\main\\resources\\templates");
			// engine.init( p );

			// Template template = engine.getTemplate("templates/userinfo.vm");
			Template template = engine.getTemplate("templates/userinfo.vm");

			VelocityContext vc = new VelocityContext();
			vc.put("userDataMap", userDataMap);

			StringWriter writer = new StringWriter();
			template.merge(vc, writer);

			System.out.println(writer);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
