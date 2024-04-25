package edu.uob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DocumentParser {
    private final javax.xml.parsers.DocumentBuilder p;
    private Element root;
    private NodeList actions;
    private Document parsedDocument;

    public DocumentParser(String actionsFileString) throws IOException, SAXException, ParserConfigurationException {
        this.p = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.parsedDocument = p.parse("config" + File.separator + actionsFileString);
        this.root = this.parsedDocument.getDocumentElement();
        this.actions = root.getChildNodes();
    }
}
