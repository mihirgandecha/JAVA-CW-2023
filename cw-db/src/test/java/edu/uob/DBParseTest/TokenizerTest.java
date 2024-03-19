package edu.uob.DBParseTest;

import edu.uob.DBCmnd.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

//100% coverage tested for 2 methods

class TokenizerTest {
    private Tokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
    }

    //Test for tokenizer.setup()
    @Test
    void setupTestForWhitespace() {
        //Test for Whitespace removal:
        tokenizer.query = "   CREATE DATABASE    db     ";
        tokenizer.setup();
        List<String> expectedTokens = Arrays.asList("CREATE", "DATABASE", "db");
        assertIterableEquals(expectedTokens, tokenizer.tokens);
    }

    @Test
    void setupTestEntireCoverage() {
        //Testing Entire Coverage of method:
        tokenizer.query = "  '' INSERT INTO people VALUES( 'Simon Lock' ,35, 'simon@bristol.ac.uk' , 1.8 ) ; ";
        tokenizer.setup();
        List<String> expectedTokens = Arrays.asList("", "''", "INSERT", "INTO", "people", "VALUES", "(", "'Simon Lock'", ",", "35", ",", "'simon@bristol.ac.uk'", ",", "1.8", ")", ";");
        assertIterableEquals(expectedTokens, tokenizer.tokens);
    }

    //Test for tokenizer.tokenise()
    @Test
    void addPaddingToSpecialCharactersTest() {
        String testCmd = "(command,arg1,arg2);";
        String[] expCmd = {"(", "command", ",", "arg1", ",", "arg2", ")", ";"};
        assertArrayEquals(expCmd, tokenizer.tokenise(testCmd));
    }

    @Test
    public void removeDoubleSpacingTest() {
        String input = "command  arg1  arg2";
        String[] expectedOutput = {"command", "arg1", "arg2"};
        String[] actualOutput = tokenizer.tokenise(input);
        assertArrayEquals(expectedOutput, actualOutput);
    }

    @Test
    public void removeWhitespacesFromBeginAndEndTest() {
        String input = "  command arg1 arg2  ";
        String[] expectedOutput = {"command", "arg1", "arg2"};
        String[] actualOutput = tokenizer.tokenise(input);
        assertArrayEquals(expectedOutput, actualOutput);
    }

    @Test
    public void splitOnSpaceCharTest() {
        String input = "command arg1 arg2";
        String[] expectedOutput = {"command", "arg1", "arg2"};
        String[] actualOutput = tokenizer.tokenise(input);
        assertArrayEquals(expectedOutput, actualOutput);
    }
}