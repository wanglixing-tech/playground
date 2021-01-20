package crs.fcl.integration.iib;

@GrabResolver(name='snakeyaml', root='https://mvnrepository.com/artifact/org.yaml/snakeyaml')
@Grab(group='org.yaml', module='snakeyaml', version='1.24')

import org.yaml.snakeyaml.*
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

try {
	def CONFIGFILE = "mi9-2.1-richard-testing.yml"
	//def TARGET_DIR = "/var/lib/jenkins/repositories/iib/" + CONFIGFILE
	def TARGET_DIR = "C:\\IIB\\iib\\" + CONFIGFILE
	InputStream input = new FileInputStream(new File(TARGET_DIR ));
	Yaml yaml = new Yaml();
	def inodeList = [];
	Map<String, Object> object = (Map<String, Object>) yaml.load(input);
	envObject = object['aceEnvironments']
	devEnv = envObject['DEV']
	envInodeList = devEnv['integrationNodes']
	envInodeList.each { oneInode ->
		inodeList.add(oneInode['nodeName'])
	}
	//return appList
	println inodeList
} catch (Exception e) {
	println(e)
}
