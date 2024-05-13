package edu.uob.CommandTesting;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTesting {

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
    void basicInventoryTest() {
        String response = "mihir: inv";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("empty"));
        response = "mihir: inventory";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("empty"));
    }

    @Test
    void inventoryCommandWithWrongSpelling() {
        String response = "mihir: in";
        response = sendCommandToServer(response);
        assertFalse(response.toLowerCase().contains("empty"));
        response = "mihir: invent0ry";
        response = sendCommandToServer(response);
        assertFalse(response.toLowerCase().contains("empty"));
    }

    @Test
    void inventoryCommandWithGetAndLook() {
        String response = "mihir: get axe";
        sendCommandToServer(response);
        sendCommandToServer("mihir: get coin");
        response = sendCommandToServer("mihir: inv");
        assertTrue(response.toLowerCase().contains("axe"));
        assertTrue(response.toLowerCase().contains("coin"));
        response = sendCommandToServer("mihir: look").toLowerCase();
        assertFalse(response.toLowerCase().contains("axe"));
        assertFalse(response.toLowerCase().contains("coin"));
    }

    @Test
    void inventoryCommandWithDropExtended() {
        String response = "mihir: get axe";
        sendCommandToServer(response);
        sendCommandToServer("mihir: get coin");
        response = sendCommandToServer("mihir: get coin");
        assertTrue(response.toLowerCase().contains("not found"));
        sendCommandToServer("mihir: drop coin");
        response = sendCommandToServer("mihir: inv");
        assertFalse(response.toLowerCase().contains("coin"));
        assertTrue(response.toLowerCase().contains("axe"));
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("coin"));
        assertFalse(response.toLowerCase().contains("axe"));
        sendCommandToServer("mihir: drop axe");
        response = sendCommandToServer("mihir: inv");
        assertFalse(response.toLowerCase().contains("coin"));
        assertFalse(response.toLowerCase().contains("axe"));
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("coin"));
        assertTrue(response.toLowerCase().contains("axe"));
    }

    @Test
    void inventoryCommandWithDecorativePhrases() {
        String response = "mihir: get axe";
        sendCommandToServer(response);
        sendCommandToServer("mihir: get coin");
        sendCommandToServer("mihir: drop coin");
        sendCommandToServer("mihir: drop axe");
        response = sendCommandToServer("mihir: inv");
        assertFalse(response.toLowerCase().contains("coin"));
        assertFalse(response.toLowerCase().contains("axe"));
        response = "mihir: get axe";
        sendCommandToServer(response);
        response = sendCommandToServer("mihir: show me the inv");
        assertTrue(response.toLowerCase().contains("axe"));
        assertFalse(response.toLowerCase().contains("coin"));
        sendCommandToServer("mihir: drop axe");
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("coin"));
        assertTrue(response.toLowerCase().contains("axe"));
    }

    @Test
    void inventoryCommandWithExtendedDecorativePhrases() {
        String response = "mihir: get axe";
        sendCommandToServer(response);
        sendCommandToServer("mihir: get coin");
        sendCommandToServer("mihir: drop coin");
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("coin"));
        assertFalse(response.toLowerCase().contains("axe"));
        response = sendCommandToServer("mihir: open inventory for cabin");
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void inventoryCommandExtraneous(){
        String response = sendCommandToServer("mihir: get axe");
        response = sendCommandToServer("mihir: inv inventory");
        assertTrue(response.toLowerCase().contains("error"));
    }
}
