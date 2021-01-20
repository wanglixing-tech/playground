package yorozuya;

import java.util.Arrays;
import java.util.List;

public class testCmdLineParams {

	public static void main(String[] args) {

		// Convert String Array to List
		List<String> list = Arrays.asList(args);

		if ((! list.contains("-pl")) && ( ! list.contains("--projects"))){
			System.out.println("No projects specified as command-line arguments?");
			System.exit(1);
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals("-pl")  || list.get(i).equals("--projects")) {
				System.out.println(list.get(i + 1));
				break;
			}
		}

	}

}
