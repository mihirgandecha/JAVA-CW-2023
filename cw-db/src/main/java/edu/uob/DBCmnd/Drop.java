package edu.uob.DBCmnd;

import java.io.IOException;

public class Drop extends Metadata implements DBCmnd {
    private boolean isDb = false;
    private boolean isTb = false;
    public Drop(Parser p) {
    }

    //<Drop> ::=  "DROP " "DATABASE " [DatabaseName] | "DROP " "TABLE " [TableName]
    @Override
    public void parse(Parser p) throws SyntaxException, IOException {

        //3 token len
        //first always DROP
        //Then either db or tb
        //then name
        //Ending with ';'

        String nextToken = p.getNextToken();
        switch (nextToken) {
            case "DATABASE":
                parseDb(p);
                isDb = true;
                break;
            case "TABLE":
                parseTb(p);
                isTb = true;
                break;
            default:
                throw new SyntaxException(" Parsing [CREATE]: Token 'DATABASE'/'TABLE' not found!");
        }
    }

    private void parseDb(Parser p) throws SyntaxException, IOException {
        int tokenLen = p.getTokenLen();
        if (tokenLen != 4) {
            throw new SyntaxException(" Token length invalid.");
        }
        String databaseName = p.getNextToken();
        if (!p.isTbAtrDbName(databaseName)) {
            throw new SyntaxException(" Invalid Database name!");
        }
        //dbName = databaseName;
    }

    private void parseTb(Parser p) throws SyntaxException, IOException {
        int tokenLen = p.getTokenLen();
        if (tokenLen < 4) {
            throw new SyntaxException(" Token length invalid.");
        }
        String tableName = p.getNextToken();
        if (!p.isTbAtrDbName(tableName)) {
            throw new SyntaxException(" Invalid Table name!");
        }
        String nextTkn = p.getNextToken();
        if (p.ensureCmdEnd(nextTkn)){
            isTb = true;
            //setTbName = tableName;
            return;
        }
        else if (!"(".equals(nextTkn)) {
            throw new SyntaxException(" Token '(' not found!");
        }
        else if (!")".equals(p.getPenultimateToken())) {
            throw new SyntaxException(" Token ')' not found!");
        }
        parseTbAtrb(p);
    }

    private void parseTbAtrb(Parser p) throws SyntaxException, IOException {
        String nextTkn = p.getNextToken();
        while (nextTkn != null && !")".equals(nextTkn)) {
            if (!p.isPlainText(nextTkn)) {
                throw new SyntaxException(" Invalid Table Attribute!");
            }
            nextTkn = p.getNextToken();
            if (",".equals(nextTkn)) {
                nextTkn = p.getNextToken();
            }
            else if (!")".equals(nextTkn)){
                throw new SyntaxException(" No comma found!");
            }
        }
        p.getNextToken();
    }
    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        throw new SyntaxException("");
    }
}
