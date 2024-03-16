package edu.uob.DBParseTest;

import edu.uob.DBParse.Parser;
import edu.uob.DBParse.SyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateSyntaxTest {

    @Test
    void testCreate(){
        Parser p = new Parser();

        p.setTokens("CREATE DATABASE");
        assertThrows(SyntaxException.class, () -> p.parse(p));

    }

}