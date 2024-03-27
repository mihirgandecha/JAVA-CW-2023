package edu.uob.DBCmnd;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Metadata {
    public String dbName;
    public Path storagePath;
    public Path dbPath;
    public String tbName;
    public Path currentDbPath;
    public final String EXTENSION = ".tab";
    public Table table;
    public boolean tbHasEntries = false;
    public int maxEntryRows;

    public void setStoragePath(String storagePathFromServer) {
        if (storagePath != null) {
            return;
        }
        Path keepFilePath = Paths.get(storagePathFromServer, ".keep");
        if (Files.exists(keepFilePath)) {
            storagePath = Paths.get(storagePathFromServer);
        }
    }

    public boolean isDatabasesDirPresent(){
        return Files.exists(getAbsPath("databases"));
    }

    public boolean isTbPresent(){
        return table.isTableConfigured();
    }

    public void setDbName(String dbToken) throws IOException {
        if (dbToken == null){
            throw new IOException("dbName is empty!");
        }
        dbName = dbToken;
    }

    public void setPath() throws IOException {
        dbPath = Paths.get("databases", dbName).toAbsolutePath();
        if (!checkCreateRoot()){
            throw new IOException("[ERROR]");
        }
    }

    public Path setPathUseCmd(String useCmdPath) throws IOException {
        Path usePath = Paths.get("cw-db","databases", useCmdPath).toAbsolutePath();
        if (!checkCreateRoot()){
            throw new IOException("[ERROR]");
        }
        return usePath;
    }

    public Path getAbsPath(String directory){
        return Paths.get(directory).toAbsolutePath();
    }

    public boolean isDirAtEndOfPath(String dirName) {
        Path lastPartOfPath = currentDbPath.getFileName();
        return lastPartOfPath != null && lastPartOfPath.toString().equals(dirName);
    }

    public boolean isTbAtEndOfPath(String filename) {
        Path fileNamePath = currentDbPath.getFileName();
        return fileNamePath != null && fileNamePath.toString().equals(filename + EXTENSION);
    }

    //Check if exists -> cw/db/databases
    public boolean checkCreateRoot() throws IOException {
        if (!isDatabasesDirPresent()){
            createDir();
            return true;
        }
        else return isDatabasesDirPresent();
    }

    //Creates directory
    public boolean createDir() throws IOException{
        File f = new File(String.valueOf(dbPath));
        boolean createExecuted = f.mkdir();
        return createExecuted;
    }

    //Check if cw/databases/<DATABASE_NAME> already exists
    private boolean isDBPresent(){
        boolean isDBPres = Files.exists(dbPath);
        return isDBPres;
    }

    public boolean isDbPresentUseCmd(String dbToken){
        boolean isDatabasePresent = Files.exists(Path.of(dbToken).toAbsolutePath());
        return isDatabasePresent;
    }

    //Check if exists -> Creates cw/databases/<DATABASE_NAME>
    public boolean createDB() throws IOException {
        if (!isDBPresent()) {
            return createDir();
        }
        return false;
    }

    //Testing methods:

    public String pathToString(String directory){
        return getAbsPath(directory).toString();
    }

    //Returns UNIX+Windows compatible path
    public String getCompatiblePath(String directory){
        return directory + File.separator;
    }

    public void readTbFile(Path filePathToRead) throws SyntaxException, FileNotFoundException {
        File tabFile = new File(String.valueOf(filePathToRead));
        if(!tabFile.exists()){
            throw new SyntaxException(" Cannot find path" + String.valueOf(filePathToRead) + " when reading file.");
        }
        ArrayList<String> tableLines = new ArrayList<>();
        FileReader fileReader = new FileReader(tabFile);
        BufferedReader br = new BufferedReader(fileReader);
        try {
            String tbLine = br.readLine();
            while(tbLine != null){
                tableLines.add(tbLine);
                tbLine = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> columnsRead = readColumns(tableLines);
        columnsRead.remove(Arrays.asList("id"));
        if (columnsRead != null){
            if(!tableLines.isEmpty()){
                tableLines.remove(0);
            }
        }
        else{
            throw new SyntaxException(" columns could not be read");
        }
        columnsRead.remove("id");
        System.out.println(columnsRead);
        this.table = new Table("newTb", this.storagePath, columnsRead);
        while(!tableLines.isEmpty()){
            ArrayList<String> readEntry = readColumns(tableLines);
            readEntry.remove(0);
            if (!tableLines.isEmpty()) {
                tableLines.remove(0);
            }
            this.table.addEntry(readEntry);
        }
    }

    public static void main(String[] args) throws IOException{
        Metadata metadata = new Metadata();
        metadata.setStoragePath("/home/mihirgany/IdeaProjects/JAVA-CW-2023/cw-db/databases");
        if(metadata.table == null){
            metadata.readTbFile(Path.of(metadata.storagePath + File.separator + "newdatab" + File.separator + "newTb.tab"));
        }
        System.out.println(metadata.table);
    }

    private ArrayList<String> readColumns(ArrayList<String> readLines) throws SyntaxException {
        if(!readLines.isEmpty()) {
            String[] columnsToRead = readLines.get(0).split("\\t");
            return new ArrayList<>((Arrays.asList(columnsToRead)));
//            for (String i : readLines) {
//                if (i.contains("id")) {
//                    return new ArrayList<String>(Arrays.(columnsToRead));
//                }
//            }
        }
        return null;
    }

    private ArrayList<String> readEntries(ArrayList<String> readLines) throws SyntaxException {
        if(!readLines.isEmpty()) {
            String columnsToRead = Arrays.toString(readLines.get(0).split("\\t"));
            for (String i : readLines) {
                return new ArrayList<>(Arrays.asList(columnsToRead));
            }
        }
        return null;
    }

//    private List<Map<String, String>> readEntries(List<String> tableLines) {
//        List<Map<String, String>> table = new ArrayList<>();
//        for (int i = 1; i < tableLines.size(); i++) {
//            String[] entries = tableLines.get(i).split("\\t");
//            Map<String, String> row = new HashMap<>();
//            for (int j = 0; j < columns.size(); j++) {
//                row.put(columns.get(j), entries[j]);
//            }
//            table.add(row);
//        }
//        return table;
//    }

//    private void readEntries(ArrayList<String> listOfLines){
//        for(int i=1; i<listOfLines.size(); i++){
//            table.addEntry(listOfLines.get(i).split("\\t"));
//        }
//    }

    //Working!
    public void dropDatabase(Path databasePath) throws IOException {
        Files.walkFileTree(databasePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    //Helper method to dropDatabase
//    private void deleteDirectoryRecursively(File dir) throws IOException {
//        File[] allContents = dir.listFiles();
//        if (allContents != null) {
//            for (File file : allContents) {
//                deleteDirectoryRecursively(file);
//            }
//        }
//        Files.delete(dir.toPath());
//    }


//    public boolean dropTable(String tableName) {
//        Path tablePath = currentDbPath.resolve(tableName + ".tab");
//        try {
//            if (Files.exists(tablePath)) {
//                Files.delete(tablePath);
//                return true;
//            }
//        } catch (IOException ignored) {
//        }
//        return false;
//    }
    public boolean dropTable(Path tablePath) throws IOException {
        if (Files.exists(tablePath) && !Files.isDirectory(tablePath)) {
            Files.delete(tablePath);
            return true;
        } else {
            System.out.println("Specified path does not exist or is a directory.");
            return false;
        }
    }
}
