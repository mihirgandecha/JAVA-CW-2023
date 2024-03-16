package edu.uob.DBParse;

public class Parser extends handleSQLCmnd {
    public String userInCmnd;
    Tokenizer tokenizer;

    public boolean Parser(){
        this.tokenizer = new Tokenizer();
        tokenizer.query = userInCmnd;
        tokenizer.setup();
        return true;

    }

}
