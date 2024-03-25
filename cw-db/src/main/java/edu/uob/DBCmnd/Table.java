package edu.uob.DBCmnd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Table {
    public String name;
    public Path dbPath;
    ArrayList<String> columns;
    private int id;
    private List<Map<String, String>> table;
    public final String FEXTENSION = ".tab";


    public Table(ArrayList<String> tbColumns){
        this.columns = new ArrayList<>(tbColumns);
        addIdColumn();
        for (String column : tbColumns) {
            if (!this.columns.contains(column)) {
                this.columns.add(column);
            }
        }
        this.id = 1;
        this.table = new ArrayList<>();
        //this.columns.addAll(tbColumns);
    }

    public void setName(String tablename){
        this.name = tablename;
    }

    public String getName(){
        return name;
    }

    public void setPath(Path usePath){
        this.dbPath = usePath;
    }

    public String getDbPath(){
        return String.valueOf(this.dbPath);
    }

    public String setFilePath() throws SyntaxException {
        if(this.dbPath == null) throw new SyntaxException(" Database Path not set!");
        if(this.name == null) throw new SyntaxException(" Table name not set!");
        return name + FEXTENSION;
    }

    public void writeTbToFile(String dirPath, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(dirPath + fileName));
        String column = String.join("\t", columns);
        try {
            writer.write(column);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writer.close();
        }
    }

    // Get the current columns
    public ArrayList<String> getColumns() {
        return new ArrayList<>(this.columns);
    }

    //Add single column:
    public void addColumn(String colName){
        this.columns.add(colName);
    }

    //Add all columns from tbColumns
    public void addAllColumns(ArrayList<String> newColumns) {
        for (String column : newColumns) {
            if (!this.columns.contains(column)) {
                this.columns.add(column);
            }
        }
    }

    //Add ID column to start
    public void addIdColumn(){
        if (!columns.contains("id")){
            columns.add(0, "id");
        }
    }

    public void insertRow(Map<String, String> rowData) throws Exception {
        for (String column : columns) {
            if (!column.equals("id") && !rowData.containsKey(column)) {
                throw new Exception("Missing data for column: " + column);
            }
        }
        rowData.put("id", Integer.toString(id++));
        if (this.table == null) {
            this.table = new ArrayList<>();
        }
        this.table.add(new HashMap<>(rowData));
    }

    private void getColumns(ArrayList<String> createTbColumns) {
        this.columns = createTbColumns;
    }

    private void clear() {
        this.name = null;
        this.dbPath = null;
        this.columns.clear();
        this.id = 1;
        this.table.clear();
    }

    public void addEntry(ArrayList<String> entry) throws SyntaxException {
        if (this.table == null) {
            this.table = new ArrayList<>();
        }
        if (entry.size() != columns.size() - 1) {
            throw new SyntaxException("Incorrect number of values. Expected: " + (columns.size() - 1) + ", but received: " + entry.size());
        }
        Map<String, String> row = new HashMap<>();
        row.put("id", Integer.toString(id++));
        for (int i = 0; i < entry.size(); i++) {
            row.put(columns.get(i + 1), entry.get(i));
        }
        this.table.add(row);
    }

    @Override
    public String toString() {
        Map<String, Integer> columnWidths = new HashMap<>();
        for (String column : columns) {
            columnWidths.put(column, Math.max(column.length(), "NULL".length()));
        }
        StringBuilder builder = new StringBuilder();
        // Header and Divider
        builder.append("+");
        columns.forEach(column -> builder.append(String.format("%-" + (columnWidths.get(column) + 2) + "s+", "").replace(" ", "-")));
        builder.append("\n| ");
        // Column Names
        columns.forEach(column -> builder.append(String.format("%-" + columnWidths.get(column) + "s | ", column)));
        builder.append("\n+");
        columns.forEach(column -> builder.append(String.format("%-" + (columnWidths.get(column) + 2) + "s+", "").replace(" ", "-")));
        // Row Data
        table.forEach(row -> {
            builder.append("\n| ");
            columns.forEach(column -> builder.append(String.format("%-" + columnWidths.get(column) + "s | ", row.getOrDefault(column, "NULL"))));
        });
        if (table.isEmpty()) {
            builder.append("\n+");
            columns.forEach(column -> builder.append(String.format("%-" + (columnWidths.get(column) + 2) + "s+", "").replace(" ", "-")));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        Table myTable = new Table(new ArrayList<>(Arrays.asList("name", "mark", "pass")));
        ArrayList<String> newRowData = new ArrayList<>(Arrays.asList("Simon", "98", "True"));
        ArrayList<String> newRowData2 = new ArrayList<>(Arrays.asList("Mark", "33", "False"));
        try {
            myTable.addEntry(newRowData);
            myTable.addEntry(newRowData2);
            System.out.println(myTable);
        } catch (Exception e) {
            System.err.println("Error adding data: " + e.getMessage());
        }
        myTable.clear();
    }
}
