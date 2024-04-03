package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DBUseTest {
    public DBServer server;
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

    // Randomises the case of characters in the input string
    public static String randomiseCasing(String inFromGenerateRandomName) {
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
    public void testUseIsValid(){
        String randomName = randomiseCasing(generateRandomName());
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        String query2 = sendCommandToServer("USE " + randomName + ";");
        assertTrue(query2.contains("[OK]"));
    }

    @Test
    public void testUseCaseInsensitiveAndTokenizedWhiteSpace(){
        String randomName = randomiseCasing(generateRandomName());
        String query1 = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(query1.contains("[OK]"));
        String query2 = sendCommandToServer("uSE       " + randomName + "        ;        ");
        assertTrue(query2.contains("[OK]"));
    }

    @Test
    public void testCreateDbNotExecuted(){
        String randomName = randomiseCasing(generateRandomName());
        String query2 = sendCommandToServer("use " + randomName + ";");
        String response = sendCommandToServer(query2);
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testInvalidDatabaseName(){
        String randomName = randomiseCasing(generateRandomName());
        //Modify string such that invalid:
        String response = sendCommandToServer("cReAtE dAtabasE " +  randomName + ";");
        assertTrue(response.contains("[OK]"));
        randomName = "#" + randomName.substring(1);
        String testUse = sendCommandToServer("uSe " + randomName + ";");
        String expectedUseResponse = "[ERROR] " + randomName + " Database syntax is not a valid name!";
        assertEquals(expectedUseResponse, testUse);
    }

    @Test
    public void testWBoxDirRemoved(){
        String randomName = randomiseCasing(generateRandomName());
        String query1 = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(query1.contains("[OK]"));
        String query2 = sendCommandToServer("uSE       " + randomName + "        ;        ");
        assertTrue(query2.contains("[OK]"));
        sendCommandToServer("drop database " + randomName + ";");
        String query3 = sendCommandToServer("uSE       " + randomName + "        ;        ");
        String expected = "[ERROR] " + randomName.toLowerCase() + " is not an existing database.";
        assertEquals(expected, query3);
    }

    @Test
    public void testSetPathIfNullInUseWhiteBox(){
        String randomName = randomiseCasing(generateRandomName());
        String query1 = sendCommandToServer("CREATE DATABASE " + randomName + ";");
        assertTrue(query1.contains("[OK]"));
        String query2 = sendCommandToServer("uSE       " + randomName + "        ;        ");
        assertTrue(query2.contains("[OK]"));
        server.dbStore.storagePath = null;
        sendCommandToServer("uSE       " + randomName + "        ;        ");
        //Removed method from use DBServer will always set
        assertNotNull(server.dbStore.storagePath);
    }

}