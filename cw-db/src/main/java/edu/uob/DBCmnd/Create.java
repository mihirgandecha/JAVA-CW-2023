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
            throw new SyntaxException(1, "Command Empty or missing ';' at end.");
        }
        int tokenLen = p.getTokenLen();
        if (tokenLen < 4) {
            throw new SyntaxException(1, "Command length too short");
        }
        String createToken = p.getCurrentToken();
        String createExpectedToken = "CREATE";
        if (!createExpectedToken.equals(createToken)) {
            throw new SyntaxException(1, "CREATE command syntax error. No 'CREATE' token found.");
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
                throw new SyntaxException(1, "Expected 'DATABASE' or 'TABLE' after 'CREATE'");
        }
    }

    private void parseDb(Parser p) throws SyntaxException, IOException {
        String databaseName = p.getNextToken();
        if (!p.isTbAtrDbName(databaseName)) {
            throw new SyntaxException(1, "CREATE command syntax error. Database name not plain text.");
        }
        String lastTkn = p.getLastToken();
        if (!p.ensureCmdEnd(lastTkn)) {
            throw new SyntaxException(1, "';' Token not found.");
        }
        dbName = databaseName;
    }

    private void parseTb(Parser p) throws SyntaxException, IOException {
        String tableName = p.getNextToken();
        if (!p.isTbAtrDbName(tableName)) {
            throw new SyntaxException(1, "CREATE command syntax error. Table name not plain text.");
        }
        parseTbAtrb(p);

//        String nextTkn = p.getNextToken();
//        if (!p.ensureCmdEnd(nextTkn)) {
//            throw new SyntaxException(1, "Error.");
//        }
    }

    private void parseTbAtrb(Parser p) throws SyntaxException, IOException {
        String firstBrkt = p.getNextToken();
        if (!"(".equals(firstBrkt)) {
            throw new SyntaxException(1, "Opening bracket missing.");
        }
        String nextToken = p.getNextToken();
        while (nextToken != null && !")".equals(nextToken)) {
            if (!p.isPlainText(nextToken)) {
                throw new SyntaxException(1, "Invalid Attribute List");
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
//        Database d = new Database();
        dbStore.setDbName(dbName);
        dbStore.setPath();
        if (isDb) {
            if (!dbStore.createDB()) {
                throw new SyntaxException(1, "Failed to initiate:" + dbName + "at cw-db/databases/" + dbName);
            }
        }
        if (isTb){
            return "[OK]";
        }
        isTb = false;
        isDb = false;
        return "[OK]";
    }
}
