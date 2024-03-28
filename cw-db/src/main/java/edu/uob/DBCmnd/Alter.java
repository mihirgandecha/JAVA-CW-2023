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

    //"ALTER " "TABLE " [TableName] " " <AlterationType> " " [AttributeName]
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
        this.tbName = tableNameTkn;
        this.attrName = attrNameTkn;
        this.alterType = altTypeTkn;
    }

    @Override
    public String execute(Parser p) throws IOException {
        //Check if file exist
        if (dbStore.currentDbPath == null) {
            throw new SyntaxException(" No Database selected. USE command not implemented.");
        }
        File f = new File((dbStore.currentDbPath + File.separator + this.tbName + dbStore.EXTENSION));
        if(f.exists()){
            throw new SyntaxException(" File already exists!");
        }
        Path pathToTable = Path.of(dbStore.currentDbPath + File.separator + this.tbName + dbStore.EXTENSION);
        if(dbStore.table == null){
            dbStore.readTbFile(pathToTable);
        }
        if("add".equals(this.alterType)){
            try {
                dbStore.table.addColumn(this.attrName);
            } catch (Exception e) {
                throw new SyntaxException(" Error when adding column to table.");
            }
        }
        if("drop".equals(this.alterType)){
            if(dbStore.table.columns.get(0).matches("id")){
                throw new SyntaxException("id cannot be dropped");
            }
            try {
                dbStore.table.removeColumn(this.attrName);
            } catch (Exception e) {
                throw new SyntaxException(" Error when removing column to table");
            }
        }
        else throw new SyntaxException(" Invalid command when executing alter.");
        dbStore.table.writeTbToFile();
        return "[OK]";
    }

}
