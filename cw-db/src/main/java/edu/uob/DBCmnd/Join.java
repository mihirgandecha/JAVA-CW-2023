package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;


public class Join implements DBCmnd {
    private Metadata dbStore;
    private String table1;
    private String table2;
    private Path tableOnePath;
    private Path tableTwoPath;
    private String attributeTableOne;
    private String attributeTableTwo;
    private Table t1;
    private Table t2;

    public Join(Metadata metadata) {
        this.dbStore = metadata;
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
        String firstTkn = p.getCurrentToken();
        if(!firstTkn.equalsIgnoreCase("JOIN")){
            throw new SyntaxException(" 'JOIN' token not found");
        }
        String tableName = p.getNextToken();
        if(!p.isTbAtrDbName(tableName) || p.isKeyword(tableName)){
            throw new SyntaxException(" Table name invalid!");
        }
        if(dbStore.currentDbPath == null) throw new SyntaxException(" USE command not implemented!");
        this.tableOnePath = Path.of(dbStore.currentDbPath + File.separator + tableName.toLowerCase() + dbStore.EXTENSION);
        File f = new File(String.valueOf(this.tableOnePath));
        if (!f.exists()) throw new SyntaxException(" " + tableName + " file1 does not exist in path for JOIN: " + dbStore.currentDbPath);
        this.table1 = tableName;
        //'AND'
        String andTkn = p.getNextToken();
        if(!andTkn.equalsIgnoreCase("AND")){
            throw new SyntaxException(" 'AND' token not found");
        }
        String tableName2 = p.getNextToken();
        if(!p.isTbAtrDbName(tableName2) || p.isKeyword(tableName2)){
            throw new SyntaxException(" " + tableName2 + " Table2 name invalid!");
        }
        this.table2 = tableName2.toLowerCase();
        this.tableTwoPath = Path.of(dbStore.currentDbPath + File.separator + this.table2 + dbStore.EXTENSION);
        File f2 = new File(String.valueOf(this.tableTwoPath));
        if (!f2.exists()) throw new SyntaxException(" " + tableName + " file2 does not exist in path for JOIN: " + dbStore.currentDbPath);
        //'ON'
        String onTkn = p.getNextToken();
        if(!onTkn.equalsIgnoreCase("ON")){
            throw new SyntaxException(" 'ON' token not found");
        }
        //Atrib Name 1
        String atribOne = p.getNextToken();
        if(!p.isTbAtrDbName(atribOne) || p.isKeyword(atribOne)) throw new SyntaxException(" Invalid Atrib1 Name!");
        this.attributeTableOne = atribOne;
        if(dbStore.table == null || dbStore.table.name != this.table1){
            dbStore.readTbFile(this.tableOnePath);
        }
        this.t1 = dbStore.table;
        ArrayList<String> col1 = dbStore.table.getColumns();
        if (!col1.contains(this.attributeTableOne)) throw new SyntaxException(" Attribute One does not exist in table file: " + this.table1);
        //'AND2'
        String and2Tkn = p.getNextToken();
        if(!and2Tkn.equalsIgnoreCase("AND")){
            throw new SyntaxException(" 'AND' token not found");
        }
        //Atrib Name 2
        String atribTwo = p.getNextToken();
        if(!p.isTbAtrDbName(atribTwo) || p.isKeyword(atribTwo)) throw new SyntaxException(" Invalid Atrib1 Name!");
        this.attributeTableTwo = atribTwo.toLowerCase();
        if(dbStore.table == null || dbStore.table.name != this.table2){
            dbStore.readTbFile(this.tableTwoPath);
        }
        this.t2 = dbStore.table;
        ArrayList<String> col2 = dbStore.table.getColumns();
        if (!col2.contains(this.attributeTableTwo)) throw new SyntaxException(" Attribute One does not exist in table file: " + this.table1);
    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        ArrayList<String> joinColumnNames = getJoinColumnNames();
        Table joinTable = executeJoin(joinColumnNames);
        return joinTable.displayTableToString();
    }

    private ArrayList<String> getJoinColumnNames() {
        ArrayList<String> joinColumnNames = new ArrayList<>();

        t1.getColumns().stream()
                .filter(column -> !column.equals("id"))
                .forEach(column -> joinColumnNames.add(table1 + "." + column));


        t2.getColumns().stream()
                .filter(column -> !column.equals("id"))
                .forEach(column -> joinColumnNames.add(table2 + "." + column));
        return joinColumnNames;
    }

    private Table executeJoin(ArrayList<String> joinColumnNames) throws SyntaxException {
        Table joinTable = new Table("join_" + table1 + "_" + table2, dbStore.currentDbPath, joinColumnNames);
        for (Map<String, String> row1 : t1.table) {
            for (Map<String, String> row2 : t2.table) {
                if (row1.get(attributeTableOne).equals(row2.get(attributeTableTwo))) {
                    ArrayList<String> values = collectRowValues(row1, row2);
                    joinTable.addEntry(values);
                }
            }
        }
        return joinTable;
    }

    private ArrayList<String> collectRowValues(Map<String, String> row1, Map<String, String> row2) {
        ArrayList<String> values = new ArrayList<>();
        t1.getColumns().stream()
                .filter(column -> !column.equals("id"))
                .forEach(column -> values.add(row1.get(column)));

        t2.getColumns().stream()
                .filter(column -> !column.equals("id"))
                .forEach(column -> values.add(row2.get(column)));
        return values;
    }

}
