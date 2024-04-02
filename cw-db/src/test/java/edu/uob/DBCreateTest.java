package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DBCreateTest {
    public DBServer server;
    @SuppressWarnings("ResultOfMethodCallIgnored")
    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        if(server == null){
            server = new DBServer();
        }
    }

    @AfterAll
    public static void cleanDBFolder() {
        File databaseFolder = new File(Paths.get("databases").toAbsolutePath().toString());
        File[] directories = databaseFolder.listFiles();
        if (directories == null) return;
        for (File directory : directories) {
            File[] files = directory.listFiles();
            if (files == null) continue;
            for (File file : files) file.delete();
            directory.delete();
        }
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    public static String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    //Just 'CREATE' without ';' catches in CREATE - edge case found!
    @Test
    public void testParsingJustCreate() {
        String testEmptyCmd = sendCommandToServer("create");
        String expected = "[ERROR]" + " No ';' at end!";
        assertEquals(expected, testEmptyCmd);
    }


}