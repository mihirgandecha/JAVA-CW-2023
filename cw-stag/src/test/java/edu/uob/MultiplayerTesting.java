package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MultiplayerTesting {

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

    @Test
    void multiplePlayersGreaterThanTwoWorkingForLook(){
        String response;
        response = sendCommandToServer("simon: look");
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("simon"));
        assertFalse(response.toLowerCase().contains("mihir"));
        response = sendCommandToServer("sion: look");
        assertFalse(response.toLowerCase().contains("sion"));
        assertTrue(response.toLowerCase().contains("simon"));
        assertTrue(response.toLowerCase().contains("mihir"));
        response = sendCommandToServer("neil: look");
        assertFalse(response.toLowerCase().contains("neil"));
        assertTrue(response.toLowerCase().contains("simon"));
        assertTrue(response.toLowerCase().contains("mihir"));
        assertTrue(response.toLowerCase().contains("sion"));
    }

    @Test
    void multiplePlayersGreaterThanTwoWorkingForGoto(){
        String response;
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("mihir: look");
        assertFalse(response.toLowerCase().contains("simon"));
        assertFalse(response.toLowerCase().contains("mihir"));
        response = sendCommandToServer("mihir: goto forest");
        assertTrue(response.toLowerCase().contains("simon"));
        assertFalse(response.toLowerCase().contains("mihir"));
        response = sendCommandToServer("sion: goto forest");
        assertTrue(response.toLowerCase().contains("simon"));
        assertTrue(response.toLowerCase().contains("mihir"));
        assertFalse(response.toLowerCase().contains("sion"));
        response = sendCommandToServer("neil: goto forest");
        assertFalse(response.toLowerCase().contains("neil"));
        assertTrue(response.toLowerCase().contains("simon"));
        assertTrue(response.toLowerCase().contains("mihir"));
        assertTrue(response.toLowerCase().contains("sion"));
    }
}
