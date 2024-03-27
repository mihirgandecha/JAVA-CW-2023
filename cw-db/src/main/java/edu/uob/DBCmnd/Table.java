package edu.uob.DBCmnd;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Table {
    public String name;
    public Path dbPath;
    ArrayList<String> columns;
    private int id;
    List<Map<String, String>> table;
    public final String EXTENSION = ".tab";


    public Table(String tableName, Path filePath, ArrayList<String> tbColumns) throws SyntaxException {
        this.table = new ArrayList<>();
        this.columns = new ArrayList<>(tbColumns);
        addIdColumn();
        for (String column : tbColumns) {
            if (!this.columns.contains(column)) {
                this.columns.add(column);
            }
        }
        setPath(filePath);
        this.name = setName(tableName);
        this.id = 1;
    }

    //Ensure Table properly configured:
    public boolean isTableConfigured() {
        return this.name != null && this.dbPath != null && !this.columns.isEmpty();
    }

    //Is valid column (testing purposes):
    public boolean isValidColumnName(String columnName) {
        return this.columns.contains(columnName);
    }

    //Is valid columns (testing purposes):
    public boolean isValidColumnNames(List<String> columnNames) {
        return this.columns.containsAll(columnNames);
    }

    //do columns match (excluding 'id' column):
    public boolean isValidEntrySize(int dataSize) {
        return dataSize == this.columns.size() - 1;
    }

    //How many rows:
    public int getExtraSize() {
        return this.table != null ? this.table.size() : 0;
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

    public String setName(String tableName) throws SyntaxException {
        if(this.dbPath == null) throw new SyntaxException(" Database Path not set!");
        return tableName + EXTENSION;
    }



    public void writeTbToFile() throws SyntaxException, IOException {
        if (dbPath == null || name == null) {
            throw new SyntaxException(" Table path or name is not configured.");
        }
        File f = new File(String.valueOf(dbPath));
        if(!(f.exists() || f.isDirectory())) {
            throw new SyntaxException(" Database not found within path: " + dbPath);
        }
        Path filePath = Path.of(dbPath + File.separator + name);
        File fileToOpen = filePath.toFile();
        fileToOpen.createNewFile();
        if (!fileToOpen.isFile()) {
            throw new SyntaxException(" Cannot create or access the file at " + filePath);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToOpen))) {
            writeColumns(bw);
            writeEntries(bw);
        }
    }

    private void writeColumns(BufferedWriter bw) throws SyntaxException, IOException {
        String header = String.join("\t", columns);
        try {
            bw.write(header);
            bw.newLine();
            bw.flush();
        } catch (IOException e){
            throw new SyntaxException(" Writing to file");
        }
    }

    private void writeEntries(BufferedWriter bw) throws SyntaxException, IOException {
        for (Map<String, String> row : table) {
            List<String> rowData = new ArrayList<>();
            for (String column : columns) {
                rowData.add(row.getOrDefault(column, ""));
            }
            String rowLine = String.join("\t", rowData);
            bw.write(rowLine);
            bw.newLine();
        }
        bw.flush();
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
        Map<String, String> row = new LinkedHashMap<>();
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
}
