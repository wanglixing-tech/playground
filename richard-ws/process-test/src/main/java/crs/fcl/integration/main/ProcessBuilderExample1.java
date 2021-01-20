package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ProcessBuilderExample1 {

    public static void main(String[] args) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder("notepad.exe");
		Process process = builder.start();
		boolean exitStatus = process.waitFor(30, TimeUnit.SECONDS);
		System.out.println("exitStatus==" + exitStatus);
		if (!exitStatus) {
			process.destroy();
			if(process.isAlive()) {
				process.destroyForcibly();
			}
		}
	}
}