package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Drop implements DBCmnd {
    private boolean isDb = false;
    private boolean isTb = false;
    private final Metadata dbStore;
    private String name;

    public Drop(Metadata metadata) {
        this.dbStore = metadata;
    }

    @Override
    public void parse(Parser p) throws IOException {
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
                throw new SyntaxException(" Parsing [DROP]: Token 'DATABASE'/'TABLE' not found!");
        }
    }

    private void parseDb(Parser p) throws IOException {
        int tokenLen = p.getTokenLen();
        if (tokenLen != 4) {
            throw new SyntaxException(" Token length invalid.");
        }
        String databaseName = p.getNextToken();
        if (!p.isTbAtrDbName(databaseName)) {
            throw new SyntaxException(" Invalid Database name!");
        }
        name = databaseName.toLowerCase();
    }

    private void parseTb(Parser p) throws IOException {
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
            name = tableName.toLowerCase();
        }
    }

    @Override
    public String execute(Parser p) throws IOException {
        if ((isTb && isDb) || (!isTb && !isDb)){
            throw new SyntaxException(" DROP should be towards table or database, not both or neither.");
        }
        if (isDb){
            Path dropPath = Path.of(dbStore.storagePath + File.separator + name);
            if (Files.exists(dropPath)) {
                dbStore.dropDatabase(dropPath);
                return "[OK] " + name + " database successfully dropped";
            } else{
                throw new SyntaxException(" " + name + " cannot be dropped as it is not an initiated database.");
            }
        }
        if (dbStore.currentDbPath == null) throw new SyntaxException(" 'USE' command not executed.");
        name = name + dbStore.EXTENSION;
        Path withTbFile = Path.of(dbStore.currentDbPath + File.separator + name);
        File f = new File(String.valueOf(withTbFile));
        if (!f.exists()) throw new SyntaxException(" " + name + " file does not match path given by USE: " + withTbFile);
        if (!f.delete()) throw new SyntaxException(" " + name + " Error when deleting");
        return "[OK] " + name + " table successfully dropped";
    }
}
