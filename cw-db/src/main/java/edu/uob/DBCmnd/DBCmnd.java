package edu.uob.DBCmnd;

import java.io.IOException;


public interface DBCmnd {
    void parse(Parser p) throws SyntaxException, IOException;
    String execute() throws SyntaxException, IOException;
}
