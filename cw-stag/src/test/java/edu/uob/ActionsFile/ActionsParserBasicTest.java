package edu.uob.ActionsFile;

import edu.uob.DocumentParser;
import edu.uob.GameAction;
import edu.uob.GameError;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/*
    Low-Level Tests:
 */
class ActionsParserBasicTest {
    private HashMap<String, HashSet<GameAction>> gameActions;

    @BeforeEach
    void setUp() throws GameError {
        DocumentParser parser = new DocumentParser("config/basic-actions.xml");
        gameActions = parser.getGameActions();
    }

    // Check that the expected triggers are present
    @Test
    void testTriggersArePresent() {
        assertTrue(gameActions.containsKey("open"), "The 'open' trigger is missing");
        assertTrue(gameActions.containsKey("chop"), "The 'chop' trigger is missing");
        assertTrue(gameActions.containsKey("drink"), "The 'drink' trigger is missing");
        assertTrue(gameActions.containsKey("fight"), "The 'fight' trigger is missing");
    }

    @Test
    void testAttributesOfOpenAction() {
        GameAction openAction = gameActions.get("open").iterator().next();
        assertTrue(openAction.getSubjects().contains("trapdoor"), "The 'trapdoor' subject is missing for 'open'");
        assertTrue(openAction.getConsumed().contains("key"), "The 'key' consumed entity is missing for 'open'");
        assertTrue(openAction.getProduced().contains("cellar"), "The 'cellar' produced entity is missing for 'open'");
        assertEquals("You unlock the trapdoor and see steps leading down into a cellar", openAction.getNarration(), "Incorrect narration for 'open'");
    }

    @Test
    void testAttributesOfChopAction() {
        GameAction chopAction = gameActions.get("chop").iterator().next();
        assertTrue(chopAction.getSubjects().contains("tree"), "The 'tree' subject is missing for 'chop'");
        assertTrue(chopAction.getConsumed().contains("tree"), "The 'tree' consumed entity is missing for 'chop'");
        assertTrue(chopAction.getProduced().contains("log"), "The 'log' produced entity is missing for 'chop'");
        assertEquals("You cut down the tree with the axe", chopAction.getNarration(), "Incorrect narration for 'chop'");
    }

    @Test
    void testAttributesOfDrinkAction() {
        GameAction drinkAction = gameActions.get("drink").iterator().next();
        assertTrue(drinkAction.getSubjects().contains("potion"), "The 'potion' subject is missing for 'drink'");
        assertTrue(drinkAction.getConsumed().contains("potion"), "The 'potion' consumed entity is missing for 'drink'");
        assertTrue(drinkAction.getProduced().contains("health"), "The 'health' produced entity is missing for 'drink'");
        assertEquals("You drink the potion and your health improves", drinkAction.getNarration(), "Incorrect narration for 'drink'");
    }

    @Test
    void testAttributesOfFightAction() {
        GameAction fightAction = gameActions.get("fight").iterator().next();
        assertTrue(fightAction.getSubjects().contains("elf"), "The 'elf' subject is missing for 'fight'");
        assertTrue(fightAction.getConsumed().contains("health"), "The 'health' consumed entity is missing for 'fight'");
        assertEquals("You attack the elf, but he fights back and you lose some health", fightAction.getNarration(), "Incorrect narration for 'fight'");
    }
}