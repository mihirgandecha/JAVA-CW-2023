package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Drop implements DBCmnd {
    private boolean isDb = false;
    private boolean isTb = false;
    private final Metadata metadata;
    private String name;

    public Drop(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public void parse(Parser p) throws IOException {
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

    private void parseDb(Parser p) throws IOException {
        int tokenLen = p.getTokenLen();
        if (tokenLen != 4) {
            throw new SyntaxException(" Token length invalid.");
        }
        String databaseName = p.getNextToken();
        if (!p.isTbAtrDbName(databaseName)) {
            throw new SyntaxException(" Invalid Database name!");
        }
        name = databaseName;
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
            name = tableName;
        }
    }

    @Override
    public String execute(Parser p) throws IOException {
        if ((isTb && isDb) || (!isTb && !isDb)){
            throw new SyntaxException(" Error when parsing DROP");
        }
        if (isDb){
            if(metadata.currentDbPath == null) throw new SyntaxException(" 'USE' command not executed.");
            if (!metadata.isDirAtEndOfPath(name)) throw new SyntaxException(" " + name + " database does not match path given by USE: " + metadata.currentDbPath);
            //TODO consider surrounding in try catch
            metadata.dropDatabase(metadata.currentDbPath);
            return "[OK] " + name + " database successfully dropped";
        }
        if (metadata.currentDbPath == null) throw new SyntaxException(" 'USE' command not executed.");
        name = name + metadata.EXTENSION;
        Path withTbFile = Path.of(metadata.currentDbPath + File.separator + name);
        File f = new File(String.valueOf(withTbFile));
        if (!f.exists()) throw new SyntaxException(" " + name + " file does not match path given by USE: " + withTbFile);
        if (!f.delete()) throw new SyntaxException(" " + name + " Error when deleting");
        //if (!metadata.isTbAtEndOfPath(name + metadata.EXTENSION)) throw new SyntaxException(" " + name + " file does not match path given by USE: " + withTbFile);
        //if (!metadata.dropTable(withTbFile)) throw new SyntaxException(" " + name + " table could not be drooped");
        return "[OK] " + name + " table successfully dropped";
    }
}
