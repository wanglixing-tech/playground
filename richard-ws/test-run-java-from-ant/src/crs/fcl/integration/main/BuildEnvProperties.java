package crs.fcl.integration.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class BuildEnvProperties {
	private static final String mqsideploy = "/server/bin/mqsideploy.sh";
	private static final String mqsideployscript = "/server/bin/mqsideployscript.sh";
	private static final String mqsichangeflowmonitoring = "/server/bin/mqsichangeflowmonitoring.sh";
	private static final String mqsiapplybaroverride = "/server/bin/mqsiapplybaroverride.sh";
	public static void main(String args[]) throws IOException{   
		String envPrefix = System.getProperty("env");
		String domainConfigFile = System.getProperty("yamlFile");
		String propFileName = "ant-iib-environment-" + envPrefix.toLowerCase() + ".properties";
        OutputStream dynamicPropFile = new FileOutputStream(propFileName);
        Properties prop = new Properties();
		YamlConfig envConf = new YamlConfig();
		AceEnvironment iibEnv = envConf.getAceEnvConfig(domainConfigFile, envPrefix.toUpperCase());
		
		System.out.println("ENV:" + iibEnv.getEnvironmentName());
		System.out.println("HOST:" + iibEnv.getHostName());
		System.out.println("USER:" + iibEnv.getProxyUser());
		System.out.println("sshKeyName:" + iibEnv.getSshKeyName());
		System.out.println("iibVersion:" + iibEnv.getIibVersion());
		System.out.println("iibInstallPath:" + iibEnv.getIibInstallPath());
		ArrayList<IntegrationNode> inList = iibEnv.getiNodeList();
		
		for (IntegrationNode in : inList) {
			System.out.println("Integration Node Name:" + in.getNodeName());
			ArrayList<IntegrationServer> isList = in.getiServerList();
			if ((inList.size() == 1) && isList.size() == 1) {
				/*
				 * Single node with single server
				 */
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".hostname", iibEnv.getHostName());
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".user", iibEnv.getProxyUser());
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".id_rsa", iibEnv.getSshKeyName());
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".ibname", in.getNodeName());
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".isname", isList.get(0).getIntegrationServer());
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".version", iibEnv.getIibVersion());
		        String iibInstallPath = iibEnv.getIibInstallPath();
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".install.path", iibInstallPath);
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".mqsideploy", iibInstallPath + mqsideploy);
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".mqsideployscript", iibInstallPath + mqsideployscript);
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".mqsiapplybaroverride", iibInstallPath + mqsiapplybaroverride);
		        prop.setProperty("iib.evn." + envPrefix.toLowerCase() + ".mqsichangeflowmonitoring", iibInstallPath + mqsichangeflowmonitoring);
		        
		        break;
			} else {				
				for (int i=0; i < isList.size(); i++) {
					String envPrefixPlus = envPrefix + ( i == 0 ? "" : String.valueOf(i));
					IntegrationServer is = isList.get(i);
					System.out.println("\tIntegration Server Name:" + is.getIntegrationServer());
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".hostname", iibEnv.getHostName());
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".user", iibEnv.getProxyUser());
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".id_rsa", iibEnv.getSshKeyName());
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".ibname", in.getNodeName());
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".isname", is.getIntegrationServer());
			        String iibInstallPath = iibEnv.getIibInstallPath();
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".install.path", iibInstallPath);
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".mqsideploy", iibInstallPath + mqsideploy);
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".mqsideployscript", iibInstallPath + mqsideployscript);
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".mqsiapplybaroverride", iibInstallPath + mqsiapplybaroverride);
			        prop.setProperty("iib.evn." + envPrefixPlus.toLowerCase() + ".mqsichangeflowmonitoring", iibInstallPath + mqsichangeflowmonitoring);
				}
			}
		}
		
        Properties iibEnvProp = new Properties() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        iibEnvProp.putAll(prop);       
        
        iibEnvProp.store(dynamicPropFile, "Dynamic Property File");
  	         
		
    }
}
