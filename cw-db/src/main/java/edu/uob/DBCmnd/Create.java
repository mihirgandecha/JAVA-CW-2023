package edu.uob.DBCmnd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Create implements DBCmnd {
    private boolean isDb = false;
    private boolean isTb = false;
    private static String dbName = null;
    private static String setTbName = null;
    private Metadata dbStore;

    public Create(Metadata dbStore) {
        this.dbStore = dbStore;
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
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
        dbName = databaseName;
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
            setTbName = tableName;
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
        if (isDb) {
            dbStore.setDbName(dbName);
            dbStore.setPath();
            if (!dbStore.createDB()) {
                dbStore.dbPath = null;
                dbStore.dbName = null;
                throw new SyntaxException(" Executing [CREATE]/[DATABASE]: Database already existing!");
            }
            isDb = false;
            p.clear();
            return "[OK]" + dbName + " Database Created";
        }
        if (isTb) {
            if (dbStore.currentDbPath == null) {
                throw new SyntaxException(" No Database selected. USE command not implemented.");
            }
            //TODO ! Reforactor just instantiating Table after path confirmed.
            //TODO Check if file already present in directory given tbName
            String dirPath = String.valueOf(dbStore.currentDbPath) + File.separator;
            String fileName = dbStore.tbName + dbStore.FEXTENSION;
            //TODO check for if Windows works:
            dbStore.tbFile = new BufferedWriter(new FileWriter(dirPath + fileName));
            dbStore.isFileCreated = true;
            dbStore.tbAttributes = new ArrayList<>();
            if (dbStore.tbAttributes.isEmpty()) {
                checkAtribContainsID();
                dbStore.tbAttributes.add(0, "id");
            }
            isTb = false;
            p.clear();
            dbStore.tbName = setTbName;
            return "[OK]" + " " + dbStore.tbName + " Table created.";
        }
        dbStore.dbPath = null;
        dbStore.dbName = null;
        p.clear();
        throw new SyntaxException(" Executing [CREATE]/[TABLE]: Table execution invalid!");
    }

    private void checkAtribContainsID() throws SyntaxException {
        for(String idAtr : dbStore.tbAttributes){
            if(isId(idAtr)){
                throw new SyntaxException("attribute name cannot be 'id'");
            }
        }
    }

    public static boolean isId(String input) {
        return "ID".equalsIgnoreCase(input);
    }
}
