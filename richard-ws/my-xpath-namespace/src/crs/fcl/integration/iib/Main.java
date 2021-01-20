package crs.fcl.integration.iib;

import java.io.StringReader;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Main {

  public static void main(String[] args) throws Exception {
    String xml = "<soapenv:Envelope "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        + "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        + "xmlns:ser=\"http://services.web.post.list.com\"><soapenv:Header>"
        + "<authInfo xsi:type=\"soap:authentication\" "
        + "xmlns:soap=\"http://list.com/services/SoapRequestProcessor\">"
        + "<username xsi:type=\"xsd:string\">asdf@g.com</username>"
        + "<password xsi:type=\"xsd:string\">asdf</password></authInfo></soapenv:Header></soapenv:Envelope>";
    System.out.println(xml);
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true);
    DocumentBuilder builder = domFactory.newDocumentBuilder();
    Document doc = builder.parse(new InputSource(new StringReader(xml)));
    XPath xpath = XPathFactory.newInstance().newXPath();
    xpath.setNamespaceContext(new NamespaceContext() {

      @Override
      public Iterator getPrefixes(String arg0) {
        return null;
      }

      @Override
      public String getPrefix(String arg0) {
        return null;
      }

      @Override
      public String getNamespaceURI(String arg0) {
        if ("soapenv".equals(arg0)) {
          return "http://schemas.xmlsoap.org/soap/envelope/";
        }
        return null;
      }
    });
    XPathExpression expr = xpath
        .compile("/soapenv:Envelope/soapenv:Header/authInfo/password");
    Object result = expr.evaluate(doc, XPathConstants.NODESET);
    NodeList nodes = (NodeList) result;
    System.out.println("Got " + nodes.getLength() + " nodes");
  }
}