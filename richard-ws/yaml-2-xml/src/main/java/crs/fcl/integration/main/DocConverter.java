package crs.fcl.integration.main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.opencsv.CSVReader;
import com.thoughtworks.xstream.XStream;


public final class DocConverter {
	
	public static void main(String[] args) throws IOException {
		/*
		 * Read yaml file as string
		 */
		String inFileName = "C:\\IIB\\iib\\eos-1.3.yml";
		String outFileName = "C:\\Users\\Richard.Wang\\eos-1.3.xml";
		String xmlString = convertYamlToXml(convertFileToString(inFileName));
		
		convertStringToFile(outFileName, xmlString);
	}

	private static String xml;
	private static String yaml;
	private static String json;

	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	private static String csv;

	
	// String conversions

	/**
	* @param inputstream InputStream 
    * @return String
	*/	
	public static String convertStreamToString(InputStream inputstream) {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(inputstream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/**
	* @param string String
    * @return InputStream
    * @throws UnsupportedEncodingException
	*/	
	public static InputStream convertStringToStream(String string) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(string.getBytes("UTF-8"));
	}

	
	/**
	* @param doc (org.w3c.dom.Document XML document)
    * @return String
	*/	
	public static String convertDocToString(Document doc) {
		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}

	/**
	* String conversion
	* 
	* @param string String
    * @return Document (org.w3c.dom.Document XML document)
	*/
	public static Document convertStringToDoc(String string) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		return builder.parse(new ByteArrayInputStream(string.getBytes()));
	}

	/**
	* @param uri (java.net.URI)
    * @return String
	*/
	public static String convertUriToString(URI uri) throws Exception {

		URL url = uri.toURL(); // get URL from your uri object
		InputStream stream = url.openStream();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document doc = builder.parse(stream);
		
		String docAsString = convertDocToString(doc); 
		
		return docAsString;
	}

	/**
	* @param uri (java.net.URI)
    * @return Document (org.w3c.dom.Document XML document) 
	*/	
	public static Document convertUriToDoc(URI uri) throws Exception {

		URL url = uri.toURL(); // get URL from your uri object
		InputStream stream = url.openStream();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		return builder.parse(stream);
	}

