package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.util.Random;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.*;

class BasicCmndTest {

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
    void testNoInput() {
        String response = sendCommandToServer("");
        response = response.toLowerCase();
        assertTrue(response.contains("error"));
    }

    @Test
    void testNoUsername() {
        String response = sendCommandToServer(": look");
        response = response.toLowerCase();
        assertTrue(response.contains("username is invalid"));
    }

    @Test
    void testSpacing() {
        String response = sendCommandToServer("  simon  :    look     ");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"));
    }

    @Test
    void testAdditionalCharacters() {
        String response = "  simon  :    :look:     ";
        response = sendCommandToServer(randomiseCasing(response));
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"));
    }

    @Test
    void testAdditionalWeirdCharacters() {
        String response = "  simon  :    #look#     ";
        response = sendCommandToServer(randomiseCasing(response));
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"));
    }
}


