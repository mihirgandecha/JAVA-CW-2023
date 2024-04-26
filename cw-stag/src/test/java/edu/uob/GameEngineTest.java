package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    @Test
    void testBasicSetup() throws Exception {
        String ent = "basic-entities.dot";
        String act = "config" + File.separator + "basic-actions.xml";
//        GameEngine gameEngine = new GameEngine(ent, act, players);
//        assertEquals(4, gameEngine.map.size());
//        assertEquals(9, gameEngine.gameActions.size());
    }

}