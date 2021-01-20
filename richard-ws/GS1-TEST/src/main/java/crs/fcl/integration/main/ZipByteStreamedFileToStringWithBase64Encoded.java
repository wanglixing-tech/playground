package crs.fcl.integration.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * This program is created based on Yaloslav's request for his testing from IIB;
 * The method is the one zipAndEncode(supplyCoverageReportName, bStream).
 * 
 * @author Richard.Wang
 *
 */
public class ZipByteStreamedFileToStringWithBase64Encoded {
	
	/**
	 * Supply Coverage Report Name is must when to zip a byte stream, which is the file name 
	 * when the zipped + encoded string being decoded and unzipped on the receiver side.
	 */
	private static String supplyCoverageReportName = "retailerFeedbackReportFile.xml";
	
	public static void main(String[] args) {
		/**
		 * Following two files are for testing the method zipAndEncode();
		 * 		1. sourceXMLFile: it is the file just for generating XML byte stream
		 * 		2. destinationXMLFile: it is the file the receiver extracts from 
		 *         zipped + enocded string in SOAP message.
		 */
		File sourceXMLFile = new File("C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\itemTemplate.xml");
		File destinationXMLFile = new File("C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\SupplyCompressedCoverageReport.zip");
		
		// Read source XML file and convert it to byte[]
		byte[] bStream = readFileToByteArray(sourceXMLFile);
		
		try {			
			/*
			 * Here is the method Yaloslav requested
			 */
			String strZippedAndEncoded = zipAndEncode(supplyCoverageReportName, bStream);
			
			/*
			 * Here is the method for for testing the string can be reversed
			 */
			decodeAndOutputZipFile(strZippedAndEncoded, destinationXMLFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method uses java.io.FileInputStream to read file content into a byte
	 * array
	 * 
	 * @param file
	 * @return
	 */
	private static byte[] readFileToByteArray(File file) {
		FileInputStream fis = null;
		// Creating a byte array using the length of the file
		// file.length returns long which is cast to int
		byte[] bArray = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			fis.read(bArray);
			fis.close();

		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		}
		return bArray;
	}

	/**
	 * Convert Byte stream to zipped file with specified file name
	 * 
	 * @param filename
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String zipAndEncode(String filename, byte[] input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry entry = new ZipEntry(filename);
		entry.setSize(input.length);
		zos.putNextEntry(entry);
		zos.write(input);
		zos.closeEntry();
		zos.close();
		byte[] bytes = baos.toByteArray();
		String encodedBase64 = new String(Base64.getEncoder().encodeToString(bytes));
		return encodedBase64;
	}

	public static void decodeAndOutputZipFile(String strZippedAndEncoded, File zipFileName) {
		byte[] decodedBytes = Base64.getDecoder().decode(strZippedAndEncoded);
        try { 
            OutputStream os  = new FileOutputStream(zipFileName); 
            os.write(decodedBytes); 
            System.out.println("Successfully bytes into the file " + zipFileName); 
            os.close(); 
        }   catch (Exception e) { 
            System.out.println("Exception: " + e); 
        } 
	}
}
