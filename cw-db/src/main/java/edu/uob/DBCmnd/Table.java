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
                this.columns.add(column.toLowerCase());
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

    public void setPath(Path usePath){
        this.dbPath = usePath;
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
    public void addColumnToArray(String colName){
        this.columns.add(colName);
    }

    //Add ID column to start
    public void addIdColumn(){
        if (!columns.contains("id")){
            columns.add(0, "id");
        }
    }

    public void addColumn(String colName) throws IOException {
        colName = colName.toLowerCase();
        if (this.columns.contains(colName)) {
            throw new SyntaxException("Column " + colName + " already exists.");
        }
        addColumnToArray(colName);
        for (Map<String, String> row : this.table) {
            row.put(colName, "");
        }
    }

    public void removeColumn(String colName) throws IOException {
        colName = colName.toLowerCase();
        if ("id".equals(colName)) {
            throw new SyntaxException("Cannot remove the 'id' column.");
        }
        if (!this.columns.contains(colName)) {
            throw new SyntaxException("Column " + colName + " does not exist.");
        }
        this.columns.remove(colName);
        for (Map<String, String> row : this.table) {
            row.remove(colName);
        }
    }

    public void addEntry(ArrayList<String> entry) throws SyntaxException {
        if (this.table == null) {
            this.table = new ArrayList<>();
        }
//        boolean containsIgnoreCase = columns.stream().anyMatch(s -> s.equalsIgnoreCase("NULL"));
//        if (containsIgnoreCase) {
//            int columnIndexOfNull = columns.indexOf("NULL");
//            if (!entry.get(columnIndexOfNull).equalsIgnoreCase("NULL")) {
//                entry.add(columnIndexOfNull, "NULL");
//            }
//        }
//        if(columns.contains("NULL")){
//            int columnIndexOfNull = columns.indexOf("NULL");
//            if(entry.get(columnIndexOfNull) != "NULL"){
//                entry.add(columnIndexOfNull, "NULL");
//            }
//        }
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

    public void addEntryForReading(ArrayList<String> entry) throws SyntaxException {
        if (this.table == null) {
            this.table = new ArrayList<>();
        }

        Map<String, String> row = new LinkedHashMap<>();
        row.put("id", Integer.toString(id++));
        for (int i = 0; i < entry.size(); i++) {
            row.put(columns.get(i + 1), entry.get(i));
        }

        for (String column : columns) {
            if (!row.containsKey(column)) {
                row.put(column, "NULL");
            }
        }

        this.table.add(row);
    }

    public String displayTableToString() {
        ArrayList<Integer> maxWidths = new ArrayList<>();
        for (String column : this.columns) {
            int maxWidth = column.length();
            for (Map<String, String> row : this.table) {
                String value = row.getOrDefault(column, "NULL");
                if (value.length() > maxWidth) {
                    maxWidth = value.length();
                }
            }
            maxWidths.add(maxWidth);
        }
        StringBuilder line = new StringBuilder();
        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < this.columns.size(); i++) {
            line.append(String.format("%-" + maxWidths.get(i) + "s", this.columns.get(i))).append(" ");
        }
        output.add(line.toString().trim());
        for (Map<String, String> row : this.table) {
            line.setLength(0);
            for (int i = 0; i < this.columns.size(); i++) {
                String column = this.columns.get(i);
                String value = row.getOrDefault(column, "NULL");
                line.append(String.format("%-" + maxWidths.get(i) + "s", value)).append(" ");
            }
            output.add(line.toString().trim());
        }
        return "[OK]\n" + String.join("\n", output);
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
