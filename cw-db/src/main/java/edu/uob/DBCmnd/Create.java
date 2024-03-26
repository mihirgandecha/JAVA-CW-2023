package edu.uob.DBCmnd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Create implements DBCmnd {
    private boolean isDb = false;
    private boolean isTb = false;
    private static String dbName = null;
    private static String setTbName = null;
    private Metadata dbStore;
    private int expectedColLen = 0;
    private ArrayList<String> columns;

    public Create(Metadata dbStore) {
        this.dbStore = dbStore;
        columns = new ArrayList<String>();
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
        setTbName = tableName;
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

    private void parseTbAtrb(Parser p) throws SyntaxException, IOException {
        String nextTkn = p.getNextToken();
        while (nextTkn != null && !")".equals(nextTkn)) {
            if (!p.isPlainText(nextTkn)) {
                throw new SyntaxException(" Invalid Table Attribute!");
            }
            expectedColLen++;
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
            return "[OK]" + dbName + " Database Created";
        }
        if (isTb) {
            if (dbStore.currentDbPath == null) {
                throw new SyntaxException(" No Database selected. USE command not implemented.");
            }
            return createTb(p, dbStore);
        }
        dbStore.dbPath = null;
        dbStore.dbName = null;
        p.clear();
        throw new SyntaxException(" Executing [CREATE]/[TABLE]: Table execution invalid!");
    }

    private String createTb(Parser p, Metadata dbStore) throws SyntaxException {
        //TODO ! Reforactor just instantiating Table after path confirmed.
        //TODO Check if file already present in directory given tbName
        Table table = new Table(setTbName, dbStore.currentDbPath, columns);
        if (!table.isTableConfigured()){
            throw new SyntaxException(" Table configured incorrectly.");
        }
        ArrayList<String> newRowData = new ArrayList<>(Arrays.asList("Simon", "98", "True"));
        ArrayList<String> newRowData2 = new ArrayList<>(Arrays.asList("Mark", "33", "False"));
        table.addEntry(newRowData);
        table.addEntry(newRowData2);
        try {
            table.writeTbToFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        isTb = false;
        p.clear();
        dbStore.tbName = setTbName;
        return "[OK]" + " " + dbStore.tbName + " Table created.";
    }

    public void writeTbToFile(Metadata dbStore) throws IOException {
        String dirPath = String.valueOf(dbStore.currentDbPath) + File.separator;
        String fileName = setTbName + dbStore.FEXTENSION;
        //TODO check for if Windows works:
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

//    private void checkAtribContainsID() throws SyntaxException {
//        for(String idAtr : dbStore.tbAttributes){
//            if(isId(idAtr)){
//                throw new SyntaxException("attribute name cannot be 'id'");
//            }
//        }
//    }

    public static boolean isId(String input) {
        return "ID".equalsIgnoreCase(input);
    }
}
