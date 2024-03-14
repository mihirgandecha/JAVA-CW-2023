package edu.uob.DBParse;

public class Parser {
    Tokenizer tokenizer;

    public Parser(String userInCmnd){
        this.tokenizer = new Tokenizer();
        tokenizer.query = userInCmnd;
        tokenizer.setup();


    }

}
