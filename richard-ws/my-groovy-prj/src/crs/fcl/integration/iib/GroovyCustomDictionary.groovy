/**
 *  Script Name  : GroovyCustomDictionary.groovy
 *
 *  Purpose      : To demonstrate the usage of Dictionary in Groovy language via Map Data Structure
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

def scriptSimpleName = this.class.getSimpleName()

println "================================================== "
println "Output of Script : " + scriptSimpleName
println "Executed at      : " + new java.util.Date()
println "================================================== "
println " " 

class Dictionary
{
    def key
    def value

    def dict = [:]

    /* Empty No-arg constructor. Required for the overloaded constructor below */
    Dictionary() {}

    /* A two-arg constructor to facilitate an entry to be added during instantiation */
    Dictionary(key, value) {
      put(key,value)
    }

    /* Method to validate the key, value inputs for not-null */
    def validate(key, value)
    {
        if(key==null)
            throw new RuntimeException("Null key is not permitted")
        
        if(value==null)
            throw new RuntimeException("Null value is not permitted")
    }

    /* Actual method to store the key-value pairs. 
     * Exception message printed if any of them is null.
     */
    def put(key, value) {
        try {
            validate(key,value)
            this.dict[key]=value
            printInfo()
        } catch(Exception exception) {
            println "  #### ERROR #### --> " + exception.getMessage()
            println " "
        }
    }

    /* Overridden toString() to have a meaningful display */
    String toString() {
        "[Dictionary] hashCode = ${this.hashCode()}, Dict : ${dict}"
    }

    /* Utility method for printing the information */
    def printInfo() {
        println "myDict Custom Object : ${this}"
        println " "
    }
}

def myDictObj = new Dictionary()
println " ==> 1. Initial Empty Dictionary ..."
myDictObj.printInfo()
println " ==> 2. Attempting to Store values ... "
myDictObj.put('name', 'Sam')
myDictObj.put('age', 14)
println " ==> 3. Attempting to store Null Key ... "
myDictObj.put(null, 'NotNullValue');
println " ==> 4. Attempting to store Null value ... "
myDictObj.put("NullKey",null);
println " ==> 5. Attempting with duplicate key (value will be replaced) ... "
myDictObj.put('name', 'Samuel')

println " "
println " ---------------- END OF SCRIPT EXECUTION --------------------  "
println " "
