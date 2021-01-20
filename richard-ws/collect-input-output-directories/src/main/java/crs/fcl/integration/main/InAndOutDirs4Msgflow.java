package crs.fcl.integration.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class InAndOutDirs4Msgflow {
	private static final String domainHome = "C:\\Users\\Richard.Wang\\WORKSPACE\\C-eos-1.3";
	private static final String unixPathPrefix = "/";
	
	public static void main(String[] args) throws IOException {
		List<String> msgflowList = getAllMsgflowsInDomain(domainHome);
		Set<String> inAndOutDirs = new HashSet<String>();
		//msgflowList.forEach(System.out::println);
		
		for (String flowFile : msgflowList) {
			inAndOutDirs.addAll(getAllDirsFromMsgflow(flowFile));		
		}
		
		//inAndOutDirs.stream().sorted().forEach(System.out::println);
		
		for (String dir : inAndOutDirs) {
			//String dirWithoutprefix = winDir.substring(2);
			//String unixDir = FilenameUtils.separatorsToUnix(dirWithoutprefix);
			System.out.println(dir);
		}
	}

	private static List<String> getAllMsgflowsInDomain(String domainHome) throws IOException {
		List<String> domainFlows = new ArrayList<String>();
		Path start = Paths.get(domainHome);
		try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
			domainFlows = stream
		        .map(String::valueOf)
		        .filter(p -> p.endsWith("msgflow"))
		        .sorted()
		        .collect(Collectors.toList());		    
		}
		return domainFlows;
	}
	
	private static Set<String> getAllDirsFromMsgflow(String flowFileName) {
		Set<String> inAndOutDirs = new HashSet<String>();
		try {
			SAXReader reader = new SAXReader();
			Document document;
			document = reader.read(flowFileName);
			String sXPath2targetNodes = "//*[name() = 'nodes']";
			List<Node> targetNodes = document.selectNodes(sXPath2targetNodes);
			for (Node n : targetNodes) {
				Element el = (Element) n;
                String inDir = el.attributeValue("inputDirectory");
                if (inDir != null) {
        			String dirWithoutprefix = inDir.substring(2);
        			String unixDir = FilenameUtils.separatorsToUnix(dirWithoutprefix);
                	inAndOutDirs.add(unixDir);
                }
                String outDir = el.attributeValue("outputDirectory");
                if (outDir != null) {
        			String dirWithoutprefix = outDir.substring(2);
        			String unixDir = FilenameUtils.separatorsToUnix(dirWithoutprefix);
                	inAndOutDirs.add(unixDir);
                }
			}
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inAndOutDirs;
	}
	
	private static void convertDirsFromWins2Unix(Set<String> winsDirs, Set<String> unixDirs) {
		return;
	}
	
	private static void outputDirs2File(Set<String> unixDirs, String fileName) {
		return;
	}
}
