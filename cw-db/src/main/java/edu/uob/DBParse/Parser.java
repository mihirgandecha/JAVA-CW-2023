package edu.uob.DBParse;

import java.io.IOException;
import java.util.ArrayList;

public class Parser implements handleSQLCmnd{
    public String userInCmnd;
    Tokenizer tokenizer;
    public ArrayList<String> tokens;
    static int index = 0;

    public Parser(String command) {
        setTokens(command);
    }

    public void setTokens(String userInCmnd){
        this.tokenizer = new Tokenizer();
        this.tokenizer.query = userInCmnd;
        tokenizer.setup();
        this.tokens = this.tokenizer.tokens;
    }

    public ArrayList<String> getTokens(){
        return this.tokenizer.tokens;
    }

    public int getIndex(){
       return index;
    }

    public int incrementIndex(){
        return index++;
    }

    public boolean isTokensEmpty (){
        if (tokens.isEmpty()){
            return true;
        }
        return false;
    }

    public String getCurrentToken(){
        return tokens.get(index);
    }

    public String getNextToken(){
        incrementIndex();
        return tokens.get(index);
    }

    public boolean isUpperCase(String token){
        if (token.equals(token.toUpperCase())){
            return true;
        }
        return false;
    }

    public boolean isSpecialCharacter(String token) {
        String[] specialCharacters = {"(",")",",",";"};
        for (String specialChar : specialCharacters) {
            if (token.contains(specialChar)) {
                return true;
            }
        }
        return false;
    }
    public boolean checkTokensLen(int expectedLen){
        if(tokens.size() != expectedLen){
            return true;
        }
        return false;
    }

    @Override
    public String parse() throws SyntaxException, IOException {
        String tokenCmnd = getCurrentToken();
        try{
            if (tokenCmnd.contains("CREATE")){
                handleCreateCommand();
            }
        } catch (SyntaxException e){
            if("[ERROR]".equals(e.getErrorTag())){
                throw new IOException(" Error during Parsing: " + e.getErrorMsg());
            }
        }
        return "[OK]";
    }

    public boolean isPlainText(String token){
        return token.matches(("^[a-zA-Z][a-zA-Z0-9_]*$"));
    }

    public boolean isValidDatabaseName(String databaseName){
        return isPlainText(databaseName);
    }

    public void handleCreateCommand() throws SyntaxException, IOException{
        if (isTokensEmpty() || checkTokensLen(4)) {
            throw new SyntaxException(1, "CREATE command syntax error. Bad token len.");
        }
        String cmndToken = getCurrentToken();
        String expectedToken = "CREATE";
        if (!expectedToken.equals(cmndToken)) {
            throw new SyntaxException(1, "CREATE command syntax error. No 'CREATE' token found.");
        }
        cmndToken = getNextToken();
        expectedToken = "DATABASE";
        if (!expectedToken.equals(cmndToken)) {
            throw new SyntaxException(1,"CREATE command syntax error. No 'DATABASE' token found.");
        }
        String databaseName = getNextToken();
        if (!isValidDatabaseName(databaseName)) {
            throw new SyntaxException(1,"CREATE command syntax error. Database name not plain text.");
        }
        cmndToken = getNextToken();
        expectedToken = ";";
        if (!expectedToken.equals(cmndToken)) {
            throw new SyntaxException(1,"CREATE command syntax error. No ';' token found.");
        }
    }

    public void clear() {
        this.userInCmnd = null;
        this.tokenizer = null;
        if (this.tokens != null){
            this.tokens.clear();
        }
        index = 0;
    }

}
