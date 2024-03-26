package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
        "Server took too long to respond (probably stuck in an infinite loop)");
    }

    //TODO condition -> use float for everything (convert into int)
//    @Test
//    public void testStoragePathMetadataInitiatedIsValid() {
//        DBServer server = new DBServer(); // Assuming this initializes dbStore's storagePath
//        try {
//            sendCommandToServer("");
//            String actualStoragePath = server.dbStore.storagePath.toString();
//            assertTrue(actualStoragePath.contains("databases"));
//        } catch (Exception e) {
//        }
//    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
//    @Test
//    public void testBasicCreateAndQuery() {
//        String randomName = generateRandomName();
//        String testCreate = sendCommandToServer("CREATE DATABASE " + randomName + ";");
//        assertTrue(testCreate.contains("[OK]"));
//        assertEquals(randomName, server.dbStore.dbName);
//        String testUse = sendCommandToServer("USE " + randomName + ";");
//        assertTrue(testUse.contains("[OK]"));
//        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
//        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
//        String response = sendCommandToServer("SELECT * FROM marks;");
//        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
////        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
////        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
////        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");
//    }

    //CREATE Parsing:

    //Empty Command stops at SERVER:
    @Test
    public void testEmptyCmd() {
        String testEmptyCmd = sendCommandToServer("");
        String expected = " [SERVER]: Command Query is empty.";
        assertTrue(testEmptyCmd.contains(expected));
    }

    //Just 'CREATE' without ';' catches in CREATE - edge case found!
    @Test
    public void testParsingJustCreate() {
        String testEmptyCmd = sendCommandToServer("create");
        String expected = "[ERROR]" + " [SERVER]: Token cmnd NOT uppercase!";
        assertEquals(expected, testEmptyCmd);
    }

    //Test for "CREATE DATABASE" for no ';'
    @Test
    public void testParsingJustCreateDatabase() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE");
        String expected = "[ERROR]" + " No ';' at end!";
        assertEquals(expected, testEmptyCmd);
    }

    //Test for "CREATE DATABASE" for no dbName
    @Test
    public void testParsingNoDBName() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE;");
        String expected = "[ERROR]" + " Token length invalid.";
        assertEquals(expected, testEmptyCmd);
    }

    //Test for "CREATE DATABASE" for invalid dbName
    @Test
    public void testParsingInvalidDBName() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE #;");
        String expected = "[ERROR]" + " Invalid Database name!";
        assertEquals(expected, testEmptyCmd);
    }

    //Test for "CREATE DATABASE" for more than 4 tokens
    @Test
    public void testParsingInvalidTokenLen() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE # 2 4;");
        String expected = "[ERROR]" + " Token length invalid.";
        assertEquals(expected, testEmptyCmd);
    }


    //CREATE TABLE PARSING TESTS:
