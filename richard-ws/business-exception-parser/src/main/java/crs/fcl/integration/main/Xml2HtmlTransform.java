package crs.fcl.integration.main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import dro.util.Properties;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Xml2HtmlTransform {
	private static final String className = Xml2HtmlTransform.class.getName();
	private static final java.util.logging.Logger logger = dro.util.Logger.getLogger(className);

	static {
		try {
			Properties.properties(new dro.util.Properties(Xml2HtmlTransform.class));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
			throws IOException, URISyntaxException, TransformerException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();

		//String path2Xml = "C:\\Users\\Richard.Wang\\WS-HOME\\ws-xslt\\business-exception-parser\\Business-Exception.xml";
		String path2Xml = "C:\\Users\\Richard.Wang\\WS-HOME\\ws-xslt\\business-exception-parser\\Business-Exception1.xml";
		Document doc = db.parse(new File(path2Xml));

		Xml2HtmlTransform.xml2html("Business-Exception:EOS-Sales-Order", doc);
	}

	/**
	 * This is a method to use event-based XSLT to parse XML Document object and generate an HTML Exception file
	 * 
	 * Event-based XSLT file names are predefined and named from their corresponding event name, For example,
	 * If an event name is "Business-Exception:EOS-Sales-Order", then its corresponding XSLT file name will be 
	 * "Business-Exception-EOS-Sales-Order.xsl", and the returned HTML exception file name will be:
	 * "Business-Exception-EOS-Sales-Order.html"
	 *  
	 * @param eventName - Event Name, which was yielded from IIB-logger.
	 * @param exception - Exception XML Document object, which contains detailed exception information
	 * @return - File Handler for an HTML-formatted output file created by event-based XSLT, 
	 *           the HTML file name 
	 * @throws TransformerException
	 */
	public static File xml2html(String eventName, Document exception) throws TransformerException {
		final Properties __p = Properties.properties();
		String path2Xslt = __p.getProperty("XSLT_FILE_DIR");
		String path2Html = __p.getProperty("HTML_FILE_DIR");
		logger.info("Exception Event NAME:" + "[" + eventName + "]");
		
		String xsltFile = path2Xslt + File.separator + eventName.replace(':', '-') + ".xsl";
		logger.info("Target XSLT FILE:" + xsltFile);

		File htmlExceptionFile = new File(path2Html + File.separator + eventName.replace(':', '-') + ".html");
		logger.info("Output HTML EXCEPTION FILE:" + htmlExceptionFile.getAbsolutePath());

		Source xslt = new StreamSource(new File(xsltFile));
		Source xmlException = new DOMSource(exception);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(xslt);

		transformer.transform(xmlException, new StreamResult(htmlExceptionFile));

		return htmlExceptionFile;
	}
}