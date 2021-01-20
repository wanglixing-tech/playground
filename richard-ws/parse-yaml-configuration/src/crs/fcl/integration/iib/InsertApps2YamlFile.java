package crs.fcl.integration.iib;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InsertApps2YamlFile {
	private static String projectVersion = "1.3";
	public static void main(String[] args) {
		Path dcf = Paths.get("C:\\Temp\\test.txt");
		String s = System.lineSeparator() + "applications";
		ArrayList<Map<String, String>> list = createList();
		Map<String, ArrayList<Map<String, String>>> apps = new HashMap();
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		apps.put("applications", list);
		Yaml yaml = new Yaml(options);
		String output = yaml.dump(apps);
	
		try (BufferedWriter writer = Files.newBufferedWriter(dcf, StandardOpenOption.APPEND)) {
		    writer.write(s);
		    writer.write(System.lineSeparator() + output);
		} catch (IOException ioe) {
		    System.err.format("IOException: %s%n", ioe);
		}
		
	}
	
	private static ArrayList<Map<String, String>> createList() {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("appName", "EventLoggger");
			map.put("appType", "APP");
			map.put("appVersion",projectVersion);
			list.add(map);
		}
		return list;
	}

}
