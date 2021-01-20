package crs.fcl.integration.iib
@GrabResolver(name='snakeyaml', root='https://mvnrepository.com/artifact/org.yaml/snakeyaml')
@Grab(group='org.yaml', module='snakeyaml', version='1.24')

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

try {
	//def TARGET_DIR = "/var/lib/jenkins/repositories/iib/oms-3.0.yml"
	def TARGET_DIR = "C:\\Users\\Richard.Wang\\fcl.crs\\repositories\\iib\\oms-3.0.yml"
    InputStream input = new FileInputStream(new File(TARGET_DIR ));
    Yaml yaml = new Yaml();
    def fixedLen = 60;
    def appList = [];
    def iServerList = [];
    Map<String, Object> object = (Map<String, Object>) yaml.load(input);
    envObject = object['aceEnvironments']
    targetEnv = envObject["UAT_HA_11"]
    envInodeList = targetEnv ['integrationNodes']
    envInodeList.each { oneInode ->
        if (oneInode["nodeName"].equals("UFIB01") ) {
            inodeServers = oneInode['integrationServers']
            inodeServers.each { 
				oneIServer = it['serverName'].toString()
				iServerList.add(oneIServer)
            }
        }
    }

    appMapList = object['applications']
    appMapList.each { oneApp ->
        if (oneApp["appType"].equals("APP") ){
            iServers = oneApp["integrationServer"].toString()
			if (! iServers.contains("*")) {
	            maybeIServers = iServers.split(',')
	            isListAfterParsing = []
	            maybeIServers.each { 
	                if (iServerList.contains(it)) {
	                    isListAfterParsing.add(it)
	                } 
	            }	
				if (! isListAfterParsing.empty)	
					appList.add(oneApp['appName'].padRight(fixedLen, '.')  + "[" + isListAfterParsing.join(",") + "]")
	        }
        }
    }
	println appList
   //return appList
} catch (Exception e) {
    println(e)
}