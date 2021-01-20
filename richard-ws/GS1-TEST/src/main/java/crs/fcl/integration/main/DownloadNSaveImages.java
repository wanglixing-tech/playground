package crs.fcl.integration.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.xmlbeans.XmlException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import noNamespace.ImageType;
import noNamespace.MessageDocument;
import noNamespace.ProductType;

public class DownloadNSaveImages {
	public static String imageDestDir = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Images";
	public static String srcFileName = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Data-2019.07.25.15.19.28\\response-getUnsentProdData002.xml";
	public final static String workingXmlFile = "C:\\Temp\\working.xml";

	public static void main(String[] args) throws IOException, XmlException {
		try {
			// the image file name must be in URL
			File inputXMLFile = new File(srcFileName);

			buildMessageDocument(inputXMLFile);

			MessageDocument msgDoc = MessageDocument.Factory.parse(new File(workingXmlFile));
			ProductType[] productArray = msgDoc.getMessage().getProductArray();

			System.out.println("Total products number:" + productArray.length);
			/*
			 * For one lot, there may be maximum 250 products, if download all images, it
			 * will become a huge numbers and use lots of space, please keep in mind to
			 * download only what you want to run this program!!!!
			 */
			for (int i = 0; i < productArray.length; i++) {
				System.out.println("-----------------------------------------------");
				String GTIN = productArray[i].getIdentity().getProductCodes().getCodeArray(0).getStringValue();
				ImageType[] immageArray = findAllImagesInProduct(productArray[i]);
				for (int j = 0; j < immageArray.length; j++) {
					// URL defined in product Assets does not contain the image file name
					String strShortTypeId =  String.valueOf(getImageShortTypeId(immageArray[j]));
					String imageUrl = getImageUrl(immageArray[j]);
					String imageFileType = getImageFileType(immageArray[j]);
					String imageId = getImageId(immageArray[j]);
					String imageFullFileName = imageDestDir + File.separator + GTIN + "-" + strShortTypeId  + "." + imageFileType;
					System.out.println("ImageURL=" + imageUrl);
					System.out.println("ImageType=" + imageFileType);
					System.out.println("ImageId=" + imageId);
					saveImage(imageUrl, imageFullFileName);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static ImageType[] findAllImagesInProduct(ProductType productDoc) {
		return productDoc.getAssets().getImageArray();
	}
	
	public static String getImageUrl(ImageType imageDoc) {
		return imageDoc.getUrl().getStringValue();
	}

	public static int getImageShortTypeId(ImageType imageDoc) {
		return imageDoc.getShotTypeId();
	}

	public static String getImageFileType(ImageType imageDoc) {
		String imageMimeType = imageDoc.getMimeType();
		String[] imts = imageMimeType.split("/");
		return imts[1];
	}

	public static String getImageId(ImageType imageDoc) {
		String strUrl = imageDoc.getUrl().getStringValue();
		String[] imageIds = strUrl.split("\\?id\\=");
		return imageIds[1];
	}

	public static void saveImage(String imageUrl, String imageFileName) throws IOException {
		URL url = new URL(imageUrl);
		System.out.println(imageFileName);

		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(imageFileName);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}

	private static void buildMessageDocument(File file) throws DocumentException {
		try {
			SAXReader reader = new SAXReader();
			Document unsentProductSOAPResult = reader.read(file);

			String sXPath2targetDocument = ".//*[name()='Message']";
			Element element = ((Element) unsentProductSOAPResult.selectSingleNode(sXPath2targetDocument)).createCopy();
			Document document = DocumentHelper.createDocument(element);
			// document.setXMLEncoding("ISO-8859-1");
			// Create a temporary xml file
			FileOutputStream fos = new FileOutputStream(workingXmlFile);
			// Create the pretty print of xml document.
			// OutputFormat format = OutputFormat.createCompactFormat();
			// OutputFormat outFormat = new OutputFormat();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setNewLineAfterDeclaration(false);
			format.setIndentSize(2);
			format.setEncoding("ISO-8859-1");

			// Create the xml writer by passing outputstream and format
			XMLWriter writer = new XMLWriter(fos, format);
			// Write to the xml document
			writer.write(document);
			// Flush after done
			writer.flush();
			writer.close();
			fos.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
