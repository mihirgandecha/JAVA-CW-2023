package edu.uob.DBParse;

public class Parser {
    public String userInCmnd;
    Tokenizer tokenizer;

    public boolean Parser(){
        this.tokenizer = new Tokenizer();
        tokenizer.query = userInCmnd;
        tokenizer.setup();
        return true;

    }

}
