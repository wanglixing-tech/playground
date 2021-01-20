package crs.fcl.integration.iib

class MyGroovy4Jenkins {

	static void main(args) {
		// TODO Auto-generated method stub
		println("Hello Richard!");
		def grepPattern = ~/A\w+/
		def cities = ['Alabama', 'Los Angeles', 'Arizona']
		println cities.grep(grepPattern) // [Alabama, Arizona]
		
		def date = new Date()
		println date // Sat Sep 26 19:22:50 EEST 2015
		def calendar = Calendar.instance
		println calendar // java.util.GregorianCalendar[time=1443284933986, ...
	}
}
