package edu.uob.CommandTesting;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class LookTesting {

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

    //Look testing:
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
    void lookRandomiseCasingUsingMethod() {
        String response = randomiseCasing("simon: look");
        response = sendCommandToServer(response);
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    }

    @Test
    void lookDecorativeWithRandomisedCasingPassed(){
        String response = "simon: i want to look wherever i am please";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("axe"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("potion"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("trapdoor"), "Failed attempt to use 'look' command");
    }

    @Test
    void testWithIncorrectSpellingFails(){
        String response = "simon: loker looky lookatme ilooknoworky";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void lookAdvancedDecorative(){
        String response = "simon: look in forest";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
    }


    @Test
    void lookCommandExtraneous(){
        //Duplicate no error however >1 trigger word is error
        String response = sendCommandToServer("mihir: look LoOk");
        assertFalse(response.toLowerCase().contains("error"));
        response = sendCommandToServer("mihir: look inv");
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void lookCommandExtraneous2(){
        String response = sendCommandToServer("mihir: have a look if there is a potion");
        assertTrue(response.toLowerCase().contains("error"));
    }
}
