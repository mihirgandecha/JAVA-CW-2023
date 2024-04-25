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

import static org.junit.jupiter.api.Assertions.*;

final class ActionsFileTests {

//    private DocumentParser p;

//    @BeforeEach
//    void setup() throws GameError {
//        p = new DocumentParser("config" + File.separator + "basic-actions.xml");
//    }

//     Test to make sure that the basic actions file is readable
  @Test
  void testBasicActionsFileIsReadable() {
      try {
          DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          Document document = builder.parse("config" + File.separator + "basic-actions.xml");
          Element root = document.getDocumentElement();
          NodeList actions = root.getChildNodes();
          // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
          Element firstAction = (Element)actions.item(1);
          Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
          // Get the first trigger phrase
          String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
          assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
      } catch(ParserConfigurationException pce) {
          fail("ParserConfigurationException was thrown when attempting to read basic actions file");
      } catch(SAXException saxe) {
          fail("SAXException was thrown when attempting to read basic actions file");
      } catch(IOException ioe) {
          fail("IOException was thrown when attempting to read basic actions file");
      }
  }

    /*
    Low-Level Unit Testing: Testing for Coverage
     */

  /*
  Medium-Level Unit Testing: Testing for State Change
   */
  @Test
  void testParseExtendedActions() throws GameError {
      DocumentParser parser = new DocumentParser("config" + File.separator + "extended-actions.xml");
      HashMap<String, HashSet<GameAction>> gameActions = parser.getGameActions();
      // Validate that the expected triggers are present
      assertTrue(gameActions.containsKey("open"), "The 'open' trigger is missing");
      assertTrue(gameActions.containsKey("chop"), "The 'chop' trigger is missing");
      assertTrue(gameActions.containsKey("drink"), "The 'drink' trigger is missing");
      assertTrue(gameActions.containsKey("fight"), "The 'fight' trigger is missing");
      assertTrue(gameActions.containsKey("pay"), "The 'pay' trigger is missing");
      assertTrue(gameActions.containsKey("bridge"), "The 'bridge' trigger is missing");
      assertTrue(gameActions.containsKey("dig"), "The 'dig' trigger is missing");
      assertTrue(gameActions.containsKey("blow"), "The 'blow' trigger is missing");
      // Validate specific game actions
      validateOpenAction(gameActions.get("open").iterator().next());
      validateChopAction(gameActions.get("chop").iterator().next());
      validateDrinkAction(gameActions.get("drink").iterator().next());
      validateFightAction(gameActions.get("fight").iterator().next());
      validatePayAction(gameActions.get("pay").iterator().next());
      validateBridgeAction(gameActions.get("bridge").iterator().next());
      validateDigAction(gameActions.get("dig").iterator().next());
      validateBlowAction(gameActions.get("blow").iterator().next());
  }

    private void validateOpenAction(GameAction action) {
        assertTrue(action.getSubjects().contains("trapdoor"), "Missing 'trapdoor' in 'open'");
        assertTrue(action.getConsumed().contains("key"), "Missing 'key' in 'open'");
        assertTrue(action.getProduced().contains("cellar"), "Missing 'cellar' in 'open'");
        assertEquals("You unlock the door and see steps leading down into a cellar", action.getNarration(), "Incorrect narration for 'open'");
    }

    private void validateChopAction(GameAction action) {
        assertTrue(action.getSubjects().contains("tree"), "Missing 'tree' in 'chop'");
        assertTrue(action.getConsumed().contains("tree"), "Missing 'tree' in 'chop'");
        assertTrue(action.getProduced().contains("log"), "Missing 'log' in 'chop'");
        assertEquals("You cut down the tree with the axe", action.getNarration(), "Incorrect narration for 'chop'");
    }

    private void validateDrinkAction(GameAction action) {
        assertTrue(action.getSubjects().contains("potion"), "Missing 'potion' in 'drink'");
        assertTrue(action.getConsumed().contains("potion"), "Missing 'potion' in 'drink'");
        assertTrue(action.getProduced().contains("health"), "Missing 'health' in 'drink'");
        assertEquals("You drink the potion and your health improves", action.getNarration(), "Incorrect narration for 'drink'");
    }

    private void validateFightAction(GameAction action) {
        assertTrue(action.getSubjects().contains("elf"), "Missing 'elf' in 'fight'");
        assertTrue(action.getConsumed().contains("health"), "Missing 'health' in 'fight'");
        assertEquals("You attack the elf, but he fights back and you lose some health", action.getNarration(), "Incorrect narration for 'fight'");
    }

    private void validatePayAction(GameAction action) {
        assertTrue(action.getSubjects().contains("elf"), "Missing 'elf' in 'pay'");
        assertTrue(action.getSubjects().contains("coin"), "Missing 'coin' in 'pay'");
        assertTrue(action.getConsumed().contains("coin"), "Missing 'coin' in 'pay'");
        assertTrue(action.getProduced().contains("shovel"), "Missing 'shovel' in 'pay'");
        assertEquals("You pay the elf your silver coin and he produces a shovel", action.getNarration(), "Incorrect narration for 'pay'");
    }

    private void validateBridgeAction(GameAction action) {
        assertTrue(action.getSubjects().contains("log"), "Missing 'log' in 'bridge'");
        assertTrue(action.getSubjects().contains("river"), "Missing 'river' in 'bridge'");
        assertTrue(action.getConsumed().contains("log"), "Missing 'log' in 'bridge'");
        assertTrue(action.getProduced().contains("clearing"), "Missing 'clearing' in 'bridge'");
        assertEquals("You bridge the river with the log and can now reach the other side", action.getNarration(), "Incorrect narration for 'bridge'");
    }

    private void validateDigAction(GameAction action) {
        assertTrue(action.getSubjects().contains("ground"), "Missing 'ground' in 'dig'");
        assertTrue(action.getSubjects().contains("shovel"), "Missing 'shovel' in 'dig'");
        assertTrue(action.getConsumed().contains("ground"), "Missing 'ground' in 'dig'");
        assertTrue(action.getProduced().contains("hole"), "Missing 'hole' in 'dig'");
        assertTrue(action.getProduced().contains("gold"), "Missing 'gold' in 'dig'");
        assertEquals("You dig into the soft ground and unearth a pot of gold !!!", action.getNarration(), "Incorrect narration for 'dig'");
    }

    private void validateBlowAction(GameAction action) {
        assertTrue(action.getSubjects().contains("horn"), "Missing 'horn' in 'blow'");
        assertTrue(action.getProduced().contains("lumberjack"), "Missing 'lumberjack' in 'blow'");
        assertEquals("You blow the horn and as if by magic, a lumberjack appears !", action.getNarration(), "Incorrect narration for 'blow'");
    }
  /*
  High-Level Unit Testing: Integration Testing
   */
}
