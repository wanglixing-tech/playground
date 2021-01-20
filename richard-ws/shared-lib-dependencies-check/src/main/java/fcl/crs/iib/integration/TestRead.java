package fcl.crs.iib.integration;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

public class TestRead {
    public static void main(String args[]) {
    	String home = System.getProperty("user.home");

    	// inserts correct file path separator on *nix and Windows
    	// works on *nix
    	// works on Windows
    	java.nio.file.Path path = java.nio.file.Paths.get(home, "ws-java", "try-maven-compiler", ".project");
    	boolean fileExists = java.nio.file.Files.exists(path);
    	if (fileExists) {
    		System.out.println("PATH=" + path.toString());
    	}else {
    		System.out.println("The file does not exist");
    	}
        try {
			StaXParser.parser(path.toFile());
		} catch (FileNotFoundException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}