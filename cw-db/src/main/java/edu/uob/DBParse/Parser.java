package edu.uob.DBParse;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser implements handleSQLCmnd{
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

    public void checkIfUppercase(List<String> singleToken) throws SyntaxException{
        String[] specialCharacters = {"(",")",",",";"};
        for (String token : singleToken) {
            if (helperIsSpecialCharacter(token, specialCharacters)) {
                continue;
            }
            if (!token.equals(token.toUpperCase())) {
                throw new SyntaxException("[ERROR]", "lowercase detected");
            }
        }
    }

    static boolean helperIsSpecialCharacter(String token, String[] specialCharacters) {
        for (String specialChar : specialCharacters) {
            if (token.equals(specialChar)) {
                return true;
            }
        }
        return false;
    }

    //eg handle("COMMAND")
    //return error
    @Override
    public void parse(Parser p) throws SyntaxException, Exception {
        String toString = p.tokenizer.tokens.toString();
        if (toString.contains("CREATE")){
            CreateSyntax createSyntax = new CreateSyntax();
            createSyntax.parse(p);
        }
        else{
            throw new Exception("[ERROR]");
        }
    }
}
