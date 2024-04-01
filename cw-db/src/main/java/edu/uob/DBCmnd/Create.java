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
    private static String tbName = null;
    private final Metadata dbStore;
    private ArrayList<String> columns;

    public Create(Metadata dbStore) {
        this.dbStore = dbStore;
        columns = new ArrayList<>();
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
        String nextToken = p.getNextToken();
        switch (nextToken.toUpperCase()) {
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
        if(p.isKeyword(databaseName)){
            throw new SyntaxException(" " + databaseName + " cannot be same as SQL keyword!");
        }
        dbName = databaseName.toLowerCase();
    }

    private void parseTb(Parser p) throws SyntaxException, IOException {
        int tokenLen = p.getTokenLen();
        if (tokenLen < 4) {
            throw new SyntaxException(" Token length invalid.");
        }
        String tableName = p.getNextToken();
        if (!p.isTbAtrDbName(tableName.toLowerCase())) {
            throw new SyntaxException(" Invalid Table name!");
        }
        if(p.isKeyword(tableName)){
            throw new SyntaxException(" table name cannot be condition!");

        }
        tbName = tableName.toLowerCase();
        String nextTkn = p.getNextToken();
        if (p.ensureCmdEnd(nextTkn)){
            isTb = true;
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

    public void parseTbAtrb(Parser p) throws SyntaxException, IOException {
        String nextTkn = p.getNextToken();
        while (nextTkn.toLowerCase() != null && !")".equals(nextTkn)) {
            if (!p.isAttributeList(nextTkn)) {
                throw new SyntaxException(" Invalid Table Attribute!");
            }
            if (p.isKeyword(nextTkn)){
                throw new SyntaxException(" " + nextTkn + " Attribute cannot be same as SQL keyword!");
            }
            columns.add(nextTkn);
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
            return "[OK] " + dbName + " Database Created";
        }
        if (isTb) {
            if (dbStore.currentDbPath == null) {
                throw new SyntaxException(" No Database selected. USE command not implemented.");
            }
            File f = new File((dbStore.currentDbPath + File.separator + tbName + dbStore.EXTENSION));
            if(f.exists()){
                throw new SyntaxException(" File already exists!");
            }
            return createTb(p, dbStore);
        }
        dbStore.dbPath = null;
        dbStore.dbName = null;
        p.clear();
        throw new SyntaxException(" Executing [CREATE]/[TABLE]: Table execution invalid!");
    }

    private String createTb(Parser p, Metadata dbStore) throws SyntaxException {
        columns.replaceAll(String::toLowerCase);
        Table table = new Table(tbName, dbStore.currentDbPath, columns);
        if (!table.isTableConfigured()){
            throw new SyntaxException(" Table configured incorrectly.");
        }
        try {
            table.writeTbToFile();
        } catch (IOException e) {
            throw new SyntaxException(" Failed to create table!");
        }
        dbStore.table = table;
        isTb = false;
        p.clear();
        dbStore.tbName = tbName;
        return "[OK]" + " " + dbStore.tbName + " Table created.";
    }

    public void writeTbToFile(Metadata dbStore) throws IOException {
        String dirPath = String.valueOf(dbStore.currentDbPath) + File.separator;
        String fileName = tbName + dbStore.EXTENSION;
        BufferedWriter writer = new BufferedWriter(new FileWriter(dirPath + fileName));
        String column = String.join("\t", columns);
        try {
            writer.write(column);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writer.close();
        }
    }

}
