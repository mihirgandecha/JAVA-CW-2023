package edu.uob.DBParseTest;

import edu.uob.DBCmnd.Parser;
import edu.uob.DBCmnd.SyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Parser p;

    @BeforeEach
    void setUp() throws SyntaxException {
        String command = "TEMP COMMAND;";
        p = new Parser(command);
    }

    @AfterEach
    void tearDown() {
        p.clear();
    }

    //Test for DB-Name:
    @Test
    void testValidDatabaseNameWithLettersOnly() {
        assertTrue(p.isTbAtrDbName("DatabaseName"));
    }

    @Test
    void testValidDatabaseNameWithDigitsOnly() {
        assertTrue(p.isTbAtrDbName("12345"));
    }

    @Test
    void testValidDatabaseNameWithLettersAndDigits() {
        assertTrue(p.isTbAtrDbName("Db1Name2"));
    }

    @Test
    void testDatabaseNameStartingWithDigit() {
        assertTrue(p.isTbAtrDbName("1stDatabase"));
    }

    //Invalid:
    @Test
    void testDatabaseNameWithUnderscore() {
        assertFalse(p.isTbAtrDbName("my_database"));
    }

    @Test
    void testDatabaseNameWithHyphen() {
        assertFalse(p.isTbAtrDbName("my-database"));
    }

    @Test
    void testDatabaseNameWithSpace() {
        assertFalse(p.isTbAtrDbName("my database"));
    }

    @Test
    void testDatabaseNameWithSymbol() {
        assertFalse(p.isTbAtrDbName("name!"));
    }

    @Test
    void testDatabaseNameWithLeadingDigit() {
        assertTrue(p.isTbAtrDbName("1name"));
    }

    @Test
    void testEmptyDatabaseName() {
        assertFalse(p.isTbAtrDbName(""));
    }

    @Test
    void testDatabaseNameWithSpecialCharacters() {
        assertFalse(p.isTbAtrDbName("database!"));
    }

    @Test
    void testDatabaseNameWithDot() {
        assertFalse(p.isTbAtrDbName("name.name"));
    }

    @Test
    void testDatabaseNameWithSingleQuote() {
        assertFalse(p.isTbAtrDbName("name'"));
    }

    @Test
    void testDatabaseNameWithUnicodeCharacters() {
        assertFalse(p.isTbAtrDbName("数据库"));
    }

    @Test
    void testDatabaseNameWithSpecialCharacterCombination() {
        assertFalse(p.isTbAtrDbName("!@#$%"));
    }

    @Test
    void testDatabaseNameWithNewLineCharacter() {
        assertFalse(p.isTbAtrDbName("name\n"));
    }

    @Test
    void testDatabaseNameWithTabCharacter() {
        assertFalse(p.isTbAtrDbName("name\t"));
    }
}