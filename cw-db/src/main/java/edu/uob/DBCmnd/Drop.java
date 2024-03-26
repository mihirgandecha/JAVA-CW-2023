package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Drop extends Metadata implements DBCmnd {
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
        if (isDb && !isTb){
            if(metadata.currentDbPath == null) throw new SyntaxException(" 'USE' command not executed.");
            if (!metadata.isDirAtEndOfPath(name)) throw new SyntaxException(" " + name + " database does not match path given by USE: " + metadata.currentDbPath);
            if (!metadata.dropDatabase(name)) throw new SyntaxException(" " + name + " database could not be drooped");
            metadata.dropDatabase(name);
            return "[OK] " + name + " database successfully dropped";
        }
        if (!isDb && isTb){
            if(metadata.currentDbPath == null) throw new SyntaxException(" 'USE' command not executed.");
            Path withTbFile = Path.of(metadata.currentDbPath + File.separator + name);
            if (!metadata.isTbAtEndOfPath(name)) throw new SyntaxException(" " + name + " file does not match path given by USE: " + withTbFile);
            if (!metadata.dropTable(name)) throw new SyntaxException(" " + name + " table could not be drooped");
            return "[OK] " + name + " table successfully dropped";
        }
        else{
            throw new SyntaxException(" Error when executing DROP command!");
        }
    }
}
