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

    public void setStoragePath(String storagePathFromServer) {
        storagePath = Paths.get(storagePathFromServer);
    }

    public boolean isDatabasesDirPresent(){
        return Files.exists(getAbsPath("databases"));
    }

    public void setDbName(String dbToken) throws IOException {
        if (dbToken == null){
            throw new IOException("dbName is empty!");
        }
        dbName = dbToken.toLowerCase();
    }

    public void setPath() throws IOException {
        dbPath = Paths.get("databases", dbName).toAbsolutePath();
        if (!checkCreateRoot()){
            throw new IOException("[ERROR]");
        }
    }

    public Path getAbsPath(String directory){
        return Paths.get(directory).toAbsolutePath();
    }

    public boolean isDirAtEndOfPath(String dirName) {
        Path lastPartOfPath = currentDbPath.getFileName();
        return lastPartOfPath != null && lastPartOfPath.toString().equals(dirName);
    }

    //Check if exists -> cw/db/databases
    public boolean checkCreateRoot() {
        if (!isDatabasesDirPresent()){
            createDir();
            return true;
        }
        else return isDatabasesDirPresent();
    }

    //Creates directory
    public boolean createDir() {
        File f = new File(String.valueOf(dbPath));
        return f.mkdir();
    }

    //Check if cw/databases/<DATABASE_NAME> already exists
    private boolean isDBPresent(){
        return Files.exists(dbPath);
    }

    //Check if exists -> Creates cw/databases/<DATABASE_NAME>
    public boolean createDB() {
        if (!isDBPresent()) {
            return createDir();
        }
        return false;
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
        this.table = new Table(tbName, this.currentDbPath, columnsRead);
        while(tableLines != null && !tableLines.isEmpty()){
            ArrayList<String> readEntry = readColumns(tableLines);
            if(readEntry != null){
                if (!readEntry.isEmpty()) {
                    readEntry.remove(0);
                }
                if (!tableLines.isEmpty()) {
                    tableLines.remove(0);
                }
                if(readEntry != null){
                    this.table.addEntryForReading(readEntry);
                }
            }
        }
    }

    private ArrayList<String> readColumns(ArrayList<String> readLines) {
        if(!readLines.isEmpty()) {
            String[] columnsToRead = readLines.get(0).split("\\t");
            return new ArrayList<>((Arrays.asList(columnsToRead)));
        }
        return null;
    }

    //Working!
    public void dropDatabase(Path databasePath) throws IOException {
        Files.walkFileTree(databasePath, new SimpleFileVisitor<>() {
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

}
