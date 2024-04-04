package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DBInsertTest {
    public DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        if (server == null) {
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
        for (int i = 0; i < 10; i++) randomName += (char) (97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000000), () -> {
                    return server.handleCommand(command);
                },
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
    public void testInsertCommandIsValid(){
        String randomName = randomiseCasing(generateRandomName());
        sendCommandToServer("CREATE DATABASE "+randomName+";");
        sendCommandToServer("use "+randomName+";");
        String randomTbName = randomiseCasing(generateRandomName());
        String query1 = sendCommandToServer("CREATE TABLE "+ randomTbName +" (studentName, mark, pass, graduated, allergies);");
        sendCommandToServer(query1);
        String studentName = generateRandomName();
        double mark = 39.0;
        boolean pass = false;
        boolean graduated = false;
        String query2 = sendCommandToServer("Insert into " + randomTbName + " values ('" + studentName + "'," + mark + "," + pass + "," + graduated + "," + "null);");
        System.out.println(query2);
        assertTrue(query2.contains("[OK]"));
        String query3 = sendCommandToServer("Select  * from " + randomTbName + " ;");
        System.out.println(query3);
        assert(query3.contains(studentName));
        assert(query3.contains("39.0"));
        assert(query3.contains("FALSE"));
        assert(query3.contains("NULL"));
    }


}