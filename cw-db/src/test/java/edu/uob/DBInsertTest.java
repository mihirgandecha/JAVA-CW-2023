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

    //Parsing Tests: "INSERT " "INTO " [TableName] " VALUES" "(" <ValueList> ")"
    //TODO every check for isdb or istb should also check for isKeyword
    @Test
    public void testInsertNoTableName() {
        String testCmd = sendCommandToServer("INSERT INTO  VALUES ('value');");
        String expected = "[ERROR]" + " " + "VALUES" + " is not a valid table name!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertNoColumns() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ();");
        String expected = "[ERROR]" + " No value(s) inside brackets or invalid token length error";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertInvalidTableName() {
        String testCmd = sendCommandToServer("INSERT INTO !names!  VALUES ('value');");
        String expected = "[ERROR]" + " " + "!names!" + " is not a valid table name!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertInvalidTableNameIsSemiColon() {
        String testCmd = sendCommandToServer("INSERT INTO ;; VALUES ('value');");
        String expected = "[ERROR]" + " " + ";" + " is not a valid table name!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertIntoInvalidInsertSpellingCaughtAtServer() {
        String testCmd = sendCommandToServer("ISERT INto tableName VALUES ('value');");
        String expected = "[ERROR] [SERVER]: Empty/Invalid Command";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertIntoInvalidIntoSpelling() {
        String testCmd = sendCommandToServer("INSERT INOT tableName VALUES ('value');");
        String expected = "[ERROR]" + " expected INTO token after INSERT.";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertIntoInvalidValuesSpelling() {
        String testCmd = sendCommandToServer("INSERT INto tableName VALES ('value');");
        String expected = "[ERROR]" + " Expected VALUES after table name.";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertNoEndSemiColon() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ('value')");
        String expected = "[ERROR]" + " No ';' at end!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertNoDBCreated() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ('value');");
        assertEquals("[ERROR] No Database selected. USE command not executed.", testCmd);
    }

    @Test
    public void testInsertMissingOpeningParenthesis() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES 'value');");
        String expected = "[ERROR]" + " Expected '(' after VALUES";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertMissingClosingParenthesis() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ('value';");
        String expected = "[ERROR]" + " Expected ')' after VALUES";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertInvalidColumnIsSemiCol() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ('value', ;);");
        String expected = "[ERROR]" + " Invalid value: ;";
        assertEquals(expected, testCmd);
    }

    //Parsing testing values:
    @Test
    public void testInsertIncompleteFloatLiteral() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES (123.);");
        String expected = "[ERROR] Invalid value: 123.";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertInvalidBooleanLiteral() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES (TRU);");
        String expected = "[ERROR] Invalid value: TRU";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertInvalidNegativeInteger() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES (-);");
        String expected = "[ERROR] Invalid value: -";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertStringWithoutQuotes() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES (SomeString);");
        String expected = "[ERROR] Invalid value: SomeString";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertExtraCommaBetweenValues() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ('ValidString', , 123);");
        String expected = "[ERROR] Invalid value: ,";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertOnlyCommaNoValue() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES (,);");
        String expected = "[ERROR] Invalid value: ,";
        assertEquals(expected, testCmd);
    }

    //No time to change Tokeniser, however edge case found when tokeniser splits "'" causing mismatch to glue with ');'
    @Test
    public void testInsertUnmatchedQuotes() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ('Mismatch);");
        String expected = "[ERROR] No ';' at end!";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertInvalidFloatMultipleDots() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES (123.4.56);");
        String expected = "[ERROR] Invalid value: 123.4.56";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertInvalidValueCombination() {
        String testCmd = sendCommandToServer("INSERT INTO tableName VALUES ('String', TRUE, 123, -45A.678);");
        String expected = "[ERROR] Invalid value: -45A.678";
        assertEquals(expected, testCmd);
    }

    @Test
    public void testInsertEdgeCase() {
        String randomDb = randomiseCasing(generateRandomName());
        String randomTb = randomiseCasing(generateRandomName());
        sendCommandToServer("create database " + randomDb + ";");
        sendCommandToServer("use " + randomDb + ";");
        sendCommandToServer("create table " + randomTb + ";");
        sendCommandToServer("alter table " + randomTb + " add name" + ";");
        String testCmd = sendCommandToServer("INSERT INTO " + randomTb + " VALUES ('mihir Gandecha');");
        System.out.println(testCmd);
        assertTrue(testCmd.contains("OK"));
    }
    //test in query: perhaps create multiple databases dir, attempt to delete databases with .keep cannot be allowed.
    //DONE:test parsing (ie wrong spelling, no into tkn, no values tkn, no brackets, missing brackets, attribName)
    //DONE:test handling same column names handling
    //DONE:test no columns inserted - working
    //DONE:test just create table (no cols)-> insert into; working -> change msg if 0 cannot insert into id?

    //test alter works? Y. what if one col removed/added - does expected change? -> TODO: handle spacing (eg insert name age only adds name)
    //insert into TODO should mihir and 'mihir' work? it displays 'mihir' when output
    //test more columns inserted than there are: TODO just change messaging if no attrib,
    //TODO error with expected: actual: not showing!
    //test vice versa ^
    //test col names have spaces //TODO this needs handling!

    //test with NULL:
    //alter table interesting add null;
    // select * from interesting;
    //[ERROR]Incorrect number of values. Expected: 2, but received: 1
    //TODO ^ this shouldnt come with select: error found in readValues!

    //id generation matches
    //tab spaces are correct (maybe select testing incorp)
}