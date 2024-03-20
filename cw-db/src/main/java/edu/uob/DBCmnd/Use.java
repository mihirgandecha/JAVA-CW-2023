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
            throw new SyntaxException(1, "Expected 'USE' command as first token");
        }
        String dbNameTkn = p.getNextToken();
        if (!p.isTbAtrDbName(dbNameTkn)) {
            throw new SyntaxException(1,"CREATE command syntax error. Database name not plain text.");
        }
        String lastTkn = p.getLastToken();
        if (!p.ensureCmdEnd(lastTkn)){
            throw new SyntaxException(1, "';' Token not found.");
        }
        dbName = dbNameTkn;
    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        String newPath = String.valueOf(dbStore.setPathUseCmd(dbName));
        if (Files.exists(Paths.get(newPath))) {
            dbStore.currentDbPath = Path.of(newPath);
            return "[OK]";
        } else {
            throw new SyntaxException(1, "");
        }
    }
}
