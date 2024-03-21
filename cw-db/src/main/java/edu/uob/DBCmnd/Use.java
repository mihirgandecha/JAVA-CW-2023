package edu.uob.DBCmnd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Use extends Database implements DBCmnd {
    private String dbName = null;
    private Database dbStore;

    public Use(Database database) {
        this.dbStore = database;
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
        p.firstCheck();
        p.checkTokensLen(3);
        String firstTkn = p.getCurrentToken();
        String expectedFirstTkn = "USE";
        if (!expectedFirstTkn.equals(firstTkn)){
            throw new SyntaxException(1);
        }
        String dbNameTkn = p.getNextToken();
        if (!p.isTbAtrDbName(dbNameTkn)) {
            throw new SyntaxException(1);
        }
        String lastTkn = p.getLastToken();
        if (!p.ensureCmdEnd(lastTkn)){
            throw new SyntaxException(1);
        }
        dbName = dbNameTkn;
    }

    @Override
    public String execute(Parser p) throws IOException {
        try {
            String newPath = String.valueOf(dbStore.setPathUseCmd(dbName));
            if (Files.exists(Paths.get(newPath))) {
                dbStore.currentDbPath = Path.of(newPath);
                return "[OK]" + dbName + " is an existing database. " + "USE Executed Successfully";
            } else {
                throw new SyntaxException(1);
            }
        } catch (SyntaxException se) {
            System.err.println(se.getErrorTag());
            throw se;
        }
    }
}
