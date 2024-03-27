package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Use implements DBCmnd {
    private String dbName = null;
    private final Metadata dbStore;

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
    public String execute(Parser p) throws SyntaxException, IOException {
        File f = new File(dbStore.storagePath + File.separator + dbName);
        if (f.exists() && f.isDirectory()) {
            dbStore.currentDbPath = Path.of(dbStore.storagePath + File.separator + dbName);
            return "[OK] " + dbName + " is an existing database. " + "USE Executed Successfully";
        }
        else {
            throw new SyntaxException(" [USE]:" + dbName + " is not an existing database.");
        }
    }
}
