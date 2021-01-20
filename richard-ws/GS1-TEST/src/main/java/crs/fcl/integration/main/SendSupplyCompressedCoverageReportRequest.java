package crs.fcl.integration.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * This is a program used to help evaluating GS1 Web Service capacity and
 * performance created based on [JIRA] (ESB-189) PoC for GK/GS1 Web Interface
 * 
 * @author Richard.Wang
 * @Date 2019/05/28
 */
public class SendSupplyCompressedCoverageReportRequest {
	public static String supplyCompressedCoverageReportRequest = "C:\\Users\\Richard.Wang\\Documents\\My_DailyWork\\GS1\\coverageReportRequest-new-gtinList.xml";
	public static String url4GS1WS = "https://api.brandbank.com/svc/feed/reportdata.asmx";

	public static void main(String[] args) throws Exception {
		URL obj = new URL(url4GS1WS);
		String requestXMLString = new String(Files.readAllBytes(Paths.get(supplyCompressedCoverageReportRequest)));

		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/soap+xml; charset=ISO-8859-1");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(requestXMLString);
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
}
