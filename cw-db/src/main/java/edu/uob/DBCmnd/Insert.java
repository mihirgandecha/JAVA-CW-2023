package edu.uob.DBCmnd;

import java.io.IOException;

public class Insert implements DBCmnd {
    public Insert(Parser p) {
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {

    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        throw new SyntaxException("");
    }
}
