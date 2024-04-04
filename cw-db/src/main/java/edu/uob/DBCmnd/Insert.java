package edu.uob.DBCmnd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class Insert implements DBCmnd {
    private Metadata dbStore;
    private String tableName;
    private final ArrayList<String> values = new ArrayList<>();

    public Insert(Metadata dbStore) {
        this.dbStore = dbStore;
    }

    public void parse(Parser p) throws SyntaxException {
        String intoToken = p.getNextToken();
        if (!"into".equals(intoToken.toLowerCase())) throw new SyntaxException(" expected INTO token after INSERT.");
        String tableToken = p.getNextToken();
        if (!p.isTbAtrDbName(tableToken) || p.isKeyword(tableToken)) throw new SyntaxException(" " + tableToken + " is not a valid table name!");
        tableName = tableToken.toLowerCase();
        String nextToken = p.getNextToken().toLowerCase();
        if (!"values".equals(nextToken)) {
            throw new SyntaxException(" Expected VALUES after table name.");
        }
        nextToken = p.getNextToken();
        if (!"(".equals(nextToken)) {
            throw new SyntaxException(" Expected '(' after VALUES");
        }
        if (!")".equals(p.getPenultimateToken())) {
            throw new SyntaxException(" Expected ')' after VALUES");
        }
        if (p.getTokenLen() <= 7){
            throw new SyntaxException(" No columns inside brackets or invalid token length error");
        }
        processValues(p);
    }

    private void processValues(Parser p) throws SyntaxException {
        String nextToken = p.getNextToken();
        while (!")".equals(nextToken)) {
            if (!p.isValue(nextToken)) {
                throw new SyntaxException(" Invalid value: " + nextToken);
            }
            if("NULL".equalsIgnoreCase(nextToken)){
                values.add(nextToken.toUpperCase());
            }
            else if(p.isBooleanLiteral(nextToken)){
                values.add(nextToken.toUpperCase());
            }
            else{
                values.add(nextToken);
            }
            nextToken = p.getNextToken();
            if (",".equals(nextToken)) {
                nextToken = p.getNextToken();
            }
        }
    }

    public String execute(Parser p) throws SyntaxException, FileNotFoundException {
        if (dbStore.currentDbPath == null) {
            throw new SyntaxException(" No Database selected. USE command not executed.");
        }
        File f = new File((dbStore.currentDbPath + File.separator + this.tableName + dbStore.EXTENSION));
        if(!f.exists()){
            throw new SyntaxException(" File already exists!");
        }
        Path pathToTable = Path.of(dbStore.currentDbPath + File.separator + this.tableName + dbStore.EXTENSION);
        if(dbStore.table == null){
            dbStore.readTbFile(pathToTable);
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
