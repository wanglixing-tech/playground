package crs.fcl.integration.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class FileObserverExecutor {
	public static void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("ShutdownHook running......");
				System.exit(0);
			}
		});
	}

	public static void main(String[] args) throws InterruptedException {
		attachShutDownHook();
		ExecutorService executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

		FileObserver observer = new FileObserver("Hello Mail!" , 30);
		Future<Integer> future = executor.submit(observer);
		
		//try { System.out.println("attempt to shutdown executor");
		//  executor.shutdown(); } finally { if (!executor.isTerminated()) {
		//  System.err.println("cancel non-finished tasks"); } executor.shutdownNow();
		//  System.out.println("shutdown finished"); }
		}
}