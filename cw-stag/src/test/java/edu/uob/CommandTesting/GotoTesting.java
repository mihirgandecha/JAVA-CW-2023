package edu.uob.CommandTesting;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static javax.swing.text.html.parser.DTDConstants.NUMBERS;
import static org.junit.jupiter.api.Assertions.*;

public class GotoTesting {

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

    //TODO: Goto for extended

    // Test that we can goto a different location (we won't get very far if we can't move around the game !)
    @Test
    void testGoto() {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
    }

    @Test
    void gotoWorkingAroundBasicMap(){
        String response = "mihir: goto forest";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("forest"));
        response = "mihir: look";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("forest"));
        assertTrue(response.toLowerCase().contains("key"));
        assertTrue(response.toLowerCase().contains("tree"));
        assertTrue(response.toLowerCase().contains("cabin"));
        sendCommandToServer("mihir: get key");
        response = "mihir: goto cabin";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("cabin"));
        response = "mihir: look";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("axe"));
        assertTrue(response.toLowerCase().contains("potion"));
        assertTrue(response.toLowerCase().contains("trapdoor"));
        assertTrue(response.toLowerCase().contains("forest"));
        assertTrue(response.toLowerCase().contains("cabin"));
        sendCommandToServer("mihir: unlock trapdoor");
        response = "mihir: look";
        response = sendCommandToServer(response);
        assertTrue(response.contains("cellar"));
        response = "mihir: goto cellar";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("elf"));
        assertTrue(response.toLowerCase().contains("dusty cellar"));
        assertTrue(response.toLowerCase().contains("cabin"));
    }

    @Test
    void gotoWithoutUsernameShouldFail(){
        String response = ": goto ";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
        response = " goto ";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void emptyGotoShouldFail(){
        String response = "mihir: goto ";
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void randomisedCasingGotoShouldPass(){
        String response = "simon: goto forest";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.contains("forest"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("tree"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
    }

    @Test
    void randomisedCasingWithPunctuationAround(){
        String response = "simon: %goTo 0 fOrEst $";
        response = sendCommandToServer(response);
        assertTrue(response.contains("forest"), "Failed attempt to use 'look' command");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("key"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("tree"), "Failed attempt to use 'look' command");
        assertTrue(response.contains("cabin"), "Failed attempt to use 'look' command");
    }

    @Test
    void gotoLocationThatIsNotAccessible(){
        String response = "mihir: goto cellar";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("no path"));
        response = "simon: goto cellar";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("no path"));
    }

    @Test
    void decorativeTestingGotoWithMultipleLocations(){
        String response = "mihir: goto forest cabin";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertFalse(response.contains("cellar"), "Failed attempt to use 'look' command");
        assertFalse(response.contains("forest"), "Failed attempt to use 'look' command");
        assertTrue(response.toLowerCase().contains("error"));
    }

    @Test
    void decorativeTestingGotoInReverseOrder(){
        String response = "mihir: forest goto";
        randomiseCasing(response);
        response = sendCommandToServer(response);
        assertTrue(response.toLowerCase().contains("forest"));
        assertTrue(response.toLowerCase().contains("key"));
        assertTrue(response.toLowerCase().contains("tree"));
        assertTrue(response.toLowerCase().contains("cabin"));
    }
}
