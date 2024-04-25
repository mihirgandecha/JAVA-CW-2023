package edu.uob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DocumentParser {

    public DocumentParser(String actionsFileString) throws GameError {
        setup(actionsFileString);
    }

    private void setup(String actionsFileString) throws GameError {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document parsedDocument = builder.parse(actionsFileString);
            parseDocument(parsedDocument);
        } catch (Exception e) {
            throw new GameError(e.getMessage());
        }
    }

    private void parseDocument(Document document) {
        Element root = document.getDocumentElement();
        NodeList actionNodes = root.getElementsByTagName("action");
        for (int i = 0; i < actionNodes.getLength(); i++) {
            Element actionElement = (Element) actionNodes.item(i);
            GameAction gameAction = parseGameAction(actionElement);
            addGameActionToMap(gameAction);
        }
    }

    private GameAction parseGameAction(Element actionElement) {
        GameAction gameAction = new GameAction();

        return gameAction;
    }

}
