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
    private String actionFileName;
    private Element root;
    private NodeList actions;

    public DocumentParser(String actionsFileString) throws IOException, SAXException, ParserConfigurationException {
        this.actionFileName = actionsFileString;
        this.p = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = p.parse("config" + File.separator + this.actionFileName);
        this.root = document.getDocumentElement();
        this.actions = root.getChildNodes();
    }
}
