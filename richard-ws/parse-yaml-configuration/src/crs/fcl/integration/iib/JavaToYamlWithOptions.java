package crs.fcl.integration.iib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class JavaToYamlWithOptions {

	  public static void main(String[] args) {
	      Map<String, Map<String, String>> map = createMap();
	      List<Map<String, String>> list1 = createMap1();

	      DumperOptions options = new DumperOptions();
	      //options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	      options.setPrettyFlow(true);
	        options.setDefaultFlowStyle( DumperOptions.FlowStyle.FLOW); 
	        options.setIndent( 2 ); 
	        options.setWidth( 80 ); 

	      Map<String, List<Map<String, String>>> topMap = new HashMap<String, List<Map<String, String>>>();
	      topMap.put("applications", list1);
	      Yaml yaml = new Yaml(options);
	      String output = yaml.dump(map);
	      //System.out.println(output);
	      String output1 = yaml.dump(topMap);
	      System.out.println(output1);
	  }

	  private static Map<String, Map<String, String>> createMap() {
	      Map<String, Map<String, String>> map = new HashMap<>();
	      for (int i = 1; i <= 3; i++) {
	          Map<String, String> map2 = new HashMap<>();
	          map2.put("key1" + i, "value1" + i);
	          map2.put("key2" + i, "value2" + i);
	          map2.put("key3" + i, "value4" + i);
	          map.put("key" + i, map2);
	      }
	      return map;
	  }
	  private static List<Map<String, String>> createMap1() {
		  Map<String, List<Map<String, String>>> topMap = new HashMap<String, List<Map<String, String>>>();
	      List<Map<String, String>> list = new ArrayList<>();
	      for (int i = 1; i <= 3; i++) {
	          Map<String, String> map2 = new HashMap<>();
	          map2.put("key1" + i, "value1" + i);
	          map2.put("key2" + i, "value2" + i);
	          map2.put("key3" + i, "value4" + i);
	          list.add(map2);
	      }
	      return list;
	  }
	}	

/**
	  private static Map<String, List<Map<String, String>>> createMap1() {
		  Map<String, List<Map<String, String>>> topMap = new HashMap<String, List<Map<String, String>>>();
	      List<Map<String, String>> list = new ArrayList<>();
	      for (int i = 1; i <= 3; i++) {
	          Map<String, String> map2 = new HashMap<>();
	          map2.put("key1" + i, "value1" + i);
	          map2.put("key2" + i, "value2" + i);
	          map2.put("key3" + i, "value4" + i);
	          list.add(map2);
	      }
	      return (Map<String, List<Map<String, String>>>) topMap.put("applications", list);
	  }
	}
*/