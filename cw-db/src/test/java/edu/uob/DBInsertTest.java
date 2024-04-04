package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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
        String studentName = generateRandomName();
        double mark = 39.0;
        boolean pass = false;
        boolean graduated = false;
        String query2 = sendCommandToServer("Insert into " + randomTbName + " values ('" + studentName + "'," + mark + "," + pass + "," + graduated + "," + "null);");
        String expectedQ2 = "[OK] " + "Values inserted into " + randomTbName.toLowerCase();
        assertEquals(expectedQ2, query2);
        ArrayList<String> expectedColumns = new ArrayList<>();
        expectedColumns.addAll(Arrays.asList("id", "studentname", "mark", "pass", "graduated", "allergies"));
        ArrayList<String> actualColumns = server.dbStore.table.getColumns();
        assertEquals(expectedColumns, actualColumns);
        actualColumns.forEach(item -> assertTrue(item.equals(item.toLowerCase())));
        String query3 = sendCommandToServer("Select  * from " + randomTbName + " ;");
        assertTrue(query3.contains(studentName));
        assertTrue(query3.contains("39.0"));
        assertTrue(query3.contains("FALSE"));
        assertTrue(query3.contains("NULL"));
    }

    @Test
    public void testColumnsOutputAreAllToLowerCase () {
        String randomName = randomiseCasing(generateRandomName());
        sendCommandToServer("CREATE DATABASE "+randomName+";");
        sendCommandToServer("use "+randomName+";");
        String randomTbName = generateRandomName().toUpperCase();
        sendCommandToServer("CREATE TABLE "+ randomTbName +" (STUDENTNAME, MARK, PASS, GRADUATED, ALLERGIES);");
        String studentName = generateRandomName();
        double mark = 39.0;
        boolean pass = false;
        boolean graduated = false;
        sendCommandToServer("Insert into " + randomTbName + " values ('" + studentName + "'," + mark + "," + pass + "," + graduated + "," + "null);");
        ArrayList<String> actualColumns = server.dbStore.table.getColumns();
        actualColumns.forEach(item -> assertTrue(item.equals(item.toLowerCase())));
    }

    @Test
    public void testColumnsAreCaseInsensitive () {
        String randomName = randomiseCasing(generateRandomName());
        sendCommandToServer("CREATE DATABASE "+randomName+";");
        sendCommandToServer("use "+randomName+";");
        String randomTbName = generateRandomName().toUpperCase();
        sendCommandToServer("CREATE TABLE "+ randomTbName +" (STUDENTNAME, MARK, PASS, GRADUATED, ALLERGIES);");
        String studentName = generateRandomName();
        double mark = 39.0;
        boolean pass = false;
        boolean graduated = false;
        sendCommandToServer("Insert into " + randomTbName + " values ('" + studentName + "'," + mark + "," + pass + "," + graduated + "," + "null);");
        ArrayList<String> actualColumns = server.dbStore.table.getColumns();
        actualColumns.forEach(item -> assertTrue(item.equalsIgnoreCase(item)));
    }

    //test in query: perhaps create multiple databases dir, attempt to delete databases with .keep cannot be allowed.
    //test parsing (ie wrong spelling, no into tkn, no values tkn, no brackets, missing brackets, attribName)
    //test handling same column names handling
    //test no columns inserted
    //test just create table (no cols)-> insert into
    //test alter works? Y. what if one col removed/added - does expected change?
    //test more columns inserted than there are
    //test vice versa ^
    //test col names have spaces
    //test with NULL
    //id generation matches
    //tab spaces are correct (maybe select testing incorp)
}