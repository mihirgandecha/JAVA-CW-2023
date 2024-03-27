package edu.uob.DBCmnd;

import java.io.IOException;
import java.util.ArrayList;

public class Insert implements DBCmnd {
    private Metadata dbStore;
    private String tableName;
    private final ArrayList<String> values = new ArrayList<>();

    public Insert(Metadata dbStore) {
        this.dbStore = dbStore;
    }

    public void parse(Parser p) throws SyntaxException {
        //TODO check that expected token len >= ?
        String expectedFirstTkn = "INTO";
        String firstTkn = p.getNextToken();
        if (!expectedFirstTkn.equals(firstTkn)) {
            throw new SyntaxException("Expected INTO");
        }
        String tableToken = p.getNextToken();
        if (!p.isTbAtrDbName(tableToken)) throw new SyntaxException(" " + tableToken + " is not a valid table name!");
        tableName = tableToken.toLowerCase();
        String nextToken = p.getNextToken();
        if (!"VALUES".equals(nextToken)) {
            throw new SyntaxException("Expected VALUES after table name");
        }
        nextToken = p.getNextToken();
        if (!"(".equals(nextToken)) {
            throw new SyntaxException("Expected '(' after VALUES");
        }
        nextToken = p.getNextToken();
        while (!")".equals(nextToken)) {
            if (!p.isValue(nextToken)) {
                throw new SyntaxException("Invalid value: " + nextToken);
            }
            values.add(nextToken);
            nextToken = p.getNextToken();
            if (",".equals(nextToken)) {
                nextToken = p.getNextToken();
            }
        }
        parseValueType(p);

    }

    public void parseValueType(Parser p) throws SyntaxException {
        for (String token : values) {
            if (!(p.isStringLiteral(token) || p.isBooleanLiteral(token) || p.isFloatLiteral(token) || p.isIntegerLiteral(token) || "NULL".equals(token))) {
                throw new SyntaxException(" Invalid row token: " + token);
            }
        }
    }

    public String execute(Parser p) throws SyntaxException {
        if (dbStore.currentDbPath == null) {
            throw new SyntaxException(" No Database selected. USE command not executed.");
        }
        if(dbStore.table == null){
            throw new SyntaxException(" Table class not instantiated");
        }
        if(values.isEmpty()) throw new SyntaxException(" No data provided for table insertion.");
        dbStore.table.addEntry(values);
        try {
            dbStore.table.writeTbToFile();
        } catch (IOException e) {
            throw new SyntaxException(" Error when writing to " + tableName + ".tab file.");
        }
        return "[OK] Values inserted into " + tableName;
    }
}