	/**
	* @param path as string (uses UTF-8 for encoding)
    * @return String
	*/
	   public static String convertFileToString(String path) throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  String fileAsString = new String(encoded, StandardCharsets.UTF_8);
		  return fileAsString;
	}

	/**
	* @param path as String
	* @param encoding Charset
    * @return String
	*/
	public static String convertFileToString(String path, Charset encoding) throws IOException {
		if(encoding==null) {
			encoding = StandardCharsets.UTF_8;
		}
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  String fileAsString = new String(encoded, encoding);
		  return fileAsString;
	}

    /**
    * @param path as String
    * @param content as String
    */
	public static void convertStringToFile(String path, String content) throws IOException {
		Files.write( Paths.get(path), content.getBytes(), StandardOpenOption.CREATE);
	}

	/**
	* @param list
    * @return String
	*/
	public static String convertListToString(List<String> list)  {
		String listAsString = String.join(",", list);
		return listAsString;
	}

	/**
	* @param commaSeparatedString
    * @return List
	*/	
	public static List<String> convertStringToList(String commaSeparatedString)  {
		List<String> stringAsList = new ArrayList<String>(Arrays.asList(commaSeparatedString.split(",")));
		return stringAsList;
	}	

	/**
	* @param string
    * @return Reader
	*/	public static Reader convertStringToReader(String string)  {
		Reader reader = new StringReader(string);
		return reader;
	}

	/**
	* @param reader (java.io.Reader)
    * @return String
	* @throws IOException
	*/		
	public static String convertStringToReader(Reader reader) throws IOException  {
		String readerAsString = IOUtils.toString(reader);
		return readerAsString;
	}

	//data format conversions
	
	/**
	* @param xml as string
    * @return json as string 
	*/
	public static String convertXmlToJson(String xml) {

		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		String json = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);

		return json;
	}

	/**
	* @param xml as string
    * @return yaml as string 
	* @throws IOException
	*/
	public static String convertXmlToYaml(String xml) throws IOException {

		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		json = xmlJSONObj.toString();

		JsonNode jsonNode = new ObjectMapper().readTree(json);
		yaml = new YAMLMapper().writeValueAsString(jsonNode);

		return yaml;
	}

	/**
	* @param xml as string
    * @return csv as string 
	* @throws Exception
	*/
	public static String convertXmlToCsv(String xml) throws Exception {

		String[] xmlLines = xml.split(System.lineSeparator());
		String stylesheet = "src/main/resources/xmltocsv.generic.xsl"; 
		if(xmlLines.length > 2) {
			if(xmlLines[0].equals("<rows>")||xmlLines[1].equals("<rows>")) {
				stylesheet = "src/main/resources/xmltocsv.xsl"; 
			}
		}
		
		Document document = convertStringToDoc(xml);
	    StringWriter writer = new StringWriter();
	    
        StreamSource stylesource = new StreamSource(new File(stylesheet));
        Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
        Source source = new DOMSource(document);
        Result result = new javax.xml.transform.stream.StreamResult(writer);
        transformer.transform(source, result);
        
		csv = writer.toString();;

		return csv;
	}

	/**
	* @param json as string
    * @return xml as string 
	*/
	public static String convertJsonToXml(String json) {

		JSONObject jsonObj = new JSONObject(json);
		String xml = XML.toString(jsonObj);

		return xml;

	}

	/**
	* @param json as string
    * @return yaml as string 
	* @throws IOException
	*/
	public static String convertJsonToYaml(String json) throws IOException {

		JsonNode jsonNodeTree = new ObjectMapper().readTree(json);
		yaml = new YAMLMapper().writeValueAsString(jsonNodeTree);

		return yaml;
	}

	/**
	* @param json as string
    * @return csv as string 
	* @throws Exception 
	*/
	public static String convertJsonToCsv(String json) throws Exception{

		xml = convertJsonToXml(json);
		csv = convertXmlToCsv(xml);

		return csv;
	}

	/**
	* @param csv as string
    * @return xml as string 
	* @throws IOException
	*/
	@SuppressWarnings({"unchecked","resource","rawtypes"})
	public static String convertCsvToXml(String csv) throws IOException {

		Reader reader = convertStringToReader(csv);
		CSVReader csvReader = new CSVReader(reader);
        
        String[] line = null;

        String[] header = csvReader.readNext();

		List out = new ArrayList();

        while((line = csvReader.readNext())!=null){
            List<String[]> item = new ArrayList<String[]>();
                for (int i = 0; i < header.length; i++) {
                String[] keyVal = new String[2];
                String string = header[i];
                String val = line[i];
                keyVal[0] = string;
                keyVal[1] = val;
                item.add(keyVal);
            }
            out.add(item);
        }

        XStream xstream = new XStream();

        xml = xstream.toXML(out);
        	    
		return xml;
	}

	/**
	* @param csv as string
    * @return json as string 
	* @throws IOException
	*/
	public static String convertCsvToJson(String csv) throws IOException{

        xml = convertCsvToXml(csv);
        json = convertXmlToJson(xml);
        	    
		return json;
	}

	/**
	* @param csv as string
    * @return yaml as string 
	* @throws IOException
	*/
	public static String convertCsvToYaml(String csv) throws IOException{

        xml = convertCsvToXml(csv);
        json = convertXmlToYaml(xml);
        	    
		return json;
	}

	/**
	* @param yaml as string
    * @return xml as string 
	*/
	public static String convertYamlToXml(String yaml) throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
		Object obj = yamlReader.readValue(yaml, Object.class);

		ObjectMapper jsonWriter = new ObjectMapper();
		json = jsonWriter.writeValueAsString(obj);

		JSONObject jsonObj = new JSONObject(json);
		xml = XML.toString(jsonObj);

		return xml;
	}

	/**
	* @param yaml as string
    * @return json as string 
	*/	
	public static String convertYamlToJson(String yaml) throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper yamlReader = new ObjectMapper(new JsonFactory());
		Object obj = yamlReader.readValue(yaml, Object.class);

		ObjectMapper jsonWriter = new ObjectMapper();
		json = jsonWriter.writeValueAsString(obj);

		return json;
	}	

	/**
	* @param yaml as string
    * @return csv as string 
	*/
	public static String convertYamlToCsv(String yaml) throws Exception {

		xml = convertYamlToXml(yaml);
		csv = convertXmlToCsv(xml);

		return csv;
	}
	
}