/**
 *  Script Name  : GroovyCountryCapitalDictionary.groovy
 *
 *  Purpose      : To demonstrate the usage of Dictionary in Groovy language via Map Data Structure with an example
 *                 a few countries and the respective capital cities
 *
 *  Environment  : Tested with -> Groovy Version: 2.4.3 JVM: 1.7.0_80 Vendor: Oracle Corporation OS: Windows 7
 *
 *  URL to refer : https://examples.javacodegeeks.com/jvm-languages/groovy/groovy-map-example/
 *                 http://docs.oracle.com/javase/8/docs/api/java/util/Map.html
 *		   http://docs.groovy-lang.org/docs/groovy-2.4.0/html/groovy-jdk/java/util/Map.html
 * 
 *  Author       : Raghavan alias Saravanan Muthu (Java Code Geeks)
 *  Date         : 23 Mar 2016
 */

package crs.fcl.integration.iib;

def file = new File('C:\\Users\\Richard.Wang\\WS-HOME\\ws-groovy\\my-groovy-prj\\src\\crs\\fcl\\integration\\iib\\country-capital.txt')
//println file.getAbsolutePath()

def validLines = []

file.each { line -> 
    if(line.trim().length()>0 && !line.startsWith("#"))
       validLines.add(line) 
}

def dict = [:]
def key, value
def tokens

validLines.each { line -> 
    tokens = line.tokenize(",")
    key = tokens[0].trim()
    value = tokens[1].trim()
    validate(key,value)
    dict[key]=value       
}

/* Method to validate the key, value inputs for not-null */
def validate(key, value)
{
    if(key==null)
        throw new RuntimeException("Null key is not permitted")
    
    if(value==null)
        throw new RuntimeException("Null value is not permitted")
}

def getCountryStartsWith = { String s ->
    def result = []
    dict.keySet().each {
        if(it.startsWith(s))
           result.add(it)
    }
    result
}

def getCapitalStartsWith = { String s ->
    def result = []
    dict.values().each {
        if(it.startsWith(s))
          result.add(it)
    }
    result
}

def printUsage() {
    def scriptName = this.class.getSimpleName()
    println ""
    println "[Usage] Please use any of the following : "
    println "  1.  $scriptName <[Country:<Alphabet>] [Capital:<Alphabet>]"
    println "  2.  $scriptName print --> to print the dictionary"
    System.exit(1)
}

def handleInvalidArgs() {
    println " "
    println " ## Please specify the input in proper format"
    printUsage()
}

if(args.length<1) {
   printUsage()   
}

if(args[0].equalsIgnoreCase("print")) {
    println " "
    println "Total Elements in Dictionary : " + dict.size() 
    println " "
    println " Dictionary Contents  "
    println "---------------------------"
    println dict
    System.exit(0)
} else {
    def argType
    def argTokens
    def inputArg = args[0]    

    argTokens = args[0].tokenize(':')

    if(argTokens.size()<2) {
        handleInvalidArgs()
    }
    
    argType = argTokens[0]
    
    if(argType.equalsIgnoreCase("country")) {
        def countryAlphabet = argTokens[1]
    println ""
        println "Country starts with '${countryAlphabet}' : "
        println getCountryStartsWith("${countryAlphabet}")
    } 
    else if(argType.equalsIgnoreCase("capital")) {
        def capitalAlphabet = argTokens[1]
        println ""
        println "Capital starts with '${capitalAlphabet}' : "
        println getCapitalStartsWith("${capitalAlphabet}")
    } 
    else {
        handleInvalidArgs()
    }
}
