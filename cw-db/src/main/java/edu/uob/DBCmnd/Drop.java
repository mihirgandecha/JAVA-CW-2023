package edu.uob.DBCmnd;

import java.io.IOException;

public class Drop extends Database implements DBCmnd {
    public Drop(Parser p) {
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {

    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        throw new IOException("[ERROR]");
    }
}
