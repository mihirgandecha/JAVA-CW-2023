package edu.uob.CommandTesting;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class HealthTesting {

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
    void healthCommandExtraneous(){
        String response = sendCommandToServer("mihir: look health");
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void healthCommandExtraneous2(){
        String response = sendCommandToServer("mihir: potion health");
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void healthAndMultiplayerDeathPasses() {
        String response;
        response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: get potion");
        response = sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("cabin"));
        assertTrue(response.toLowerCase().contains("cellar"));
        assertFalse(response.toLowerCase().contains("potion"));
        assertFalse(response.toLowerCase().contains("simon"));
        sendCommandToServer("simon: fight with elf");
        response = sendCommandToServer("simon: health");
        sendCommandToServer("simon: fight with elf");
        sendCommandToServer("simon: health");
        response = sendCommandToServer("simon: fight with elf");
        response = response.toLowerCase();
        assertEquals("you died and lost all of your items, you must return to the start of the game\n", response);
        response = sendCommandToServer("simon: health");
        assertEquals("player health 3", response.toLowerCase());
        assertTrue(response.contains("3"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cellar"));
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.toLowerCase().contains("potion"));
        response = sendCommandToServer("simon: health");
        assertEquals("player health 3", response.toLowerCase());
        response = sendCommandToServer("simon: fight elf");
        assertTrue(response.toLowerCase().contains("attack the elf"));
        response = sendCommandToServer("simon: health");
        assertTrue(response.toLowerCase().contains("2"));
        //Test player 2 is accurately reset:
        response = sendCommandToServer("mihir: goto cellar");
        assertTrue(response.toLowerCase().contains("simon"));
        response = sendCommandToServer("mihir: get potion");
        response = sendCommandToServer("mihir: health");
        assertTrue(response.toLowerCase().contains("3"));
        response = sendCommandToServer("mihir: hit elf");
        response = sendCommandToServer("mihir: health");
        assertTrue(response.toLowerCase().contains("2"));
        response = sendCommandToServer("mihir: fight the elf");
        response = sendCommandToServer("mihir: health");
        assertTrue(response.toLowerCase().contains("1"));
        response = sendCommandToServer("mihir: break his back and hit elf");
        response = sendCommandToServer("mihir: health");
        assertTrue(response.toLowerCase().contains("3"));
        response = sendCommandToServer("mihir: look");
        assertTrue(response.toLowerCase().contains("cabin"));
        assertTrue(response.toLowerCase().contains("cellar"));
        assertFalse(response.toLowerCase().contains("simon"));
        response = sendCommandToServer("mihir: inv");
        assertTrue(response.toLowerCase().contains("empty"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("potion"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("potion"));
        //Test drinking the potion wont increase health if on Full health:
        response = sendCommandToServer("mihir: goto cellar");
        assertTrue(response.toLowerCase().contains("potion"));
        response = sendCommandToServer("mihir: health");
        assertTrue(response.toLowerCase().contains("3"));
        response = sendCommandToServer("mihir: drink the potion");
        response = sendCommandToServer("mihir: look");
        assertFalse(response.toLowerCase().contains("potion"));
        response = sendCommandToServer("simon: look");
        assertFalse(response.toLowerCase().contains("potion"));
        response = sendCommandToServer("simon: drink potion");
        assertTrue(response.toLowerCase().contains("error"));
        response = sendCommandToServer("mihir: health");
        assertTrue(response.toLowerCase().contains("3"));
    }
}
