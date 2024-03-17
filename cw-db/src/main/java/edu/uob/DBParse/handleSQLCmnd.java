package edu.uob.DBParse;

import java.io.IOException;
import java.util.*;


public interface handleSQLCmnd {

    void parse(Parser p) throws SyntaxException, IOException;

}
