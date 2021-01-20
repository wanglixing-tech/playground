package crs.fcl.integration.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;

public class IIBEnvFileParser {
	private static final String className = IIBEnvFileParser.class.getName();
	private static final java.util.logging.Logger logger = dro.util.Logger.getLogger(className);
	@SuppressWarnings("unchecked")
	public IIBEnvironments getEnvConfig(String yamlConfigFile, String envName) throws IOException {
		IIBEnvironments aceEnv = null;
		InputStream input = new FileInputStream(yamlConfigFile);
		Yaml yaml = new Yaml();
		Iterable<Object> itrEnv = yaml.loadAll(input);
		for (Object envInfo : itrEnv) {
			if (envInfo instanceof java.util.LinkedHashMap) {
				Object obj = ((LinkedHashMap<?, ?>) envInfo).get("environments");
				if (obj instanceof java.util.LinkedHashMap) {
					Object allINodes = ((LinkedHashMap<?, ?>) obj).get(envName);
					aceEnv = new IIBEnvironments();
					aceEnv.setEnvironmentName(envName);
					ArrayList<Object> iNodeObjectList = ((java.util.ArrayList<Object>) allINodes);
					ArrayList<IIBBroker> inList = new ArrayList<IIBBroker>();
					for (Object iNode : iNodeObjectList) {
						IIBBroker iNodeInst = new IIBBroker();
						
						/*
						 * Parse Node-Level Resource list
						 */
						iNodeInst.setiNodeName((String) ((LinkedHashMap<?, ?>) iNode).get("nodeName"));
						iNodeInst.setHostName((String) ((LinkedHashMap<?, ?>) iNode).get("hostName"));
						Integer portInteger = (Integer) ((LinkedHashMap<?, ?>) iNode).get("port");
						iNodeInst.setPort(portInteger.intValue());
						iNodeInst.setProxyUser((String) ((LinkedHashMap<?, ?>) iNode).get("proxyUser"));
						iNodeInst.setSshKeyName((String) ((LinkedHashMap<?, ?>) iNode).get("sshKeyName"));
						iNodeInst.setIibVersion((String) ((LinkedHashMap<?, ?>) iNode).get("iibVersion"));
						iNodeInst.setIibInstallPath((String) ((LinkedHashMap<?, ?>) iNode).get("iibInstallPath"));
						iNodeInst.setQueueManagerName((String) ((LinkedHashMap<?, ?>) iNode).get("queueManagerName"));						
						
						inList.add(iNodeInst);
					}
					aceEnv.setiNodeList(inList);
				}
			}
		}
		input.close();
		return aceEnv;
	}
	
	public static void main(String[] args) throws IOException {
		IIBEnvFileParser envConf = new IIBEnvFileParser();
		IIBEnvironments devEnv = envConf.getEnvConfig("C:\\repositories\\esb\\richard.wang\\ws-dream-app\\collect-IIB-info\\iib-env.yml", "DEV");
		System.out.println("ENV:" + devEnv.getEnvironmentName());
		ArrayList<IIBBroker> iNodeList = devEnv.getiNodeList();
		for (IIBBroker in : iNodeList) {
			System.out.println("Integration Node Name:" + in.getiNodeName());
			System.out.println("HOST:" + in.getHostName());
			System.out.println("port:" + in.getPort());
			System.out.println("USER:" + in.getProxyUser());
			System.out.println("sshKeyName:" + in.getSshKeyName());
			System.out.println("iibVersion:" + in.getIibVersion());
			System.out.println("iibInstallPath:" + in.getIibInstallPath());
		}
	}
	

}