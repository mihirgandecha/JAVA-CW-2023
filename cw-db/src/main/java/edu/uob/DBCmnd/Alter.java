package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Alter implements DBCmnd {
    private String tbName;
    private String attrName;
    private String alterType;
    private Metadata dbStore;

    public Alter(Metadata metadata) {
        this.dbStore = metadata;
    }

    @Override
    public void parse(Parser p) throws IOException {
        String firstTkn = p.getNextToken().toLowerCase();
        if(!"table".equals(firstTkn)) throw new SyntaxException(" expected 'TABLE' token.");
        String tableNameTkn = p.getNextToken().toLowerCase();
        if(!p.isTbAtrDbName(tableNameTkn) || p.isKeyword(tableNameTkn)) throw new SyntaxException(" " + tableNameTkn + " Invalid Table Name / Cannot be SQL keyword!");
        String altTypeTkn = p.getNextToken().toUpperCase();
        if(!p.isValidAlternationType(altTypeTkn)) throw new SyntaxException(" Invalid alteration type: " + altTypeTkn);
        String attrNameTkn = p.getNextToken().toLowerCase();
        if(!p.isTbAtrDbName(attrNameTkn)) throw new SyntaxException(attrNameTkn + " is not a valid attribute name!");
        this.tbName = tableNameTkn.toLowerCase();
        this.attrName = attrNameTkn.toLowerCase();
        this.alterType = altTypeTkn.toLowerCase();
    }

    @Override
    public String execute(Parser p) throws IOException {
        if (dbStore.currentDbPath == null) {
            throw new SyntaxException(" No Database selected. USE command not implemented.");
        }
        File f = new File((dbStore.currentDbPath + File.separator + this.tbName + dbStore.EXTENSION));
        if(!f.exists()){
            throw new SyntaxException(" File does not exist!");
        }
        Path pathToTable = Path.of(dbStore.currentDbPath + File.separator + this.tbName + dbStore.EXTENSION);
        dbStore.readTbFile(pathToTable);
        if("add".equals(this.alterType)){
            dbStore.table.addColumn(this.attrName);
        }
        else if("drop".equals(this.alterType)){
            if("id".equals(this.attrName)){
                throw new SyntaxException("id cannot be dropped");
            }
            dbStore.table.removeColumn(this.attrName);
        } else {
            throw new SyntaxException(" Error occurred when executing Alter");
        }
        dbStore.table.writeTbToFile();
        return "[OK]";
    }

}
