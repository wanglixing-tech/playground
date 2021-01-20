package crs.fcl.integration.iib
@GrabResolver(name='snakeyaml', root='https://mvnrepository.com/artifact/org.yaml/snakeyaml')
@Grab(group='org.yaml', module='snakeyaml', version='1.24')

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.*
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

try {
	def TARGET_DIR = "/var/lib/jenkins/repositories/iib/" + DomainConfigFile
	InputStream input = new FileInputStream(new File(TARGET_DIR ));
	Yaml yaml = new Yaml();
	def appList = [];
	Map<String, Object> object = (Map<String, Object>) yaml.load(input);
	appMapList = object['applications']
	appMapList.each { oneApp ->
	  if (oneApp["appType"].equals("SHARED_LIB") ) {
		appList.add(oneApp['appName'])
	  }
}
	return appList
} catch (Exception e) {
	println(e)
}

println(a)