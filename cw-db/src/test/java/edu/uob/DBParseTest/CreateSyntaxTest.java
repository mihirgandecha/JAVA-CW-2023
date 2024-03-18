package edu.uob.DBParseTest;

import edu.uob.DBParse.Parser;
import edu.uob.DBParse.SyntaxException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CreateSyntaxTest {

    @Test
    void testCreate(){
        Parser p = new Parser("CREATE DATABASE");
        assertThrows(IOException.class, () -> p.parse());
    }

}