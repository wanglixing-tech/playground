package yorozuya;

public class ReplaceCharTest {

	public static void main(String args[]) {
		String s1 = "javatpoint/is a/very good website";
		String replaceString = s1.replace('/', '-');// replaces all occurrences of a to e
		System.out.println(replaceString);
	}
}