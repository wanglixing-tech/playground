package crs.fcl.integration.iib;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ZooMain {

    public static void main(String[] args) throws IOException, JAXBException {
        String zoo1Str = new String(Files.readAllBytes(new File("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo1.xml").toPath()));
        String zoo2Str = new String(Files.readAllBytes(new File("C:\\Users\\Richard.Wang\\WS-HOME\\ws-java-jaxb\\CompareXMLFiles\\zoo2.xml").toPath()));

        JAXBContext jaxbContext = JAXBContext.newInstance(Zoo.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        Zoo zoo1 = (Zoo) jaxbUnmarshaller.unmarshal(new StringReader(zoo1Str));

        Zoo zoo2 = (Zoo) jaxbUnmarshaller.unmarshal(new StringReader(zoo2Str));

        Zoo diff = zoo2.getDiff(zoo1);

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        StringWriter sw=new StringWriter();

        jaxbMarshaller.marshal(diff, sw);

        System.out.println(sw);

    }

}