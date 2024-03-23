package edu.uob.DBCmnd;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Create implements DBCmnd {
    private boolean isDb = false;
    private boolean isTb = false;
    private static String dbName = null;
    private static String setTbName = null;
    private Database dbStore;

    public Create(Database dbStore) {
        this.dbStore = dbStore;
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
//        int tokenLen = p.getTokenLen();
//        if (tokenLen < 4) {
//            throw new SyntaxException(" Token length invalid.");
//        }
        String nextToken = p.getNextToken();
        switch (nextToken) {
            case "DATABASE":
                parseDb(p);
                isDb = true;
                break;
            case "TABLE":
                parseTb(p);
                //isTb = true;
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
        parseTbAtrb(p);
    }

    private void parseTbAtrb(Parser p) throws SyntaxException, IOException {
        String tableNameTkn= p.getNextToken();
        while (tableNameTkn!= null && !")".equals(tableNameTkn)) {
            if (!p.isPlainText(tableNameTkn)) {
                throw new SyntaxException(" Invalid Table Attribute!");
            }
            String nextToken = p.getNextToken();
            if (",".equals(nextToken )) {
                nextToken = p.getNextToken();
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
            return "[OK]" + " Table created.";
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
