package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Select implements DBCmnd {
    private Metadata dbStore;
    private String tableName;
    private ArrayList<String> selectedColumns = new ArrayList<>();
    private String whereCondition = "";

    public Select(Metadata dbStore) {
        this.dbStore = dbStore;
    }

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
        String nextToken = p.getNextToken();
        while (!nextToken.equals("FROM")) {
            if (!nextToken.equals(",")) selectedColumns.add(nextToken);
            nextToken = p.getNextToken();
        }
        tableName = p.getNextToken();
        if (p.getIndex() < p.getTokenLen() - 1) {
            nextToken = p.getNextToken();
            if (nextToken.equals("WHERE")) {
                whereCondition = p.getNextToken();
                while (p.getIndex() < p.getTokenLen() - 1) {
                    whereCondition += " " + p.getNextToken();
                }
            }
        }
    }

    @Override
    public String execute(Parser p) throws SyntaxException, IOException {
        if (dbStore.table == null && dbStore.table.name != tableName) {
            throw new SyntaxException("Table " + tableName + " does not exist.");
        }
        Table table = dbStore.table;
//        if(table.columns == null) {
//            selectedColumns = table.columns;
//        }
//        else {
//            for (String column : table.columns) {
//                if (!table.columns.contains(column)) throw new SyntaxException(" " + column + " is not an attribute in the table.");
//            }
//        }
        ArrayList<String> output = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String column : selectedColumns) {
            if (!table.columns.contains(column) && !"*".equals(column)) throw new SyntaxException(column + " is not an attribute in the table.");
            line.append(column).append("\t");
        }
        output.add(line.toString().trim());
        for (Map<String, String> row : table.table) {
            line.setLength(0);
            if ("*".equals(selectedColumns.get(0))) {
                for (String value : row.values()) {
                    line.append(value).append("\t");
                }
            } else {
                for (String column : selectedColumns) {
                    line.append(row.getOrDefault(column, "NULL")).append("\t");
                }
            }
            output.add(line.toString().trim());
        }
        return String.join("\n", output);
    }
}
