package edu.uob.DBCmnd;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public String userInCmnd;
    Tokenizer tokenizer;
    public ArrayList<String> tokens;
    static int index = 0;

    public Parser(String command) throws SyntaxException {
        setTokens(command);
    }

    public void setTokens(String command) throws SyntaxException {
        if (queryEmpty(command)){
            throw new SyntaxException(" [SERVER]: Command Query is empty.");
        }
        this.tokenizer = new Tokenizer();
        this.tokenizer.query = command;
        tokenizer.setup();
        this.tokens = this.tokenizer.tokens;
        this.userInCmnd = command;
    }

    public boolean queryEmpty(String query){
        if (query == null){
            return true;
        }
        return false;
    }

    public ArrayList<String> getTokens(){
        return this.tokenizer.tokens;
    }

    public int getIndex(){
       return index;
    }

    public int incrementIndex(){
        index++;
        return getIndex();
    }

    public String getCurrentToken(){
        return tokens.get(index);
    }

    public String getNextToken(){
        incrementIndex();
        if (index < tokens.size()){
            return tokens.get(index);
        }
        return null;
    }

    public String getTokenGivenIndx(int index){
        return tokens.get(index);
    }


    public int getTokenLen() {
        return tokens.size();
    }

    public String getLastToken(){
        int tokenLen = getTokenLen();
        tokenLen--;
        if(!tokens.isEmpty() && tokenLen > 0){
            String lastTkn = tokens.get(tokenLen);
            return lastTkn;
        }
        return null;
    }

    public boolean isValidCommand(){
        String lastTkn = getLastToken();
        if (lastTkn == null){
            return false;
        }
        return ";".equals(lastTkn);
    }

    public boolean ensureCmdEnd(String lastTkn){
        if (!";".equals(lastTkn)){
            return false;
        }
        return true;
    }

    //TODO Redundant?
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
    public void firstCheck() throws SyntaxException {
        ArrayList<String> tokenChk = this.tokens;
        if(isUpperCase(getCurrentToken())){
            clear();
            throw new SyntaxException(" [SERVER]: Token cmnd NOT uppercase!");
        }
        if(!isValidCommand()){
            clear();
            throw new SyntaxException(" No ';' at end!");
        }
        index = 0;
    }

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
            if (!(isLetter(currentChar) || isDigit(currentChar))){
                return false;
            }
        }
        return true;
    }

    public boolean isSymbol(String token) {
        return token.matches("[!#$%&()*+,\\-./:;<=>?@\\[\\]\\^_`{|}~]");
    }

    //TODO Redundant?
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
        return isSpace(token) || isLetter(token) || isDigit(token) || isSymbol(token);
    }

    public boolean isStringLiteral(String token) {
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

    //TODO Method: Array<list>String as parameter as well as String
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

    public void clear() {
        this.userInCmnd = null;
        this.tokenizer = null;
        if (this.tokens != null){
            this.tokens.clear();
        }
        index = 0;
    }
}
