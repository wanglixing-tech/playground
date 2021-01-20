package crs.fcl.integration.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.yaml.snakeyaml.Yaml;

public class YamlConfig {
	@SuppressWarnings("unchecked")
	public AceEnvironment getAceEnvConfig(String yamlConfigFile, String envName) throws IOException {
		AceEnvironment aceEnv = null;
		InputStream input = new FileInputStream(yamlConfigFile);
		Yaml yaml = new Yaml();
		Iterable<Object> itrEnv = yaml.loadAll(input);
		for (Object envInfo : itrEnv) {
			//System.out.println("Loaded object type:" + envInfo.getClass());
			if (envInfo instanceof java.util.LinkedHashMap) {
				Object obj = ((LinkedHashMap<?, ?>) envInfo).get("aceEnvironments");
				//System.out.println("Loaded object type1:" + obj.getClass());
				if (obj instanceof java.util.LinkedHashMap) {
					Object envAttrs = ((LinkedHashMap<?, ?>) obj).get(envName);
					//System.out.println("Loaded object type2:" + envAttrs.getClass());
					aceEnv = new AceEnvironment();
					aceEnv.setEnvironmentName(envName);
					aceEnv.setHostName((String) ((LinkedHashMap<?, ?>) envAttrs).get("hostName"));
					aceEnv.setProxyUser((String) ((LinkedHashMap<?, ?>) envAttrs).get("proxyUser"));
					aceEnv.setSshKeyName((String) ((LinkedHashMap<?, ?>) envAttrs).get("sshKeyName"));
					aceEnv.setIibVersion((String) ((LinkedHashMap<?, ?>) envAttrs).get("iibVersion"));
					aceEnv.setIibInstallPath((String) ((LinkedHashMap<?, ?>) envAttrs).get("iibInstallPath"));
					ArrayList<String> iNodeObjectList = (ArrayList<String>) ((LinkedHashMap<?, ?>) envAttrs).get("integrationNodes");
					ArrayList<IntegrationNode> inList = new ArrayList<IntegrationNode>();
					for (Object iNodeObject : iNodeObjectList) {
						IntegrationNode iNode = new IntegrationNode();
						/*
						 * Parse Node-Level Resource list
						 */
						iNode.setNodeName((String) ((LinkedHashMap<?, ?>) iNodeObject).get("nodeName"));
						ArrayList<String> iNodeResList = (ArrayList<String>) ((LinkedHashMap<?, ?>) iNodeObject).get("requiredResources");
						iNode.setRequiredResourceList(iNodeResList);
						
						Object iServerObject =  ((LinkedHashMap<?, ?>) iNodeObject).get("integrationServers");
						//System.out.println("Loaded iServerObject:" + iServerObject.getClass());
						ArrayList<IntegrationServer> isList = new ArrayList<IntegrationServer>();
						for (Object oneiServer : (ArrayList<?>) iServerObject) {
							IntegrationServer is = new IntegrationServer();
							is.setIntegrationServer((String)((LinkedHashMap<?, ?>) oneiServer).get("serverName"));
							is.setWorkDir((String)((LinkedHashMap<?, ?>) oneiServer).get("workDir"));
							ArrayList<String> isResourceList = (ArrayList<String>) ((LinkedHashMap<?, ?>) oneiServer).get("requiredResources");
							//System.out.println("Loaded object type5:" + isResourceList.getClass());
							is.setResourceList(isResourceList);
							isList.add(is);
						}
						iNode.setiServerList(isList);
						inList.add(iNode);
					}
					aceEnv.setiNodeList(inList);
				}
			}
		}
		input.close();
		return aceEnv;
	}
	
	public static void main(String[] args) throws IOException {
		YamlConfig envConf = new YamlConfig();
		AceEnvironment devEnv = envConf.getAceEnvConfig("C:\\IIB\\iib\\eos-1.3.yml", "DEV");
		System.out.println("ENV:" + devEnv.getEnvironmentName());
		System.out.println("HOST:" + devEnv.getHostName());
		System.out.println("USER:" + devEnv.getProxyUser());
		System.out.println("sshKeyName:" + devEnv.getSshKeyName());
		ArrayList<IntegrationNode> inList = devEnv.getiNodeList();
		for (IntegrationNode in : inList) {
			System.out.println("Integration Node Name:" + in.getNodeName());
			
			ArrayList<String> inResList = in.getRequiredResourceList();
			for (String resourceName : inResList) {
				System.out.println("\tResource Name under Integration Node:" + resourceName);
			}
			
			ArrayList<IntegrationServer> isList = in.getiServerList();
			for (IntegrationServer is : isList) {
				System.out.println("\tIntegration Server Name:" + is.getIntegrationServer());
				ArrayList<String> resList = is.getResourceList();
				for (String resourceName : resList) {
					System.out.println("\t\tResource Name under Resource Name:" + resourceName);
				}
			}
		}
	}
	

}