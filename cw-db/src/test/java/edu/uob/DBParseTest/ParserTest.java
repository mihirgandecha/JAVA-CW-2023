package edu.uob.DBParseTest;

import edu.uob.DBParse.Parser;
import edu.uob.DBParse.Tokenizer;
import edu.uob.ExampleDBTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    String query;

//    @BeforeEach
//    void initiateParser(){
//        Parser testParser = new Parser(query);
//    }

//    @Test
//    void testFromExampleDBParsedOK(){
//        //Testing CREATE DATABASE 100 times
//        Parser testP = new Parser();
//        for (int i = 0; i < 100; i++){
//            String randomName = ExampleDBTests.generateRandomName();
//            testP.userInCmnd = "CREATE DATABASE " + randomName + ";";
//            assertTrue(testP.Parser());
//        }
//        testP.userInCmnd = "CREATE TABLE marks (name, mark, pass);";
//        assertTrue(testP.Parser());
//    }

    @Test
    void testCreateNewDatabase() {
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.query = "CREATE DATABASE db;";
        tokenizer.setup();
        List<String> expectedTokens = Arrays.asList("CREATE", "DATABASE", "db", ";");
        assertIterableEquals(expectedTokens, tokenizer.tokens);

        //Check that Parser does same:
        String query = "CREATE DATABASE db;";
        Parser parser = new Parser(query);
        assertIterableEquals(expectedTokens, parser.getTokens());

    }


}