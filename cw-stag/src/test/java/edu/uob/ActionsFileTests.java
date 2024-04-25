package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

import static org.junit.jupiter.api.Assertions.*;

final class ActionsFileTests {

//    private DocumentParser p;

//    @BeforeEach
//    void setup() throws GameError {
//        p = new DocumentParser("config" + File.separator + "basic-actions.xml");
//    }
    // Test to make sure that the basic actions file is readable
//  @Test
//  void testBasicActionsFileIsReadable() {
//      try {
//          DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//          Document document = builder.parse("config" + File.separator + "basic-actions.xml");
//          Element root = document.getDocumentElement();
//          NodeList actions = root.getChildNodes();
//          // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
//          Element firstAction = (Element)actions.item(1);
//          Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
//          // Get the first trigger phrase
//          String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
//          assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
//      } catch(ParserConfigurationException pce) {
//          fail("ParserConfigurationException was thrown when attempting to read basic actions file");
//      } catch(SAXException saxe) {
//          fail("SAXException was thrown when attempting to read basic actions file");
//      } catch(IOException ioe) {
//          fail("IOException was thrown when attempting to read basic actions file");
//      }
//  }

    /*
    Low-Level Unit Testing: Testing for Coverage
     */
//    @Test
//    void basicActionFileTestIsCorrectlySetup() throws GameError, ParserConfigurationException, IOException, SAXException {
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        Document parsedDocument = builder.parse("config" + File.separator + "basic-actions.xml");
//        p = new DocumentParser("config" + File.separator + "basic-actions.xml");
//        p.parseDocument(parsedDocument);
//    }

    @Test
    void testParseBasicActions() throws GameError {
        DocumentParser p = new DocumentParser("config" + File.separator + "basic-actions.xml");

        // Check that the expected triggers are present
        assertTrue(p.gameActions.containsKey("open"), "The 'open' trigger is missing");
        assertTrue(p.gameActions.containsKey("chop"), "The 'chop' trigger is missing");
        assertTrue(p.gameActions.containsKey("drink"), "The 'drink' trigger is missing");
        assertTrue(p.gameActions.containsKey("fight"), "The 'fight' trigger is missing");

        // Validate the 'open' action
        GameAction openAction = p.gameActions.get("open").iterator().next();
        assertTrue(openAction.getSubjects().contains("trapdoor"), "The 'trapdoor' subject is missing for 'open'");
        assertTrue(openAction.getConsumed().contains("key"), "The 'key' consumed entity is missing for 'open'");
        assertTrue(openAction.getProduced().contains("cellar"), "The 'cellar' produced entity is missing for 'open'");
        assertEquals("You unlock the trapdoor and see steps leading down into a cellar", openAction.getNarration(), "Incorrect narration for 'open'");

        // Validate the 'chop' action
        GameAction chopAction = p.gameActions.get("chop").iterator().next();
        assertTrue(chopAction.getSubjects().contains("tree"), "The 'tree' subject is missing for 'chop'");
        assertTrue(chopAction.getConsumed().contains("tree"), "The 'tree' consumed entity is missing for 'chop'");
        assertTrue(chopAction.getProduced().contains("log"), "The 'log' produced entity is missing for 'chop'");
        assertEquals("You cut down the tree with the axe", chopAction.getNarration(), "Incorrect narration for 'chop'");

        // Validate the 'drink' action
        GameAction drinkAction = p.gameActions.get("drink").iterator().next();
        assertTrue(drinkAction.getSubjects().contains("potion"), "The 'potion' subject is missing for 'drink'");
        assertTrue(drinkAction.getConsumed().contains("potion"), "The 'potion' consumed entity is missing for 'drink'");
        assertTrue(drinkAction.getProduced().contains("health"), "The 'health' produced entity is missing for 'drink'");
        assertEquals("You drink the potion and your health improves", drinkAction.getNarration(), "Incorrect narration for 'drink'");

        // Validate the 'fight' action
        GameAction fightAction = p.gameActions.get("fight").iterator().next();
        assertTrue(fightAction.getSubjects().contains("elf"), "The 'elf' subject is missing for 'fight'");
        assertTrue(fightAction.getConsumed().contains("health"), "The 'health' consumed entity is missing for 'fight'");
        assertEquals("You attack the elf, but he fights back and you lose some health", fightAction.getNarration(), "Incorrect narration for 'fight'");
    }

  /*
  Medium-Level Unit Testing: Testing for State Change
   */

  /*
  High-Level Unit Testing: Integration Testing
   */
}
