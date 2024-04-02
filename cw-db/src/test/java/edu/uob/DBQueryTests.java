package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DBQueryTests {

    private DBServer server;
    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
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
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
        "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testGivenScript(){
        String createDbResult = sendCommandToServer("CREATE DATABASE markbook;");
        assertTrue(createDbResult.contains("[OK]"));
        String useDbResult = sendCommandToServer("USE markbook;");
        assertTrue(useDbResult.contains("[OK]"));
        String createTableMarksResult = sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        assertTrue(createTableMarksResult.contains("[OK]"));
        String insertSimonResult = sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        assertTrue(insertSimonResult.contains("[OK]"));
        String insertSionResult = sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        assertTrue(insertSionResult.contains("[OK]"));
        String insertRobResult = sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        assertTrue(insertRobResult.contains("[OK]"));
        String insertChrisResult = sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        assertTrue(insertChrisResult.contains("[OK]"));
        String selectMarksResult = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(selectMarksResult.contains("[OK]"));
        String createTableCourseworkResult = sendCommandToServer("CREATE TABLE coursework (task, submission);");
        assertTrue(createTableCourseworkResult.contains("[OK]"));
        String insertCoursework1Result = sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 3);");
        assertTrue(insertCoursework1Result.contains("[OK]"));
        String insertCoursework2Result = sendCommandToServer("INSERT INTO coursework VALUES ('DB', 1);");
        assertTrue(insertCoursework2Result.contains("[OK]"));
        String insertCoursework3Result = sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 4);");
        assertTrue(insertCoursework3Result.contains("[OK]"));
        String insertCoursework4Result = sendCommandToServer("INSERT INTO coursework VALUES ('STAG', 2);");
        assertTrue(insertCoursework4Result.contains("[OK]"));
        String selectCourseworkResult = sendCommandToServer("SELECT * FROM coursework;");
        assertTrue(selectCourseworkResult.contains("[OK]"));
        String joinResult = sendCommandToServer("JOIN coursework AND marks ON submission AND id;");
        assertTrue(joinResult.contains("[OK]"));
    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test
    public void testBasicCreateAndQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");
    }

    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
    // (these IDs are used to create relations between tables, so it is essential that suitable IDs are being generated and returned !)
    @Test
    public void testQueryID() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Simon';");
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the space character
        String[] tokens = singleLine.split(" ");
        // Check that the very last token is a number (which should be the ID of the entry)
        String lastToken = tokens[tokens.length-1];
        try {
            Integer.parseInt(lastToken);
        } catch (NumberFormatException nfe) {
            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Simon';` should have been an integer ID, but was " + lastToken);
        }
    }

    // A test to make sure that databases can be reopened after server restart
    @Test
    public void testTablePersistsAfterRestart() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        // Create a new server object
        server = new DBServer();
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Simon"), "Simon was added to a table and the server restarted - but Simon was not returned by SELECT *");
    }

    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
    @Test
    public void testForErrorTag() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        String response = sendCommandToServer("SELECT * FROM libraryfines;");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }
}
