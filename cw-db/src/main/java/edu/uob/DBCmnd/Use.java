package edu.uob.DBCmnd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Use extends Metadata implements DBCmnd {
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
        String newPath = String.valueOf(dbStore.setPathUseCmd(dbName));
        if (Files.exists(Paths.get(newPath))) {
            dbStore.currentDbPath = Path.of(newPath);
            return "[OK]" + dbName + " is an existing database. " + "USE Executed Successfully";
        }
        else {
            throw new SyntaxException(" [USE]:" + dbName + " is not an existing database.");
        }
    }
}
