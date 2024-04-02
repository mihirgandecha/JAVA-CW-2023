package edu.uob;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

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

    // Randomizes the case of characters in the input string
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
    public void testParsingJustCreate() {
        String testEmptyCmd = sendCommandToServer("create");
        String expected = "[ERROR]" + " No ';' at end!";
        assertEquals(expected, testEmptyCmd);
    }

    @Test
    public void testParsingJustCreateDatabase() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE");
        String expected = "[ERROR]" + " No ';' at end!";
        assertEquals(expected, testEmptyCmd);
    }

    @Test
    public void testParsingCreateDbNoDBName() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE;");
        String expected = "[ERROR]" + " Token length invalid.";
        assertEquals(expected, testEmptyCmd);
    }

    @Test
    public void testParsingInvalidDBName() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE #;");
        String expected = "[ERROR]" + " Invalid Database name!";
        assertEquals(expected, testEmptyCmd);
    }

    @Test
    public void testParsingInvalidTokenLen() {
        String testEmptyCmd = sendCommandToServer("CREATE DATABASE # 2 4;");
        String expected = "[ERROR]" + " Token length invalid.";
        assertEquals(expected, testEmptyCmd);
    }

    @Test
    public void testNormalCreateDbIsValid(){
        String randomName = generateRandomName();
        String response = sendCommandToServer("CREATE DATABASE " +  randomName + ";");
        assertTrue(response.contains("[OK]"));
    }

    @Test
    public void testNormalCreateDbAndUseIsValidWithCaseIns(){
        String randomName = generateRandomName();
        String randomNameWithRandomCasing = randomiseCasing(randomName);
        String response = sendCommandToServer("cReAtE dAtabasE " +  randomNameWithRandomCasing + ";");
        String expected = "[OK] " + randomNameWithRandomCasing.toLowerCase() + " Database Created";
        assertEquals(expected, response);
        String testUse = sendCommandToServer("uSe " + randomNameWithRandomCasing + ";");
        String expectedUse = "[OK] " + randomNameWithRandomCasing.toLowerCase() + " selected. USE Executed Successfully";
        assertEquals(expectedUse, testUse);
    }

    @Test
    public void testSameNowWithWrongTableSpell(){
        String randomName = generateRandomName();
        String response = sendCommandToServer("cReAtE dAtabasE " +  randomName + ";");
        assertTrue(response.contains("[OK]"));
        String wrongName = generateRandomName();
        String testUse = sendCommandToServer("uSe " + wrongName + ";");
        String expectedUseResponse = "[ERROR] [USE]:" + wrongName.toLowerCase() + " is not an existing database.";
        assertEquals(expectedUseResponse, testUse);
    }

    @Test
    public void testSameNowWithWrongCreateSpelling(){
        String randomName = "DebIaNSqlDaTabAse";
        String response = sendCommandToServer("ceAtE dAtabasE " +  randomName + ";");
        String expected = "[ERROR] [SERVER]: Empty/Invalid Command";
        assertEquals(expected, response);
    }

    @Test
    public void testSameNowWithWrongDatabaseSpelling(){
        String randomName = generateRandomName();
        String response = sendCommandToServer("cReAtE dAtaasE " +  randomName + ";");
        String expected = "[ERROR] Parsing [CREATE]: Token 'DATABASE'/'TABLE' not found!";
        assertEquals(expected, response);
    }

    @Test
    public void testCreateTableIsValid() {
        sendCommandToServer("create database NeWdB;");
        sendCommandToServer("use newdb;");
        String testEmptyCmd = sendCommandToServer("CREATE TABLE newTb;");
        assertTrue(testEmptyCmd.contains("[OK]"));
    }

    @Test
    public void testParsingCTblLowercase() {
        String testCmd = sendCommandToServer("CREATE table;");
        String expected = "[ERROR]" + " Token length invalid.";
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

}