package crs.fcl.integration.iib;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class LoadMapList {
	public static void main(String[] args) throws IOException {
		loadFromFile("release-tronia-3.0.yml");
	}

	private static void loadFromFile(String path) throws IOException {
		System.out.printf("-- loading from %s --%n", path);
		Yaml yaml = new Yaml();
		try (InputStream in = LoadMapList.class.getResourceAsStream(path)) {
			Iterable<Object> itr = yaml.loadAll(in);
			for (Object o : itr) {
				System.out.println("Loaded object type:" + o.getClass());
				Map<String, List<String>> map = (Map<String, List<String>>) o;
				System.out.println("-- the map --");
				System.out.println(map);
				System.out.println("-- iterating --");
				map.entrySet().forEach((e) -> {
					if ( e.getKey().equals("domainName")) {
						System.out.println("key: " + e.getKey());
						System.out.println("values:" + e.getValue());
					}else {
						if (e.getKey().equals("applications")) {
							System.out.println("key: " + e.getKey());
							System.out.println("values:" + e.getValue());
						}
					}
				});
			}
		}
	}
}