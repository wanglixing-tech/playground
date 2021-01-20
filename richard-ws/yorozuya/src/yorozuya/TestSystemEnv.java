package yorozuya;

public class TestSystemEnv {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.getenv().forEach((k, v) -> {
		    System.out.println(k + ":" + v);
		    String log_dir = System.getenv("log_dir");
		    System.out.println("log_dir=" + log_dir);
		});
	}

}
