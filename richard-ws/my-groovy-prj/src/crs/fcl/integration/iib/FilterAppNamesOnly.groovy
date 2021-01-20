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
def AppNames = "CA_PromotionMovement_Pub........X,CAL_Mainframe_InventoryUpdate_Pub.........Y,STN_Mainframe_InventoryUpdate_Pub........Z,Sub_ShipmentClosure_OMS........A"
def AppNamesWithCommas = "";
try {
	def allAppList = AppNames.split(',')
	def allAppNameOnlyList = []
	allAppList.each { 
		allAppNameOnlyList.add(it.substring(0, it.indexOf(".")))
	}
	
	AppNamesWithCommas = allAppNameOnlyList.join(',')
	println AppNamesWithCommas
} catch (Exception e) {
	println(e)
}
