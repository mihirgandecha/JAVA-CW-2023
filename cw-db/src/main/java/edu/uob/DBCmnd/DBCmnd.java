package edu.uob.DBCmnd;

import java.io.IOException;


public interface DBCmnd {

    // each cmnd should end in ';'
    void parse(Parser p) throws SyntaxException, IOException;
    String execute(Parser p) throws SyntaxException, IOException;
}
