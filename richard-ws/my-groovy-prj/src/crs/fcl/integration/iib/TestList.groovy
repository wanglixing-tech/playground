package crs.fcl.integration.iib

def list= ['a','b','c','b','b']
listShadow = []
listShadow.addAll(list)
list.add("XYZ")

list.remove('a')

list.each { println "Item: ${it}" }

listShadow.each { println "ItemInShadow: ${it}" }
/**
 * The first distinction between each() and forEach() is that each() is provided by Groovy's GDK, 
 * while forEach() is provided by Java 8 (so it is not available in prior versions of Java.
 * Another difference is that each() accepts a Groovy closure while forEach() accepts a Consumer. 
 * From Groovy, this difference is not noticeable because Groovy transparently coerces the closure to a Consumer.
 */
listShadow.forEach { println "ItemInShadow: ${it}" }