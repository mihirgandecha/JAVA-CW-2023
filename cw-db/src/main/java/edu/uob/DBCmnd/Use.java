package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Use implements DBCmnd {
    private String dbName = null;
    private Metadata dbStore;

    public Use(Metadata metadata) {
        this.dbStore = metadata;
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
        p.checkTokensLen(3);
        String firstTkn = p.getCurrentToken();
        String expectedFirstTkn = "USE";
        if (!expectedFirstTkn.equals(firstTkn)){
            throw new SyntaxException("");
        }
        String dbNameTkn = p.getNextToken();
        if (!p.isTbAtrDbName(dbNameTkn)) {
            throw new SyntaxException("");
        }
        dbName = dbNameTkn;
    }

    @Override
    public String execute(Parser p) throws IOException, SyntaxException {
        Path newPath = Path.of(dbStore.storagePath + File.separator + dbName);
        if (Files.exists(newPath)) {
            dbStore.dbName = dbName;
            dbStore.currentDbPath = newPath;
            return "[OK] " + dbName + " selected. " + "USE Executed Successfully";
        }
        else {
            throw new SyntaxException(" [USE]:" + dbName + " is not an existing database.");
        }
    }
}
