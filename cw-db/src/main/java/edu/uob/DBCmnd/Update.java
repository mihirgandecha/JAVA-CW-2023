package edu.uob.DBCmnd;

import java.io.IOException;

public class Update extends Metadata implements DBCmnd {
    public Update(Parser p) {
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        throw new SyntaxException("");
    }
}
