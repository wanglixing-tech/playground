package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class GetUnsentProductDataNAcknowlegeThem {
	public static String url4GS1WS = "https://api.brandbank.com/svc/feed/extractdata.asmx";
	public static URL obj = null;
	// Follow destination folder need to be changed if you want to keep existing data
	public static String destinationFolderName = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\Data-TIMESTAMP";
	public static String destinationFolder = "";
	//
	public static String request4UnsentProdDataFileName = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\request-getUnsentProdData.xml";
	public static String request4AcknowlegeFileName = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\request-acknowlege.xml";
	public static String responseFilePrefix = "";

	public static void main(String[] args) throws Exception {
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
		System.out.println("Current Date and time:" + timeStamp);
		destinationFolder = destinationFolderName.replaceAll("TIMESTAMP", timeStamp);
		System.out.println("destinationFolder:" + destinationFolder);
		new File(destinationFolder).mkdir();
		responseFilePrefix = destinationFolder + File.separator + "response-getUnsentProdData";
		// Maximum lots should be maximum GTINS (45482) / 250 (one lot can contain maximum GTINs) ~= 180
		int lots = 180;
		String defaultCharacterEncoding = System.getProperty("file.encoding");
		System.out.println("defaultCharacterEncoding by property: " + defaultCharacterEncoding);
		System.out.println("defaultCharacterEncoding by code: " + getDefaultCharEncoding());
		System.out.println("defaultCharacterEncoding by charSet: " + Charset.defaultCharset());

		obj = new URL(url4GS1WS);
		Instant start = Instant.now();
		File request4AcknowlegeFile = new File(request4AcknowlegeFileName);
		Document acknowlegeRequest = parseXML(request4AcknowlegeFile);
		String requestUPDXMLString = new String(Files.readAllBytes(Paths.get(request4UnsentProdDataFileName)));
		for (int i = 1; i <= lots; i++) {
			String numberAsString = String.format("%03d", i);
			String outputFileName = responseFilePrefix + numberAsString + ".xml";
			doGetUnsentProdData(requestUPDXMLString, outputFileName);
			
			Document doc = parseXML(new File(outputFileName));
			
			int totalDeletedProds = countDeletedProds(doc);
			System.out.println("Total deleted products in this batch: " + totalDeletedProds);
			
			String msgId = getMessageId(doc);
			System.out.println("MessageId=" + msgId);
			setMessageGuid(acknowlegeRequest, msgId);
			System.out.println("AcknowlegeRequest=" + acknowlegeRequest.asXML());
			doAcknowlege(acknowlegeRequest.asXML());
			
			// Review the pre-defined maximum lots base on the number of products in the last batch
			// if fewer than 250, we can see this is the last batch
			int totalProdsInDoc = countAllProds(doc);
			System.out.println("Total products in this batch: " + totalDeletedProds);
			if (totalProdsInDoc == 0) {
				break;
			}
		}
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println("Total elapsed time is " + timeElapsed + "ms");
	}

	/**
	 * To parse XML and upload it as a Document object
	 * 
	 * @param file XML file
	 * @return Document object
	 * @throws DocumentException possible exceptions
	 */
	public static Document parseXML(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		return document;
	}

	public static int countAllProds(Document doc) {
		List<Node> allProdNodeList = doc.selectNodes("//*[name() = 'Product']");
		return allProdNodeList.size();
	}

	public static int countDeletedProds(Document doc) {
		List<Node> deletedNodeList = doc.selectNodes("//*[@UpdateType = 'Deleted']");
		return deletedNodeList.size();
	}

	public static String getMessageId(Document doc) throws DocumentException {
		Element messageElem = (Element) doc.selectSingleNode("//*[name() = 'Message']");
		return messageElem.attributeValue("Id");
	}

	public static void setMessageGuid(Document doc, String msgId) {
		Element messageElem = (Element) doc.selectSingleNode("//*[name() = 'ns:messageGuid']");
		messageElem.setText(msgId);
	}

	public static void doGetUnsentProdData(String getUnsentProdDataRequest, String outputFileName) throws Exception {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "text/xml; charset=ISO-8859-1");
		con.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(getUnsentProdDataRequest);
		wr.flush();
		wr.close();

		String responseStatus = con.getResponseMessage();
		System.out.println(responseStatus);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream(), StandardCharsets.ISO_8859_1));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println("Response:");
		Source xmlInput = new StreamSource(new StringReader(response.toString()));
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");		
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(xmlInput,
		        new StreamResult(new FileOutputStream(outputFileName)));
	}

	public static void doAcknowlege(String acknolegeRequest) throws Exception {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/soap+xml; charset=ISO-8859-1");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(acknolegeRequest);
		wr.flush();
		wr.close();

		String responseStatus = con.getResponseMessage();
		System.out.println(responseStatus);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream(), StandardCharsets.ISO_8859_1));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println("Response:");
		Source xmlInput = new StreamSource(new StringReader(response.toString()));
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");		
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(xmlInput,
		        new StreamResult(System.out));
	}

	/**
	 * This will be used to output the generated SOAP request message xml file.
	 * 
	 * @param document  XML Document object
	 * @param sFileName XML File name to be written to
	 * @throws IOException possible exception
	 */
	public static void write(Document document, String sFileName) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		// lets write to a file
		System.out.println("Output SOAP request message to XML file......" + sFileName);
		XMLWriter writer = new XMLWriter(new FileWriter(sFileName), format);
		writer.write(document);
		writer.close();
	}
	/**
	 * THis is an alternative method for forcibly modifying encoding in XML file, 
	 * it's not being used as we are applying Transformer to SOAP response XML string
	 * @param inXmlFile
	 * @param outXmlFile
	 * @throws IOException
	 */
	public static void modifyEncoding(String inXmlFile, String outXmlFile) throws IOException {
		String ISOEncodingStr = "ISO-8859-1";
		Path pathIn = Paths.get(inXmlFile);
		Path pathOut = Paths.get(outXmlFile);
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(pathIn), charset);
		content = content.replaceAll("UTF-8", ISOEncodingStr);
		Files.write(pathOut, content.getBytes(charset));
	}

	public static String getDefaultCharEncoding() {
		byte[] bArray = { 'w' };
		InputStream is = new ByteArrayInputStream(bArray);
		InputStreamReader reader = new InputStreamReader(is);
		String defaultCharacterEncoding = reader.getEncoding();
		return defaultCharacterEncoding;
	}
}
