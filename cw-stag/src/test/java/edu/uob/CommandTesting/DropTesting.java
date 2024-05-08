package edu.uob.CommandTesting;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DropTesting {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() throws Exception {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
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

    @Test
    void testDropCMDSimple(){
        String response = "mihir: get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("mihir: drop coin");
        assertTrue(response.toLowerCase().contains("dropped"), "Failed");
        response = sendCommandToServer("mihir: inv");
        assertFalse(response.toLowerCase().contains("coin"), "Failed");
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("coin"), "Failed");
    }

    @Test
    void testDropCMDEmpty(){
        String response = "mihir: get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("mihir: drop");
        assertTrue(response.toLowerCase().contains("error"));
        assertFalse(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: inv");
        assertTrue(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: look");
        assertFalse(response.toLowerCase().contains("coin"));
    }

    @Test
    void testDropCMDMultiple(){
        String response = "mihir: get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("mihir: drop coin, axe");
        assertTrue(response.toLowerCase().contains("error"));
        response = sendCommandToServer("mihir: inv");
        assertTrue(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: look");
        assertFalse(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: drop coin and axe");
        assertTrue(response.toLowerCase().contains("error"));
        response = sendCommandToServer("mihir: inv");
        assertTrue(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("look");
        assertFalse(response.toLowerCase().contains("coin"));
    }

    @Test
    void testDropCMDInvalid(){
        String response = "mihir: get coin";
        response = sendCommandToServer(response);
        response = sendCommandToServer("mihir: drop axe");
        assertTrue(response.toLowerCase().contains("error"));
        response = sendCommandToServer("mihir: inv");
        assertTrue(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: look");
        assertFalse(response.toLowerCase().contains("coin"), "Failed");
    }

    @Test
    void testDropCMDCaseInsensitive(){
        String response = "mihir: get coin";
        response = randomiseCasing(response);
        response = sendCommandToServer(response);
        response = sendCommandToServer("mihir: drop axe");
        response = randomiseCasing(response);
        response = sendCommandToServer(randomiseCasing("mihir: drop coin"));
        assertTrue(response.toLowerCase().contains("dropped"));
        response = sendCommandToServer("inv");
        assertFalse(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("coin"));
        response = "mihir: get coin";
        response = randomiseCasing(response);
        response = sendCommandToServer(response);
        response = "mihir: drop coin";
        response = randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("dropped"));
        response = sendCommandToServer("mihir: inv");
        assertFalse(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("coin"));
    }
}