//    @Test
//    public void testCTbValid() throws IOException {
//        String testEmptyCmd = sendCommandToServer("CREATE TABLE newTb;");
//        String expected = "[ERROR]" + "  No Database selected. USE command not implemented.";
//        assertEquals(expected, testEmptyCmd);
//    }

    // Test for "CREATE TABLE" without table name
    @Test
    public void testParsingCTblLowercase() {
        String testCmd = sendCommandToServer("CREATE table;");
        String expected = "[ERROR]" + " Parsing [CREATE]: Token 'DATABASE'/'TABLE' not found!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testParsingCTblInvalidTknLen() {
        String testCmd = sendCommandToServer("CREATE TABLE;");
        String expected = "[ERROR]" + " Token length invalid.";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testParsingCTblInvalidTblName() {
        String testCmd = sendCommandToServer("CREATE TABLE !name;");
        String expected = "[ERROR]" + " Invalid Table name!";
        assertEquals(expected, testCmd);
    }

    //Parsing just CREATE TABLE <TABLENAME>
    @Test
    public void testParsingCTblNoEndCaughtServer() {
        String testCmd = sendCommandToServer("CREATE TABLE tbName");
        String expected = "[ERROR]" + " No ';' at end!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testParsingCTblNoDBCreated() {
        String testCmd = sendCommandToServer("CREATE TABLE tbName;");
        assertEquals("[ERROR] No Database selected. USE command not implemented.", testCmd);
    }

    //Test for Invalid Attribute List
    @Test
    public void testParsingMissingOpeningParenthesis() {
        String testCmd = sendCommandToServer("CREATE TABLE tableName attribute1 INT, attribute2 VARCHAR);");
        String expected = "[ERROR]" + " Token '(' not found!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testParsingMissingClosingParenthesis() {
        String testCmd = sendCommandToServer("CREATE TABLE tableName (attribute1 INT, attribute2 VARCHAR;");
        String expected = "[ERROR]" + " Token ')' not found!";
        assertEquals(expected, testCmd);
    }

//    @Test
//    public void testParsingCTblBasicValid() throws IOException {
//        server.dbStore.deleteEmptyDir("newdb");
//        sendCommandToServer("CREATE DATABASE newdb;");
//        sendCommandToServer("USE newdb;");
//        String testCmd = sendCommandToServer("CREATE TABLE tbName;");
//        assertTrue(testCmd.contains("[OK]"));
//    }

    // Test for "CREATE TABLE" with invalid attribute syntax
    @Test
    public void testParsingInvalidAttributeSyntax() {
        String testCmd = sendCommandToServer("CREATE TABLE tableName (attribute1 INT, attribute2);");
        String expected = "[ERROR]" + " No comma found!";
        assertEquals(expected, testCmd);
    }


    //Testing DROP DB
//    @Test
//    public void testDropDatabaseIsValid() {
//        sendCommandToServer("CREATE DATABASE testDrop;");
//        sendCommandToServer("USE testDrop;");
//        String testCmd = sendCommandToServer("DROP DATABASE testDrop;");
//        System.out.println(testCmd);
//        assertTrue(testCmd.contains("[OK]"));
//
//    }

    // Test for successful "CREATE TABLE" command with attribute list
//    @Test
//    public void testParsingValidCreateTable() {
//        sendCommandToServer("CREATE DATABASE testDbTb");
//        sendCommandToServer("USE testDbTb");
//        String testCmd = sendCommandToServer("CREATE TABLE tableName (attribute1, attribute2, attribute3);");
//        String expected = "[OK]" + "Table 'tableName' created successfully.";
//        assertEquals(expected, testCmd);
//    }
//
//
//
//    //CREATE TABLE IMPLEMENT TESTS:
//    @Test
//    public void testCTbUseNotImplemented() throws IOException {
//        server.dbStore.deleteEmptyDir("newDb");
//        sendCommandToServer("CREATE DATABASE newDb;");
//        String testEmptyCmd = "CREATE TABLE newTb;";
//        SyntaxException thrown = assertThrows(
//                SyntaxException.class,
//                () -> sendCommandToServer(testEmptyCmd),
//                "[ERROR]"
//        );
//        String expected = "[ERROR]" + "  No Database selected. USE command not implemented.";
//        String actual = thrown.getMessage();
//        assertEquals(expected, thrown.getMessage());
//        assertTrue(thrown.getMessage().contains(expected));
//    }






    //    @Test
//    public void testInvalidDatabase() {
//        String randomUseNameTest = generateRandomName();
//        String command = "USE " + randomUseNameTest + ";";
//        SyntaxException thrown = assertThrows(
//                SyntaxException.class,
//                () -> sendCommandToServer(command),
//                "[ERROR]"
//        );
//        assertTrue(thrown.getMessage().contains("[ERROR]"));
//        assertNotEquals(randomUseNameTest, server.dbStore.dbName);
//    }

//    @Test
//    public void testInvalidCreate() throws IOException {
////        server.dbStore.deleteEmptyDir("testDb");
//        String database = "testDb";
////        String validCr = sendCommandToServer("CREATE DATABASE " + database + ";");
////        assertTrue(validCr.contains("[OK]"));
//        //String invalidCr = "CREATE DATABASE " + database + ";";
//        SyntaxException thrown = assertThrows(SyntaxException.class, () -> sendCommandToServer("CREATE DATABASE testDb;"));
//        //System.out.println(thrown.getMessage());
//        assertTrue(thrown.getMessage().contains("[ERROR]"));
////        server.dbStore.deleteEmptyDir(database);
////        assertNotEquals(database, server.dbStore.dbName);
////        assertEquals(null, server.dbStore.dbName);
////        assertEquals(null, server.dbStore.dbPath);
//    }
//
////    @Test
////    public void testCreateDbOk() {
////        String randomName = generateRandomName();
////        String response = sendCommandToServer("CREATE DATABASE " + randomName + ";");
////        assertTrue(response.contains("[OK]"));
////    }
//
////    @Disabled
////    @Test
////    public void testUseOk() {
////        String randomName = "mihirTest";
////        sendCommandToServer("CREATE DATABASE " + randomName + ";");
////        String response = sendCommandToServer("USE " + randomName + ";");
////        assertTrue(response.contains("[OK]"));
////
////    }
////
////    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
////    // (these IDs are used to create relations between tables, so it is essential that suitable IDs are being generated and returned !)
////    @Test
////    public void testQueryID() {
////        String randomName = generateRandomName();
////        sendCommandToServer("CREATE DATABASE " + randomName + ";");
////        sendCommandToServer("USE " + randomName + ";");
////        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
////        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
////        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Simon';");
////        // Convert multi-lined responses into just a single line
////        String singleLine = response.replace("\n"," ").trim();
////        // Split the line on the space character
////        String[] tokens = singleLine.split(" ");
////        // Check that the very last token is a number (which should be the ID of the entry)
////        String lastToken = tokens[tokens.length-1];
////        try {
////            Integer.parseInt(lastToken);
////        } catch (NumberFormatException nfe) {
////            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Simon';` should have been an integer ID, but was " + lastToken);
////        }
////    }
////
////    // A test to make sure that databases can be reopened after server restart
////    @Test
////    public void testTablePersistsAfterRestart() {
////        String randomName = generateRandomName();
////        sendCommandToServer("CREATE DATABASE " + randomName + ";");
////        sendCommandToServer("USE " + randomName + ";");
////        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
////        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
////        // Create a new server object
////        server = new DBServer();
////        sendCommandToServer("USE " + randomName + ";");
////        String response = sendCommandToServer("SELECT * FROM marks;");
////        assertTrue(response.contains("Simon"), "Simon was added to a table and the server restarted - but Simon was not returned by SELECT *");
////    }
////
////    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
////    @Test
////    public void testForErrorTag() {
////        String randomName = generateRandomName();
////        sendCommandToServer("CREATE DATABASE " + randomName + ";");
////        sendCommandToServer("USE " + randomName + ";");
////        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
////        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
////        String response = sendCommandToServer("SELECT * FROM libraryfines;");
////        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
////        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
////    }

}
