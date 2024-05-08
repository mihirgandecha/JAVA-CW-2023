package edu.uob.CommandTesting;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetTesting {

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

    //TODO Testing Get with multiplayer

    //Get Test:
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

    @Test
    void emptyGetFails() {
        String response = "simon: get ";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        response = response.toLowerCase();
        assertTrue(response.contains("error"));
    }

    @Test
    void getWithWrongEntityFails() {
        String response = "simon: water ";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        response = response.toLowerCase();
        assertTrue(response.contains("error"));
    }

    @Test
    void getFailsFromDifferentLocation(){
        String response = "simon: get key";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        response = response.toLowerCase();
        assertTrue(response.contains("error"));
    }

    @Test
    void getFailsWithMultiplayer(){
        String response = "simon: get axe";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"));
        response = "mihir: get axe";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        response = response.toLowerCase();
        assertTrue(response.contains("error"));
        response = sendCommandToServer("mihir: inv");
        assertTrue(response.contains("empty"));
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"));
    }

    @Test
    void gotoWithMultipleEntitiesShouldFail(){
        String response = "simon: get axe and potion";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
        response = "get axe, potion";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
        response = randomiseCasing("simon: get and swing my big axe axe from cabin");
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
    }
}
