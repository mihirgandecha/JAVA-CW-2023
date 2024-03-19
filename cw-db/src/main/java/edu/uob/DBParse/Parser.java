package edu.uob.DBParse;

import edu.uob.DBDataHandling.Create;

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

    public String getCurrentToken(){
        return tokens.get(index);
    }

    public String getNextToken(){
        incrementIndex();
        return tokens.get(index);
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

    //Naming Parser Methods:

    public boolean isEmpty (String token){
        return (token == null) || token.isEmpty();
    }

    public boolean isDigit(String token){
        return token.matches("[0-9]");
    }

    public boolean isUpperCase(String token){
        return token.matches("[A-Z]");
    }

    public boolean isLowerCase(String token){
        return token.matches("[a-z]");
    }

    public boolean isLetter(String token){
        return (isUpperCase(token) || isLowerCase(token));
    }

    // TODO why isn't underscore allowed
    public boolean isPlainText(String token){
        if (isEmpty(token)){
            return false;
        }
        for (int i = 0; i < token.length(); i++){
            String currentChar = String.valueOf(token.charAt(i));
            // Check if each character is either a letter or a digit
            if (!(isLetter(currentChar) || isDigit(currentChar))){
                return false;
            }
        }
        return true;
    }

//    public boolean isPlainText(String token){
//        if (isEmpty(token)){
//            return false;
//        }
//        for (int i = 0; i < token.length(); i++){
//            String currentTChar = String.valueOf(token.charAt(i));
//            //First char only allowing letter & underscore
//            if (i == 0 && !((isLetter(currentTChar)) || "_".equals(currentTChar))){
//                return false;
//            }
//            //Allowing anything else for rest:
//            if (i > 0 && !(isLetter(currentTChar) || isDigit(currentTChar) || "_".equals(currentTChar))){
//                return false;
//            }
//        }
//        return true;
//    }



//    public boolean isPlainText(String token){
//        return token.matches(("^[a-zA-Z][a-zA-Z0-9_]*$"));
//    }

    public boolean isValidDatabaseName(String databaseName){
        return isPlainText(databaseName);
    }

    //Handling Logic:

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
        InterpretCreateCmnd(databaseName);
    }

    public void InterpretCreateCmnd(String dbName) throws SyntaxException, IOException{
        Create database = new Create(dbName);
        database.checkCreateRoot();
        if(!database.createDB()){
            throw new SyntaxException(1, "Failed to initiate:" + dbName + "at cw-db/databases/" + dbName);
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
