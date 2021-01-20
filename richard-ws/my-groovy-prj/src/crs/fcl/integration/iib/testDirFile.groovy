package crs.fcl.integration.iib

@Grab('org.yaml:snakeyaml:1.21')
import static groovy.io.FileType.FILES
def dir = new File("C:\\Temp");
def files = [];
dir.traverse(type: FILES, maxDepth: 0) { files.add(it) };
println files;