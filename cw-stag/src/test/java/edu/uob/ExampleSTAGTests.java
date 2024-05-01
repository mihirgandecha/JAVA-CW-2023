package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
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

    // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
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
  void testGet()
  {
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
  void testGoto()
  {
      sendCommandToServer("simon: goto forest");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

    @Test
    void testInvalidCommand()
    {
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




}
