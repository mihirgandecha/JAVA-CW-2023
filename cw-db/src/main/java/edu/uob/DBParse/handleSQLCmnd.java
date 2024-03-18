package edu.uob.DBParse;

import java.io.IOException;


public interface handleSQLCmnd {

    String parse(Parser p) throws SyntaxException, IOException;

}
