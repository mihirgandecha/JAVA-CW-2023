package edu.uob.ActionsFile;

import edu.uob.DocumentParser;
import edu.uob.GameAction;
import edu.uob.GameError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionsParserExtendedTest {
    private DocumentParser parser;
    private HashMap<String, HashSet<GameAction>> gameActions;

    @BeforeEach
    void setUp() throws GameError {
        parser = new DocumentParser("config/extended-actions.xml");
        gameActions = parser.getGameActions();
    }

    @Test
    void testExtendedTriggers() {
        assertTrue(gameActions.containsKey("open"), "The 'open' trigger is missing");
        assertTrue(gameActions.containsKey("chop"), "The 'chop' trigger is missing");
        assertTrue(gameActions.containsKey("drink"), "The 'drink' trigger is missing");
        assertTrue(gameActions.containsKey("fight"), "The 'fight' trigger is missing");
        assertTrue(gameActions.containsKey("pay"), "The 'pay' trigger is missing");
        assertTrue(gameActions.containsKey("bridge"), "The 'bridge' trigger is missing");
        assertTrue(gameActions.containsKey("dig"), "The 'dig' trigger is missing");
        assertTrue(gameActions.containsKey("blow"), "The 'blow' trigger is missing");
    }

    @Test
    void testAttributesOfOpenAction() {
        GameAction openAction = gameActions.get("open").iterator().next();
        assertTrue(openAction.getSubjects().contains("trapdoor"), "Missing 'trapdoor' in 'open'");
        assertTrue(openAction.getConsumed().contains("key"), "Missing 'key' in 'open'");
        assertTrue(openAction.getProduced().contains("cellar"), "Missing 'cellar' in 'open'");
        assertEquals("You unlock the door and see steps leading down into a cellar", openAction.getNarration(), "Incorrect narration for 'open'");
    }

    @Test
    void testAttributesOfChopAction() {
        GameAction chopAction = gameActions.get("chop").iterator().next();
        assertTrue(chopAction.getSubjects().contains("tree"), "Missing 'tree' in 'chop'");
        assertTrue(chopAction.getConsumed().contains("tree"), "Missing 'tree' in 'chop'");
        assertTrue(chopAction.getProduced().contains("log"), "Missing 'log' in 'chop'");
        assertEquals("You cut down the tree with the axe", chopAction.getNarration(), "Incorrect narration for 'chop'");
    }

    @Test
    void testAttributesOfDrinkAction() {
        GameAction drinkAction = gameActions.get("drink").iterator().next();
        assertTrue(drinkAction.getSubjects().contains("potion"), "Missing 'potion' in 'drink'");
        assertTrue(drinkAction.getConsumed().contains("potion"), "Missing 'potion' in 'drink'");
        assertTrue(drinkAction.getProduced().contains("health"), "Missing 'health' in 'drink'");
        assertEquals("You drink the potion and your health improves", drinkAction.getNarration(), "Incorrect narration for 'drink'");
    }

    @Test
    void testAttributesOfFightAction() {
        GameAction fightAction = gameActions.get("fight").iterator().next();
        assertTrue(fightAction.getSubjects().contains("elf"), "Missing 'elf' in 'fight'");
        assertTrue(fightAction.getConsumed().contains("health"), "Missing 'health' in 'fight'");
        assertEquals("You attack the elf, but he fights back and you lose some health", fightAction.getNarration(), "Incorrect narration for 'fight'");
    }

    @Test
    void testAttributesOfPayAction() {
        GameAction payAction = gameActions.get("pay").iterator().next();
        assertTrue(payAction.getSubjects().contains("elf"), "Missing 'elf' in 'pay'");
        assertTrue(payAction.getConsumed().contains("coin"), "Missing 'coin' in 'pay'");
        assertTrue(payAction.getProduced().contains("shovel"), "Missing 'shovel' in 'pay'");
        assertEquals("You pay the elf your silver coin and he produces a shovel", payAction.getNarration(), "Incorrect narration for 'pay'");
    }

    @Test
    void testAttributesOfBridgeAction() {
        GameAction bridgeAction = gameActions.get("bridge").iterator().next();
        assertTrue(bridgeAction.getSubjects().contains("log"), "Missing 'log' in 'bridge'");
        assertTrue(bridgeAction.getSubjects().contains("river"), "Missing 'river' in 'bridge'");
        assertTrue(bridgeAction.getConsumed().contains("log"), "Missing 'log' in 'bridge'");
        assertTrue(bridgeAction.getProduced().contains("clearing"), "Missing 'clearing' in 'bridge'");
        assertEquals("You bridge the river with the log and can now reach the other side", bridgeAction.getNarration(), "Incorrect narration for 'bridge'");
    }

    @Test
    void testAttributesOfDigAction() {
        GameAction digAction = gameActions.get("dig").iterator().next();
        assertTrue(digAction.getSubjects().contains("ground"), "Missing 'ground' in 'dig'");
        assertTrue(digAction.getSubjects().contains("shovel"), "Missing 'shovel' in 'dig'");
        assertTrue(digAction.getConsumed().contains("ground"), "Missing 'ground' in 'dig'");
        assertTrue(digAction.getProduced().contains("hole"), "Missing 'hole' in 'dig'");
        assertTrue(digAction.getProduced().contains("gold"), "Missing 'gold' in 'dig'");
        assertEquals("You dig into the soft ground and unearth a pot of gold !!!", digAction.getNarration(), "Incorrect narration for 'dig'");
    }

    @Test
    void testAttributesOfBlowAction() {
        GameAction blowAction = gameActions.get("blow").iterator().next();
        assertTrue(blowAction.getSubjects().contains("horn"), "Missing 'horn' in 'blow'");
        assertTrue(blowAction.getProduced().contains("lumberjack"), "Missing 'lumberjack' in 'blow'");
        assertEquals("You blow the horn and as if by magic, a lumberjack appears !", blowAction.getNarration(), "Incorrect narration for 'blow'");
    }
}