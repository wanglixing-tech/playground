package crs.fcl.integration.iib

// write the content of the file to the console
File file = new File("C:\\Temp\\report.txt")
file.eachLine{ line -> println line }

// adds a line number in front of each line to the console
def lineNumber = 0
file = new File("C:\\Temp\\report.txt")
file.eachLine{ line ->
	lineNumber++
	println "$lineNumber: $line"
}

// read the file into a String
String s = new File("C:\\Temp\\report.txt").text
println s