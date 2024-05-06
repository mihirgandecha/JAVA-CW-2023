package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ExampleSTAGTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() throws Exception {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(10000000), () -> {
                    return server.handleCommand(command);
                },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    String randomiseCasing(String inFromGenerateRandomName) {
        StringBuilder randomiseCaseForName = new StringBuilder();
        Random random = new Random();
        for (char c : inFromGenerateRandomName.toCharArray()) {
            if (random.nextBoolean()) {
                randomiseCaseForName.append(java.lang.Character.toUpperCase(c));
            } else {
                randomiseCaseForName.append(java.lang.Character.toLowerCase(c));
            }
        }
        return randomiseCaseForName.toString();
    }

//    @Test
//    void testSetupWithIncorrectFileName() {
//        assertThrows(GameError.class, () -> {
//            File entitiesFile = Paths.get("config" + File.separator + "nonExistingFile.dot").toAbsolutePath().toFile();
//            File actionsFile = Paths.get("config" + File.separator + "nonExistingFile.xml").toAbsolutePath().toFile();
//            server = new GameServer(entitiesFile, actionsFile);
//        });
//        assertDoesNotThrow(() -> {
//            File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
//            File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
//            server = new GameServer(entitiesFile, actionsFile);
//        });
//    }
//
//    @Test
//    void testSetupWithExtendedFiles() {
//        assertThrows(GameError.class, () -> {
//            File entitiesFile = Paths.get("config" + File.separator + "extended-entities.xml").toAbsolutePath().toFile();
//            File actionsFile = Paths.get("config" + File.separator + "extended-actions.dot").toAbsolutePath().toFile();
//            server = new GameServer(entitiesFile, actionsFile);
//        });
//        assertDoesNotThrow(() -> {
//            File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
//            File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
//            server = new GameServer(entitiesFile, actionsFile);
//        });
//    }

    @Test
    void testLook() {
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("forest"), "Did not see available paths in response to look");
    }

    @Test
    void testLookRandomiseCasing() {
        String response = sendCommandToServer("SimOn: lOok");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    }

    // Test that we can pick something up and that it appears in our inventory
    @Test
    void testGet() {
        String response;
        sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
    }

    // Test that we can goto a different location (we won't get very far if we can't move around the game !)
    @Test
    void testGoto() {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
    }

    @Test
    void testPlayerDeath() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: fight with elf");
        sendCommandToServer("simon: health");
        sendCommandToServer("simon: fight with elf");
        sendCommandToServer("simon: health");
        String response = sendCommandToServer("simon: fight with elf");
        response = response.toLowerCase();
        assertEquals("you died and lost all of your items, you must return to the start of the game\n", response);
        //TODO: Need to fix reset location
    }


    @Test
    void testExampleScript() {
        String response = sendCommandToServer("simon: inv");
        assertEquals("inventory is empty\n", response.toLowerCase());

        //Initial look
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(Arrays.asList("cabin", "potion", "axe", "trapdoor", "forest").stream().allMatch(response::contains));

        // Check inventory after picking axe
        response = sendCommandToServer("simon: inv");
        assertEquals("inventory is empty\n", response.toLowerCase());

        //Pickup Axe
        response = sendCommandToServer("simon: get axe");
        assertEquals("you picked up a axe\n", response.toLowerCase());

        // Check inventory after picking axe
        response = sendCommandToServer("simon: inv");
        assertTrue(response.toLowerCase().contains("axe"));

        //Look - check axe not in cabin location
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(Arrays.asList("cabin", "potion", "trapdoor", "forest").stream().allMatch(response::contains));

        //Pickup Potion
        response = sendCommandToServer("simon: get potion");
        assertEquals("you picked up a potion\n", response.toLowerCase());

        //Look - check potion not in cabin location
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(Arrays.asList("cabin", "trapdoor", "forest").stream().allMatch(response::contains));

        // Check inventory after picking potion with 'inventory'
        response = sendCommandToServer("simon: inv");
        assertTrue(response.toLowerCase().contains("potion"));

        //Goto - check player is moved
        response = sendCommandToServer("simon: goto forest");
        response = response.toLowerCase();
        assertTrue(Arrays.asList("forest", "key", "cabin").stream().allMatch(response::contains));

        response = sendCommandToServer("simon: chop tree");
        assertEquals("you cut down the tree with the axe\n", response.toLowerCase());

        //Pickup Key - check key not in forest location
        response = sendCommandToServer("simon: get key");
        assertEquals("you picked up a key\n", response.toLowerCase());

        response = sendCommandToServer("simon: inv");
        //Goto cabin now having key
        response = sendCommandToServer("simon: goto cabin");
        response = response.toLowerCase();
        assertTrue(Arrays.asList("cabin", "trapdoor", "forest").stream().allMatch(response::contains));

        //Check advanced Action: trapdoor can be opened as player holds key
        response = sendCommandToServer("simon: open trapdoor");
        assertEquals("you unlock the trapdoor and see steps leading down into a cellar\n", response.toLowerCase());

        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cellar"));

        //TODO need to remove trapdoor?
        response = sendCommandToServer("simon: goto cellar");
        response = response.toLowerCase();
        assertTrue(Arrays.asList("cellar", "elf", "cabin").stream().allMatch(response::contains));
    }

    @Test
    void testBasicGameCommands() {
        String response;
        // Initial Look in the starting location
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"), "Look should reveal the cabin description.");
        assertTrue(response.toLowerCase().contains("axe"), "Axe should be visible in cabin.");
        assertTrue(response.toLowerCase().contains("potion"), "Potion should be visible in cabin.");

        // Pickup Axe
        response = sendCommandToServer("simon: get axe");
        assertTrue(response.toLowerCase().contains("you picked up a axe"));

        // Verify inventory contains the Axe
        response = sendCommandToServer("simon: inventory");
        assertTrue(response.toLowerCase().contains("axe"));

        // Drop the Axe
        response = sendCommandToServer("simon: drop axe");
        assertTrue(response.toLowerCase().contains("you dropped a axe"));

        // Verify the Axe is no longer in inventory but is in the location
        response = sendCommandToServer("simon: inventory");
        assertFalse(response.toLowerCase().contains("axe"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("axe"));

        // Goto another location and verify transition
        response = sendCommandToServer("simon: goto forest");
        assertTrue(response.toLowerCase().contains("forest"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("tree"));
        assertTrue(response.toLowerCase().contains("key"));

        // Return to the cabin
        response = sendCommandToServer("simon: goto cabin");
        assertTrue(response.toLowerCase().contains("cabin"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"));
    }

    @Test
    void testInvalidCommand() {
//        String response1 = sendCommandToServer("simon: unlock");
//        assertTrue(response1.contains("null"));
        String response = sendCommandToServer("simon: quiet");
        response = response.toLowerCase();
        assertTrue(response.contains("unknown command"));
    }

    // Add more unit tests or integration tests here.
//    @Test
//    void testAddingBasicEntitiesToGameMap() throws Exception {
//        ArrayList<Location>gameMap = server.GameEngine.map;
//        assertTrue(gameMap.isEmpty());
//        gameMap.add(new Location("forest", "dark scary woodlands area"));
//        assertTrue(gameMap.size() == 1);
//        gameMap.add(new Location("cabin", "cosy area"));
//        assertTrue(gameMap.size() == 2);
//        gameMap.add(new Location("castle", "boogy area"));
//        assertTrue(gameMap.size() == 3);
//        assertEquals(3, server.getMapSize());
//        //Now get name,desc,type of different entities to locations (extra work?)
//    }
//
//    @Test
//    void testBasicAddNewPlayerToGameMap() throws Exception {
//        ArrayList<Location>gameMap = server.map;
//        gameMap.add(new Location("forest", "dark scary woodlands area"));
//        gameMap.add(new Location("cabin", "cosy area"));
//        Player playerOne = new Player("simon", gameMap.get(1));
//        assertEquals("cabin",playerOne.getPlayerCurrentLocation().getName());
//        playerOne = new Player("simon", gameMap.get(0));
//        assertEquals("forest",playerOne.getPlayerCurrentLocation().getName());
//    }


    @Test
    void testAdvancedActions() {
        String response;
        response = sendCommandToServer("mihir: inv");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: get axe");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: get potion");
        response = sendCommandToServer("mihir: look");
        //TODO: Response so get cannot be furniture
        response = sendCommandToServer("mihir: get trapdoor");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: goto forest");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: inv");
        response = sendCommandToServer("mihir: get key");
        response = sendCommandToServer("mihir: inv");
        response = sendCommandToServer("mihir: look");
        response = sendCommandToServer("mihir: chop axe");

    }

    @Test
    void testMultipleUsers() {
        String responseSimon = sendCommandToServer(randomiseCasing("simon: look"));
        String responseMihir = sendCommandToServer(randomiseCasing("Mihir: look"));
        assertTrue(responseSimon.toLowerCase().contains("cabin"), "Simon should be able to see his location.");
        assertTrue(responseMihir.toLowerCase().contains("forest"), "Mihir should be able to see his location.");
    }

    @Test
    void testMultiPlayerGameStateChange() {
        sendCommandToServer("Simon: pick up key");
        String response = sendCommandToServer("Mihir: inventory");
        assertFalse(response.toLowerCase().contains("key"), "Mihir should not have the key picked up by Simon.");
    }

    //Currently: 31 Failures/53 tests -> 22 Tests Passed/53 (Goto fixed bugs)
    //Objective by Tuesday (need 55-60% to not use extension): 35Passed/53 -> 13 additional tests
    //TODO: Read through docs carefullY! Pick out any features I've missed!
    //!TODO 1. Decorative command handling!!! [Guess 5 tests] {get the axe!}
    //TODO: 2. Write tests for Fully pass testing of basic files + Completing Game
        //TODO: Ensure Health functionality working by modifying Advanced Actions to use Super instead [Guess 5 tests]
        //TODO: Ensure Multiplayer functionality working in tests [Guess 5 tests]
        //TODO: (Q) Ask file handling logic correct
    //TODO: 2. Write tests for Fully pass testing of extended files + Completing Game


    //Advanced:
    //TODO: For any file given, integration test build so every situation is tested
    //TODO: Make my own entity/action files! (just one more but add more later)
    //TODO: Get someone else to play
    //TODO: Code cleanup - find out approx lines of code + try match
    //TODO: Gitignore definitely cleaned
    //TODO: Code Quality - following last feedback
    //TODO: 100% code coverage testing

}
