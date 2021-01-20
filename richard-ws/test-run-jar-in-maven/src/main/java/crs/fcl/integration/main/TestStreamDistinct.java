package crs.fcl.integration.main;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Find all distinct elements
 * 
 * @author Richard.Wang
 *
 */
public class TestStreamDistinct {
	   public static void main(String[] args) {
		   Collection<String> list = Arrays.asList("A", "B", "C", "D", "A", "B", "C");
		   
		// Get collection without duplicate i.e. distinct only
		List<Object> distinctElements = list.stream().distinct().collect(Collectors.toList());		 
		//Let's verify distinct elements
		System.out.println(distinctElements);
	   }
}
