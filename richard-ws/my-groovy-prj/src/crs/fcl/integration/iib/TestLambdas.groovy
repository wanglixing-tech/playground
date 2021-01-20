package crs.fcl.integration.iib

List<Integer> list = [5,6,7,8]
list.each({line -> println line})
list.each({println it})


// calculate the sum of the number up to 10

def total = 0
(1..10).each {total+=it}
println total

def List strings = "this is a long sentence".split();
strings.sort({s1, s2 -> s1.size() <=> s2.size()});
println strings