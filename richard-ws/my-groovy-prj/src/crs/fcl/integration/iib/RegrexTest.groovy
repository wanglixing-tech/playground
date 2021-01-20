package crs.fcl.integration.iib
import java.util.regex.Pattern
import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class RegrexTest {

	static String stripSshPrefix(String gitUrl){
		def match = (gitUrl =~ /ssh:\/\/(.+)/)
		if (match.find()) {
			return match.group(1)
		}
		return gitUrl
	}

	static String getAppName(String appNameWithServers){
		def matchApp = (appNameWithServers =~ /^([^\.]+)\.+\[.+\]/)
		if (matchApp.find()) {
			return matchApp.group(1)
		}
		return ""
	}

	static String getEGNames(String appNameWithServers){
		def matchEGs = (appNameWithServers =~ /^.+\.+\[(.+)\]/)
		if (matchEGs.find()) {
			return matchEGs.group(1)
		}
		return ""
	}

	static String getSharedLibName(ArrayList<String> appNameList, String appName){
		appNameList.each {
			if (it.contains(appName)) {
				matchSharedLib = (it =~ /^([^\.]+)\@.+/)
				if (matchSharedLib.find()) {
					return matchSharedLib.group(1)
				}
			}
		}
		return ""
	}


	static void main(String... args) {
		def gitUrl = "ssh://git@github.com:jiahut/boot.git"
		def gitUrl2 = "git@github.com:jiahut/boot.git"
		println(stripSshPrefix(gitUrl))
		println(stripSshPrefix(gitUrl2))
		def AppNames = "InventoryUpdate_Lib@CAL_Mainframe_InventoryUpdate_Pub,PriyaConfirmation_Lib@CALF_Priya_Export_PreProc,PromotionMovement_Lib@CA_PromotionMovement_Pub"
		def appNameList = AppNames.split(",")
		
		def appSvr1 = "WS_CreditAuthorization_Simulator................................................[WS_Pub_01]"
		def appSvr2 = "WS_TaxAndFee....................................................................[WS_Pub_01]"
		def appSvr3 = "Sub_ReceiptConfirmation_OMS.................................[OMS_Sub_02, OMS_Sub_03]"
		def sharedLibName = ""
		appNameList.each { 
			def matchSharedLib = (it =~ /^([^\.]+)\@.+/)
			if (matchSharedLib.find()) {
				sharedLibName = matchSharedLib.group(1)
			}
		}
		println "Shared_Lib_Name=" + sharedLibName
		//println(getAppName(appSvr1))
		//println(getEGNames(appSvr1))
		//println(getAppName(appSvr2))
		//println(getEGNames(appSvr2))
		//println(getAppName(appSvr3))
		//println(getEGNames(appSvr3))


	}
}
