package com.pentaho.install.post;

import com.pentaho.install.InstallUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLGenerator {
    protected boolean createXml(PentahoXMLConfig conf, String file) {
        boolean succeeded = false;

        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            StringWriter stringWriter = new StringWriter();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stringWriter);

            JAXBContext context = JAXBContext.newInstance(conf.getClass());
            Marshaller marshaller = context.createMarshaller();
            //marshaller.setListener(new TomcatMarshallerListener(writer));
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(conf, writer);
            writer.close();

            String xmlString = stringWriter.toString();
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

            //addComment(doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = null;
            if ("true".equals(System.getProperty("local"))) {
                result = new StreamResult(System.out);
            } else {
                System.out.print("\nUpdating file " + file);
                result = new StreamResult(new File(file));
            }

            transformer.transform(source, result);
            succeeded = true;
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }
        return succeeded;
    }
}
