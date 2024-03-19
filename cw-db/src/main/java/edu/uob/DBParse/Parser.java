package edu.uob.DBParse;

import edu.uob.DBDataHandling.Create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public boolean isCmndEmpty(ArrayList<String> cmnd){
        return (cmnd == null) || cmnd.isEmpty();
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

    public boolean isSymbol(String token) {
        return token.matches("[!#$%&()*+,\\-./:;<=>?@\\[\\]\\^_`{|}~]");
    }

    //TODO Do we need? If so how can we do for taking tokenS and method (eg isSpace) as a parameter
    public boolean stringContainsSymbol(String token) {
        Pattern pattern = Pattern.compile("[!#$%&()*+,\\-./:;<=>?@\\[\\]\\^_`{|}~]");
        Matcher matcher = pattern.matcher(token);
        return matcher.find();
    }

    public boolean isSpace(String token) {
        return " ".equals(token);
    }

    public boolean isDigitSequence(String token) {
        return token.matches("\\d+");
    }

    public boolean isIntegerLiteral(String token) {
        return token.matches("[-+]?\\d+");
    }

    public boolean isFloatLiteral(String token) {
        return token.matches("[-+]?\\d+\\.\\d+");
    }

    public boolean isBooleanLiteral(String token) {
        return "TRUE".equalsIgnoreCase(token) || "FALSE".equalsIgnoreCase(token);
    }

    public boolean isCharLiteral(String token) {
        // Includes space, letters, digits, and symbols
        return isSpace(token) || isLetter(token) || isDigit(token) || isSymbol(token);
    }

    public boolean isStringLiteral(String token) {
        // Assuming string literals are enclosed in single quotes for this context
        if (!token.startsWith("'") || !token.endsWith("'")) return false;
        String innerContent = token.substring(1, token.length() - 1);
        for (int i = 0; i < innerContent.length(); i++) {
            String currentChar = String.valueOf(innerContent.charAt(i));
            if (!isCharLiteral(currentChar) && !currentChar.equals("'")) { // Allow single quotes within string
                return false;
            }
        }
        return true;
    }

    public boolean isValue(String token) {
        return isStringLiteral(token) || isBooleanLiteral(token) || isFloatLiteral(token) || isIntegerLiteral(token) || "NULL".equals(token);
    }

    public boolean isTbAtrDbName(String token){
        return isPlainText(token);
    }

    public boolean isWildAttribList(String token) {
        if ("*".equals(token)) {
            return true;
        }
        return isTbAtrDbName(token);
    }

    public boolean isAttributeList(String token) {
        String[] attributes = token.split(",");
        for (String attribute : attributes) {
            if (!isPlainText(attribute.trim())) {
                return false;
            }
        }
        return true;
    }

    private boolean isCondition(ArrayList<String> expression) {
        for (int i = 0; i < expression.size(); i++) {
            String token = expression.get(i);
            if (!(isValidBoolOperator(token) || isValidComparator(token) || isValue(token) || isTbAtrDbName(token))) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidBoolOperator(String token) {
        return token.matches("AND|OR");
    }

    public boolean isValidComparator(String token) {
        return token.matches("==|>|<|>=|<=|!=|LIKE");
    }



    public void handleCreateCommand() throws SyntaxException, IOException{
        if (isCmndEmpty(tokens) || checkTokensLen(4)) {
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
        if (!isTbAtrDbName(databaseName)) {
            throw new SyntaxException(1,"CREATE command syntax error. Database name not plain text.");
        }
        cmndToken = getNextToken();
        expectedToken = ";";
        if (!expectedToken.equals(cmndToken)) {
            throw new SyntaxException(1,"CREATE command syntax error. No ';' token found.");
        }
        InterpretCreateCmnd(databaseName);
    }


//    public boolean handleCondition() throws SyntaxException {
//        if (tokens.isEmpty() || !tokens.contains("WHERE")) {
//            throw new SyntaxException(1, "No 'WHERE' clause found.");
//        }
//        index = tokens.indexOf("WHERE") + 1;
//        ArrayList<String> conditionExpression = new ArrayList<>(tokens.subList(index, tokens.size()));
//        return isCondition(conditionExpression);
//    }

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
