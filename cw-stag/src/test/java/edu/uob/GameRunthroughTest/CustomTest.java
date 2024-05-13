package edu.uob.GameRunthroughTest;

import edu.uob.GameServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomTest {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() throws Exception {
        File entitiesFile = Paths.get("config" + File.separator + "custom-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "custom-actions.xml").toAbsolutePath().toFile();
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
                randomiseCaseForName.append(Character.toUpperCase(c));
            } else {
                randomiseCaseForName.append(Character.toLowerCase(c));
            }
        }
        return randomiseCaseForName.toString();
    }

    @Test
    void noStoreRoomCreate(){
        String response = sendCommandToServer("mihir: goto storeroom");
        assertTrue(response.toLowerCase().contains("you are in storage for any entities not placed in the game. you can see:"));
    }

    @Test
    void chopElfInHalf(){
        sendCommandToServer("mihir: goto forest");
        sendCommandToServer("mihir: get key");
        sendCommandToServer("mihir: goto cabin");
        sendCommandToServer("mihir: unlock trapdoor");
        sendCommandToServer("mihir: get axe");
        sendCommandToServer("mihir: goto cellar");
        String response = sendCommandToServer("mihir: chop elf");
        assertTrue(response.toLowerCase().contains("gave you some gold"));
        response = sendCommandToServer("mihir: look");
        System.out.println(response);
        assertTrue(response.toLowerCase().contains("pot of gold"));
        response = sendCommandToServer("mihir: rest");
        System.out.println(response);
    }

    @Test
    void handleActionIsNotPartOfXMLFile(){
        String response = sendCommandToServer("mihir: getTO storeroom");
        assertTrue(response.toLowerCase().contains("[error]command requires at least one valid action"));
    }

    @Test
    void handleNoSubjectIsValid(){

    }

    @Test
    void handleMissingSubjectIsInvalid(){

    }

    @Test
    void handleNoConsumedIsValid(){

    }

    @Test
    void handleNoProducedIsValid(){

    }

    @Test
    void handleTriggerWordAsProducedIsInvalid(){

    }

    @Test
    void handleConsumedDuplicateGameEntityIsInvalid(){

    }

    @Test
    void handleProducedDuplicateGameEntityIsInvalid(){

    }

    @Test
    void handleConsumingLocationIsValid(){

    }

    @Test
    void handleConsumingWithNoSubjectIsValid(){

    }

    @Test
    void handleConsumingMultipleCharactersIsValid(){

    }

    @Test
    void handleMultipleActionsValidTriggerWordsWithOneThatHasCorrectSubjectsIsTriggered(){

    }

    //TODO: Check
    //Can do rest and rest?
    @Test
    void handleOneWordActionWithNoSubjectsIsInvalid(){

    }

    @Test
    void handleAmbiguousCommandsWithValidSubjectsYetMultipleActionsIsInvalid(){

    }

    @Test
    void handleWrongCurrentLocationIsInvalid(){

    }

    @Test
    void handleCoLocationIsValid(){

    }
}
