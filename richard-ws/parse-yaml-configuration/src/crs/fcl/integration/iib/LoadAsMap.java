package crs.fcl.integration.iib;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class LoadAsMap {

	public static void main(String[] args) throws IOException {
		//loadFromFile("person.yml");
		//loadFromFile("persons.yml");
		loadFromFile("release-tronia-3.0.yml");
	}

	private static void loadFromFile(String path) throws IOException {
		System.out.printf("-- loading from %s --%n", path);
		Yaml yaml = new Yaml();
		InputStream in = LoadAsMap.class.getResourceAsStream(path);
        Iterable<Object> itr = yaml.loadAll(in);

        for (Object o : itr) {
            System.out.println(o);
        }
	}
}