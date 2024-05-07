package edu.uob.ActionsFile;

import edu.uob.DocumentParser;
import edu.uob.Command.AdvancedAction;
import edu.uob.GameError;
import edu.uob.GameServer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/*
    Low-Level Tests:
 */
class ActionsParserBasicTest {
    private HashMap<String, HashSet<AdvancedAction>> gameActions;

    @BeforeEach
    void setUp() throws GameError {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.handleCommand("simon: look");
        gameActions = server.getGameEngine().getActions();
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
        AdvancedAction openAction = gameActions.get("open").iterator().next();
        assertTrue(openAction.getSubjects().contains("trapdoor"), "The 'trapdoor' subject is missing for 'open'");
        assertTrue(openAction.getConsumed().contains("key"), "The 'key' consumed entity is missing for 'open'");
        assertTrue(openAction.getProduced().contains("cellar"), "The 'cellar' produced entity is missing for 'open'");
        assertEquals("You unlock the trapdoor and see steps leading down into a cellar", openAction.getNarration(), "Incorrect narration for 'open'");
    }

    @Test
    void testAttributesOfChopAction() {
        AdvancedAction chopAction = gameActions.get("chop").iterator().next();
        assertTrue(chopAction.getSubjects().contains("tree"), "The 'tree' subject is missing for 'chop'");
        assertTrue(chopAction.getConsumed().contains("tree"), "The 'tree' consumed entity is missing for 'chop'");
        assertTrue(chopAction.getProduced().contains("log"), "The 'log' produced entity is missing for 'chop'");
        assertEquals("You cut down the tree with the axe", chopAction.getNarration(), "Incorrect narration for 'chop'");
    }

    @Test
    void testAttributesOfDrinkAction() {
        AdvancedAction drinkAction = gameActions.get("drink").iterator().next();
        assertTrue(drinkAction.getSubjects().contains("potion"), "The 'potion' subject is missing for 'drink'");
        assertTrue(drinkAction.getConsumed().contains("potion"), "The 'potion' consumed entity is missing for 'drink'");
        assertTrue(drinkAction.getProduced().contains("health"), "The 'health' produced entity is missing for 'drink'");
        assertEquals("You drink the potion and your health improves", drinkAction.getNarration(), "Incorrect narration for 'drink'");
    }

    @Test
    void testAttributesOfFightAction() {
        AdvancedAction fightAction = gameActions.get("fight").iterator().next();
        assertTrue(fightAction.getSubjects().contains("elf"), "The 'elf' subject is missing for 'fight'");
        assertTrue(fightAction.getConsumed().contains("health"), "The 'health' consumed entity is missing for 'fight'");
        assertEquals("You attack the elf, but he fights back and you lose some health", fightAction.getNarration(), "Incorrect narration for 'fight'");
    }
}