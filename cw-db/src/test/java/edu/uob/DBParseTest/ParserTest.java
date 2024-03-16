package edu.uob.DBParseTest;

import edu.uob.DBParse.Parser;
import edu.uob.ExampleDBTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    String query;

//    @BeforeEach
//    void initiateParser(){
//        Parser testParser = new Parser(query);
//    }

    @Test
    void testFromExampleDBParsedOK(){
        //Testing CREATE DATABASE 100 times
        Parser testP = new Parser();
        for (int i = 0; i < 100; i++){
            String randomName = ExampleDBTests.generateRandomName();
            testP.userInCmnd = "CREATE DATABASE " + randomName + ";";
            assertTrue(testP.Parser());
        }
        testP.userInCmnd = "CREATE TABLE marks (name, mark, pass);";
        assertTrue(testP.Parser());
    }


}