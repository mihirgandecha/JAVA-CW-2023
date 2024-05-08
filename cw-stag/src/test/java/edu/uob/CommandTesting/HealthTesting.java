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
    void lookAndGetPlayerDeath() {
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
        sendCommandToServer("simon: fight with elf");
        response = sendCommandToServer("simon: health");
        sendCommandToServer("simon: fight with elf");
        sendCommandToServer("simon: health");
        response = sendCommandToServer("simon: fight with elf");
        response = response.toLowerCase();
        assertEquals("you died and lost all of your items, you must return to the start of the game\n", response);
        response = sendCommandToServer("simon: health");
        response = response.toLowerCase();
        response = sendCommandToServer("simon: look");
        assertTrue(response.toLowerCase().contains("cabin"));
        //TODO: FIX
//        response = sendCommandToServer("mihir: goto cabin");
//        response = sendCommandToServer("mihir: get potion");
//        response = sendCommandToServer("mihir: inv");
//        assertTrue(response.toLowerCase().contains("potion"));

    }
}
