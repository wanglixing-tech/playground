package crs.fcl.integration.iib
@GrabResolver(name='snakeyaml', root='https://mvnrepository.com/artifact/org.yaml/snakeyaml')
@Grab(group='org.yaml', module='snakeyaml', version='1.24')

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.*
import org.yaml.snakeyaml.constructor.*
import groovy.transform.*

//PromotionMovement_Lib
//InventoryUpdate_Lib
//PriyaConfirmation_Lib
def AppNames = "CA_PromotionMovement_Pub,CAL_Mainframe_InventoryUpdate_Pub,STN_Mainframe_InventoryUpdate_Pub,Sub_ShipmentClosure_OMS"
def TARGET_DIR = "C:\\Users\\Richard.Wang\\fcl.crs\\repositories\\iib\\oms-3.0.yml"
def sharedLibList = []
try {
	InputStream input = new FileInputStream(new File(TARGET_DIR ))
	Yaml yaml = new Yaml()
	Map<String, Object> object = (Map<String, Object>) yaml.load(input)
	sharedLibMapList = object['sharedLibs']
	sharedLibMapList.each { oneSharedLib ->
		referredByAppList = oneSharedLib["referredBy"]
		referredByAppList.each { appName ->
			if (AppNames.startsWith(appName) || AppNames.contains("," + appName)) {
				sharedLibList.add(oneSharedLib["sharedLibName"] + "@" + appName)
			}
		}
	}
	println sharedLibList
} catch (Exception e) {
	println(e)
}
