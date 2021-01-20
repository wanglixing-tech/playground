package crs.fcl.integration.iib
import groovy.io.FileType
import java.util.regex.Pattern

def list = []
def TARGET_DIR = "C:\\Users\\Richard.Wang\\fcl.crs\\repositories\\iib"
def possibleYaml4Master = "master-eos.yml"
def masterFile = new File(TARGET_DIR + File.separator + possibleYaml4Master)
def dir = new File( TARGET_DIR )

dir.eachFile (FileType.FILES) {
	def yamlFileName = it.getName()
	if (yamlFileName =~ /eos-.+\.yml/ ) {
		list.add(yamlFileName)
	}
}

Collections.reverse(list)
if (masterFile.exists()) {
	list.add(possibleYaml4Master)
}
//return list
println list
