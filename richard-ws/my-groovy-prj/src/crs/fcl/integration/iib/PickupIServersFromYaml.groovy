package crs.fcl.integration.iib;

@GrabResolver(name='snakeyaml', root='https://mvnrepository.com/artifact/org.yaml/snakeyaml')
@Grab(group='org.yaml', module='snakeyaml', version='1.24')

import org.yaml.snakeyaml.*
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

try {
	def IntegrationNode = "TFIB01"
	def CONFIGFILE = "master-oms.yml"
	//def TARGET_DIR = "/var/lib/jenkins/repositories/iib/" + CONFIGFILE
	def TARGET_DIR = "C:\\IIB\\iib\\" + CONFIGFILE
	InputStream input = new FileInputStream(new File(TARGET_DIR ));
	Yaml yaml = new Yaml();
	def iserverList = [];
	Map<String, Object> object = (Map<String, Object>) yaml.load(input);
	envObject = object['aceEnvironments']
	devEnv = envObject['TEST']
	envInodeList = devEnv['integrationNodes']
	envInodeList.each { oneInode ->
		if (oneInode["nodeName"].equals(IntegrationNode) ) {
			inodeServers = oneInode['integrationServers']
			inodeServers.each { oneIserver ->
				iserverList.add(oneIserver['serverName'])
			}
		}
	}
	//return appList
	println iserverList
} catch (Exception e) {
	println(e)
}
