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

public class JavaZip {
	public static void main(String[] args) throws IOException {
		String sourceFile = "Product.xml";
		FileOutputStream fos = new FileOutputStream("compressed.zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(sourceFile);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		zipOut.close();
		fis.close();
		fos.close();

		System.out.println("Base-64 String:" + zipB64(new File("Product.xml")));
	}

	private static String zipB64(File file) throws IOException {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			try (FileInputStream fis = new FileInputStream(file)) {
				zos.putNextEntry(new ZipEntry(file.getName()));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
			}
		}
		write2FilefromByteStream(baos, "compressed_from_bytes.zip");
		byte[] bytes = baos.toByteArray();
		String encodedBase64 = new String(Base64.getEncoder().encodeToString(bytes));
		return encodedBase64;
	}

	private static void write2FilefromByteStream(ByteArrayOutputStream byteOutStream, String outputFile) throws IOException {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(outputFile);
			// writing bytes in to byte output stream
			byteOutStream.writeTo(outStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outStream.close();
		}
	}
}
