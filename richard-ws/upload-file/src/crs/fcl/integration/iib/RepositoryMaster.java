package crs.fcl.integration.iib;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import de.slackspace.openkeepass.domain.KeePassFile;

public class RepositoryMaster{ 
	private final static String configRepoDBFile = null;
	private final static String keepassDBFile = null;
	private final static String keyFile = null;	
	private static String outPutFile = "config.xml";
	private static String envName = null;
	private static String egName = null;
	private static String domainName = null;
	private static String appName = null;
	private static KeePassFile database = null;
	private static Document configRepo = null;	
	
	final static Logger logger = Logger.getLogger(LookupNReplaceV2.class);
   public static void main(String args[]){ 
   } 
}