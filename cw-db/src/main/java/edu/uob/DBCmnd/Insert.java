package edu.uob.DBCmnd;

import java.io.IOException;
import java.util.ArrayList;

public class Insert implements DBCmnd {
    private final Metadata dbStore;
    private String tableName;
    private final ArrayList<String> values = new ArrayList<>();

    public Insert(Metadata dbStore) {
        this.dbStore = dbStore;
    }

    public void parse(Parser p) throws SyntaxException, IOException {
        //p.checkTokensLen(3);
        String expectedFirstTkn = "INTO";
        String firstTkn = p.getNextToken();
        if (!expectedFirstTkn.equals(firstTkn)) {
            throw new SyntaxException("Expected INTO");
        }
        tableName = p.getNextToken();
        if (!p.isTbAtrDbName(tableName)) {
            throw new SyntaxException("Invalid Table name");
        }
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
            //TODO tokenise and type to string litteral
        }
    }

    public String execute(Parser p) throws SyntaxException, IOException {
        if (dbStore.currentDbPath == null) {
            throw new SyntaxException(" No Database selected. USE command not executed.");
        }
        if(dbStore.table == null){
            throw new SyntaxException(" Table class not instantiated");
        }
        if(values.isEmpty()) throw new SyntaxException(" No data provided for table insertation.");
        dbStore.table.addEntry(values);
        dbStore.table.writeTbToFile();
        return "[OK] Values inserted into " + tableName;
    }
}
