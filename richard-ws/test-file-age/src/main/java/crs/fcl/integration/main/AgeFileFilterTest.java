package crs.fcl.integration.main;

/*****************************************************************/
/* Copyright 2013 Code Strategies                                */
/* This code may be freely used and distributed in any project.  */
/* However, please do not remove this credit if you publish this */
/* code in paper or electronic form, such as on a web site.      */
/*****************************************************************/

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.filefilter.AgeFileFilter;

public class AgeFileFilterTest {

	public static void main(String[] args) throws IOException {

		File directory = new File("C:\\Users\\Richard.Wang\\fcl.crs\\worktrees\\iib\\release\\core-1.2");

		GregorianCalendar cal = new GregorianCalendar();
		cal.set(2008, 0, 15, 0, 0, 0); // January 15th, 2008
		Date cutoffDate = cal.getTime();

		System.out.println("All Files");
		displayFiles(directory, null);

		System.out.println("\nBefore " + cutoffDate);
		displayFiles(directory, new AgeFileFilter(cutoffDate));

		System.out.println("\nAfter " + cutoffDate);
		displayFiles(directory, new AgeFileFilter(cutoffDate, false));

	}

	public static void displayFiles(File directory, FileFilter fileFilter) {
		File[] files = directory.listFiles(fileFilter);
		for (File file : files) {
			Date lastMod = new Date(file.lastModified());
			System.out.println("File: " + file.getName() + ", Date: " + lastMod + "");
		}
	}

}