package edu.uob;

import edu.uob.DBCmnd.SyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleDBTests {

    private DBServer server;
    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    public static String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    //TODO: Debugging had to increase time: Understand how this works
    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000000), () -> { return server.handleCommand(command);},
        "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
//    @Test
//    public void testBasicCreateAndQuery() {
//        String randomName = generateRandomName();
//        String testCreate = sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        assertTrue(testCreate.contains("[OK]"));
//        String testUse = sendCommandToServer("USE " + randomName + ";");
//        assertTrue(testUse.contains("[OK]"));
//        assertEquals(randomName, server.dbStore.dbName);
//        assertEquals(server.dbStore.dbPath, server.dbStore.currentDbPath);
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
//        String response = sendCommandToServer("SELECT * FROM marks;");
//        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
//        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
//        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
//        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");
//    }

    @Test
    public void testInvalidDatabase() {
        String randomUseNameTest = generateRandomName();
        String command = "USE " + randomUseNameTest + ";";
        SyntaxException thrown = assertThrows(
                SyntaxException.class,
                () -> sendCommandToServer(command),
                "[ERROR]"
        );
        assertTrue(thrown.getMessage().contains("[ERROR]"));
        assertNotEquals(randomUseNameTest, server.dbStore.dbName);
    }

    @Test
    public void testInvalidCreate() throws IOException {
        server.dbStore.deleteEmptyDir("testDb");
        String database = "testDb";
        sendCommandToServer("CREATE DATABASE " + database + ";");
        String invalidCr = "CREATE DATABASE " + database + ";";
        SyntaxException thrown = assertThrows(
                SyntaxException.class,
                () -> sendCommandToServer(invalidCr),
                "[ERROR]"
        );
        assertTrue(thrown.getMessage().contains("[ERROR]"));
        server.dbStore.deleteEmptyDir(database);
        assertNotEquals(database, server.dbStore.dbName);
        assertEquals(null, server.dbStore.dbName);
        assertEquals(null, server.dbStore.dbPath);
    }

//    @Test
//    public void testCreateDbOk() {
//        String randomName = generateRandomName();
//        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        assertTrue(response.contains("[OK]"));
//    }

//    @Disabled
//    @Test
//    public void testUseOk() {
//        String randomName = "mihirTest";
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        String response = sendCommandToServer("USE " + randomName + ";");
//        assertTrue(response.contains("[OK]"));
//
//    }
//
//    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
//    // (these IDs are used to create relations between tables, so it is essential that suitable IDs are being generated and returned !)
//    @Test
//    public void testQueryID() {
//        String randomName = generateRandomName();
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        sendCommandToServer("USE " + randomName + ";");
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Simon';");
//        // Convert multi-lined responses into just a single line
//        String singleLine = response.replace("\n"," ").trim();
//        // Split the line on the space character
//        String[] tokens = singleLine.split(" ");
//        // Check that the very last token is a number (which should be the ID of the entry)
//        String lastToken = tokens[tokens.length-1];
//        try {
//            Integer.parseInt(lastToken);
//        } catch (NumberFormatException nfe) {
//            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Simon';` should have been an integer ID, but was " + lastToken);
//        }
//    }
//
//    // A test to make sure that databases can be reopened after server restart
//    @Test
//    public void testTablePersistsAfterRestart() {
//        String randomName = generateRandomName();
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        sendCommandToServer("USE " + randomName + ";");
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        // Create a new server object
//        server = new DBServer();
//        sendCommandToServer("USE " + randomName + ";");
//        String response = sendCommandToServer("SELECT * FROM marks;");
//        assertTrue(response.contains("Simon"), "Simon was added to a table and the server restarted - but Simon was not returned by SELECT *");
//    }
//
//    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
//    @Test
//    public void testForErrorTag() {
//        String randomName = generateRandomName();
//        sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        sendCommandToServer("USE " + randomName + ";");
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        String response = sendCommandToServer("SELECT * FROM libraryfines;");
//        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
//        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
//    }

}
