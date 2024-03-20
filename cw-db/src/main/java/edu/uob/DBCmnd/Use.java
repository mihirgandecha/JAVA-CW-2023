package edu.uob.DBCmnd;

import java.io.IOException;

public class Use extends Database implements DBCmnd {
    public static String dbName;

    public void Use (String dbTkn){
        dbName = dbTkn;
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
//        setDb(dbNameTkn);
    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {

        return "[OK]";
    }

//    private void setDb(String dbNameTkn) {
//        dbName = dbNameTkn;
//    }

}
