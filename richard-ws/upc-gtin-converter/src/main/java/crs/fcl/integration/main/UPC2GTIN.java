package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UPC2GTIN {
	final static int GTIN_LENGTH = 14;

	public static void main(String[] args) throws IOException {
		String upcFilePath = null;
		String gtinFilePath = null;
		String gtin = null;

		upcFilePath = System.getProperty("upcFile");
		gtinFilePath = System.getProperty("gtinFile");
		File upcf = new File(upcFilePath);
		File gtinf = new File(gtinFilePath);
		if (! upcf.exists()) {
			System.out.println("Input file " + upcFilePath + " does not exist, which is mandatory.");
			System.exit(1);
		}
		
		if (gtinf.exists()) {
			System.out.println("Output file " + gtinFilePath + " File already exists, recreate it......");
			gtinf.delete();
 		    gtinf.createNewFile();
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(upcFilePath));
			FileWriter fw = new FileWriter(gtinFilePath);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				gtin = upc2gtin(sCurrentLine);
				//fw.write(sCurrentLine + "," + gtin + "\r\n");
				fw.write(gtin + "\r\n");
			}
			br.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * To convert a UPC to a GTIN
	 * 
	 * @param upc
	 * @return gtin
	 */
	public static String upc2gtin(String upc) {
		StringBuilder codeWithCheckDigit = new StringBuilder(GTIN_LENGTH);

		char[] c_array = upc.toCharArray();
		int indexOf0 = GTIN_LENGTH - c_array.length - 1;
		char[] charZeros = new char[indexOf0];
		for (int i = 0; i < indexOf0; i++) {
			charZeros[i] = '0';
		}

		// System.out.println("Total fill zeros=" + Arrays.toString(charZeros));
		int result = 0;
		boolean f4m3 = true;
		for (int i = c_array.length - 1; i >= 0; i--) {
			int ic = Character.getNumericValue(c_array[i]);
			result = result + ic * (f4m3 ? 3 : 1);
			f4m3 = f4m3 ? false : true;
		}

		codeWithCheckDigit.append(charZeros);
		// System.out.print("Total=" + result + ";");
		int checkDigit = (int) ((Math.ceil(((double) result) / 10) * 10) - result);
		String ccd = Integer.toString(checkDigit);
		//System.out.println("CheckDigit: " + checkDigit);
		codeWithCheckDigit.append(upc);
		codeWithCheckDigit.append(ccd);
		//System.out.println("UPC:" + upc + "->GTIN:" + codeWithCheckDigit.toString());
		return codeWithCheckDigit.toString();
	}
}
