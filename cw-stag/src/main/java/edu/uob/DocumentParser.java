package edu.uob;
import edu.uob.Command.AdvancedAction;
import edu.uob.Command.GameCommand;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentParser extends GameCommand {
    private AdvancedAction actions;
    public HashMap<String, HashSet<AdvancedAction>> gameActions;

    public DocumentParser(GameEngine gameEngine, Player player, String basicCommand) throws GameError {
        super(gameEngine, player, basicCommand);
        gameActions = new HashMap<>();
        setup(basicCommand);
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

    void parseDocument(Document document) {
        Element root = document.getDocumentElement();
        NodeList actionNodes = root.getElementsByTagName("action");
        for (int i = 0; i < actionNodes.getLength(); i++) {
            Element actionElement = (Element) actionNodes.item(i);
            AdvancedAction advancedAction = parseGameAction(actionElement);
            storeGameAction(advancedAction);
        }
    }

    private AdvancedAction parseGameAction(Element actionElement) {
        actions = new AdvancedAction(getEngine(), getPlayer(), getBasicCommand());
        actions.setTriggers(new ArrayList<>(parseHashSet(actionElement, "triggers", "keyphrase")));
        actions.setSubjects(new ArrayList<>(parseHashSet(actionElement, "subjects", "entity")));
        actions.setConsumed(new ArrayList<>(parseOptionalHashSet(actionElement, "consumed")));
        actions.setProduced(new ArrayList<>(parseOptionalHashSet(actionElement, "produced")));
        actions.setNarration(parseTextContent(actionElement));
        return actions;
    }

    private HashSet<String> parseHashSet(Element parentElement, String tag, String childTag) {
        HashSet<String> set = new HashSet<>();
        NodeList parentNodes = parentElement.getElementsByTagName(tag);
        if (parentNodes.getLength() > 0) {
            Element parent = (Element) parentNodes.item(0);
            NodeList childNodes = parent.getElementsByTagName(childTag);
            for (int j = 0; j < childNodes.getLength(); j++) {
                set.add(childNodes.item(j).getTextContent().toLowerCase());
            }
        }
        return set;
    }

    private HashSet<String> parseOptionalHashSet(Element parentElement, String tag) {
        HashSet<String> set = parseHashSet(parentElement, tag, "entity");
        return set.isEmpty() ? new HashSet<>() : set;
    }

    private String parseTextContent(Element parentElement) {
        NodeList textNodes = parentElement.getElementsByTagName("narration");
        if (textNodes.getLength() > 0) {
            return textNodes.item(0).getTextContent();
        }
        return "";
    }

    private void storeGameAction(AdvancedAction advancedAction) {
        for (String trigger : advancedAction.getTriggers()) {
            gameActions.computeIfAbsent(trigger, k -> new HashSet<>()).add(advancedAction);
        }
    }

    public AdvancedAction getActionsState() {
        return actions;
    }

    public HashMap<String, HashSet<AdvancedAction>> getGameActions() {
        return gameActions;
    }
}
