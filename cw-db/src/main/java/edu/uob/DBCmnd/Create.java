package edu.uob.DBCmnd;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Create implements DBCmnd {
    private boolean isDb = false;
    private boolean isTb = false;
    private static String dbName = null;
    private Database dbStore;

    public Create(Database dbStore) {
        this.dbStore = dbStore;
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
        if (p.isCmndEmpty(p.tokens) || (!p.isValidCommand())) {
            throw new SyntaxException("");
        }
        int tokenLen = p.getTokenLen();
        if (tokenLen < 4) {
            throw new SyntaxException("");
        }
        String createToken = p.getCurrentToken();
        String createExpectedToken = "CREATE";
        if (!createExpectedToken.equals(createToken)) {
            throw new SyntaxException("");
        }
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
                throw new SyntaxException("");
        }
    }

    private void parseDb(Parser p) throws SyntaxException, IOException {
        String databaseName = p.getNextToken();
        if (!p.isTbAtrDbName(databaseName)) {
            throw new SyntaxException("");
        }
        String lastTkn = p.getLastToken();
        if (!p.ensureCmdEnd(lastTkn)) {
            throw new SyntaxException("");
        }
        dbName = databaseName;
    }

    private void parseTb(Parser p) throws SyntaxException, IOException {
        String tableName = p.getNextToken();
        if (!p.isTbAtrDbName(tableName)) {
            throw new SyntaxException("");
        }
        parseTbAtrb(p);
    }

    private void parseTbAtrb(Parser p) throws SyntaxException, IOException {
        String firstBrkt = p.getNextToken();
        if (!"(".equals(firstBrkt)) {
            throw new SyntaxException("");
        }
        String nextToken = p.getNextToken();
        while (nextToken != null && !")".equals(nextToken)) {
            if (!p.isPlainText(nextToken)) {
                throw new SyntaxException("");
            }
            nextToken = p.getNextToken();
            if (",".equals(nextToken)) {
                nextToken = p.getNextToken();
            }
        }
        p.getNextToken();

    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        dbStore.setDbName(dbName);
        dbStore.setPath();
        if (isDb) {
            if (!dbStore.createDB()) {
                dbStore.dbPath = null;
                dbStore.dbName = null;
                throw new SyntaxException("");
            }
            isDb = false;
            p.clear();
            return "[OK]" + dbName + " Database Created";
        }
        if (isTb){
            isTb = false;
            p.clear();
            return "[OK]" + " Table created";
        }
        dbStore.dbPath = null;
        dbStore.dbName = null;
        p.clear();
        throw new SyntaxException("");
    }
}
