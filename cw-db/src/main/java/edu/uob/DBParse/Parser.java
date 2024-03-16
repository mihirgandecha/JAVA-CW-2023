package edu.uob.DBParse;

import java.util.ArrayList;

public class Parser {
    public String userInCmnd;
    Tokenizer tokenizer;
    int index;

    public void setTokens(String userInCmnd){
        this.tokenizer = new Tokenizer();
        tokenizer.query = userInCmnd;
        tokenizer.setup();
    }

    public ArrayList<String> getTokens(){
        return tokenizer.tokens;
    }

}
