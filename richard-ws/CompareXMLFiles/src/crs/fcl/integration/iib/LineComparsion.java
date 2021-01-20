package crs.fcl.integration.iib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.ListIterator;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

public class LineComparsion {

	public static void main(String args[]) throws IOException {
        String file1Text = new String(Files.readAllBytes(new File("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo1.xml").toPath()));
        String file2Text = new String(Files.readAllBytes(new File("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo2.xml").toPath()));

        //String file1Text = readFile("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo1.xml");
        //String file2Text = readFile("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo2.xml");
		compareFile(file1Text, file2Text);
		System.gc();

	}

	private static String compareFile(String text1, String text2) {

		diff_match_patch dmp = new diff_match_patch();
		dmp.Diff_Timeout = 0;

		// Execute one reverse diff as a warmup.
		//LinkedList difference21 = dmp.diff_main(text2, text1, false);

		//ListIterator itr = difference21.listIterator();
		//String diff21 = dmp.diff_prettyHtml(difference21);
		//System.out.println(abc);

		//long start_time = System.currentTimeMillis();
		LinkedList<Diff> difference12 = dmp.diff_main(text1, text2, true);
		//ListIterator<Diff> itr12 = difference12.listIterator();
		String diff12 = dmp.diff_prettyHtml(difference12);

		//long end_time = System.currentTimeMillis();
		//System.out.printf("Elapsed time: %f\n", ((end_time - start_time) / 1000.0));
		return diff12;
		// Read a file from disk and return the text contents.

	}

	private static String readFile(String filename) {
		// Read a file from disk and return the text contents.
		StringBuffer strbuf = new StringBuffer();
		try {
			FileReader input = new FileReader(filename);
			BufferedReader bufRead = new BufferedReader(input);
			String line = bufRead.readLine();
			while (line != null) {
				strbuf.append(line);
				strbuf.append('\n');
				line = bufRead.readLine();
			}

			bufRead.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return strbuf.toString();
	}
}