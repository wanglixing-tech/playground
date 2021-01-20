package crs.fcl.integration.iib

class testDir {
	static void main(args) {
		def IIB_HOME = "C:\\IIB\\iib"
		//def process = "cmd /c dir ${IIB_HOME}".execute();
		//println "${process.text}"
		def cmds = [
			'cmd',
			'/c',
			'dir C:\\IIB'
		]
		def proc = new ProcessBuilder( cmds )
		def out = new StringBuffer()
		def err = new StringBuffer()
		Process process1 = proc.start()
		process1.consumeProcessOutput( out, err )
		process1.waitFor()
		if( out.size() > 0 ) println out
		if( err.size() > 0 ) println err
	}
}