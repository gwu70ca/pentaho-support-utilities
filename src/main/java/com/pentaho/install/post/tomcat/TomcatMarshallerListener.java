package com.pentaho.install.post.tomcat;

import com.pentaho.install.InstallUtil;
import com.pentaho.install.post.tomcat.conf.server.Server;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class TomcatMarshallerListener extends Marshaller.Listener {
    private XMLStreamWriter writer;

    public  TomcatMarshallerListener(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void beforeMarshal(Object source)  {
        try {
            String comment = null;
            String className = source.getClass().getSimpleName();
            System.out.println("class: " + className);
            if ("Server".equals(className)) {
                comment = "Before Server element";
            }
            if (comment != null) {
                writer.writeComment(comment);
            }
        } catch(XMLStreamException e) {
            InstallUtil.error(e.getMessage());
        }
    }

    public void afterMarshal(Object source) {
        try {
            writer.writeComment("After:  " + source.toString());
        } catch(XMLStreamException e) {
            InstallUtil.error(e.getMessage());
        }
    }

    /*
    private static void addComment(Document doc) {
        Element e = doc.getDocumentElement();
        String tagName = e.getTagName();
        System.out.println("tag: " + tagName);

        if ("Server".equals(tagName)) {
            Comment comment = doc.createComment("Comment before Server\n");
            e.getParentNode().insertBefore(comment, e);
        }

    }
    */
}