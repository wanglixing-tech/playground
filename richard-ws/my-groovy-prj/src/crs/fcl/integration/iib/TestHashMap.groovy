package crs.fcl.integration.iib
def emptyMap = [:]

def userMap = [username: "didin@djamware.com", password: "abcdf12345", fullname: "Didin J."]
def defaultMap = [name: "Eric Cantona", team: "Manchester United", roles: "Striker", age: 28]

def prodMap = [:]
def prodShadowMap = [:]

// There are more than one way to set map key:value
prodMap["prod1Name"] = "A First generation of iPhone X"
prodMap["prod1Desc"] = "A First generation of iPhone X with 8GB RAM and 256 Internal Memory"
prodMap["prod1Price"] = 999

prodMap.prod2Name = "A Second generation of iPhone X"
prodMap.prod2Desc = "A Second generation of the Samsung Galaxy S series"
prodMap.prod2Price = 1099

prodMap."prod3Name" = "iPhone XS"
prodMap."prod3Desc" = "A Thrid generation of the Samsung Galaxy S series"
prodMap."prod3Price" = 10999

prodMap.put("prod4Name", "iPhone XS")
prodMap.put("prod4Desc", "The latest iPhone series")
prodMap.put("prod4Price", 1199)

prodShadowMap = prodMap.
println "Print all prodMap and remove all"
prodMap.each { m -> println "${m.key}: ${m.value}" 
	prodMap.remove("${m.key}")
}

println "Current prodMap has " + prodMap.size()
println "Print all prodMap again:"
prodMap.each { m -> println "${m.key}: ${m.value}"
}

println "Print all userMap items:"
userMap.each { m -> 
	println "${m.key}: ${m.value}" 
}

println "Remove fullname"
userMap.remove("fullname")

println "Print all userMap items again:"
userMap.each { m -> println "${m.key}: ${m.value}" }
	

