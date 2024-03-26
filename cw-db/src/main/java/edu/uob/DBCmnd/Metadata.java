package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Metadata {
    public String dbName;
    public Path storagePath;
    public Path dbPath;
    public String tbName;
    public Path currentDbPath;
    public final String EXTENSION = ".tab";
    public Table table;

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
        boolean createExecuted = Files.createDirectories(dbPath).toFile().exists();
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

//    public boolean dropDatabase(String dbName) {
//        Path databasePath = Paths.get("databases", dbName).toAbsolutePath();
//        File databaseDir = databasePath.toFile();
//        if (databaseDir.exists() && databaseDir.isDirectory()) {
//            try {
//                deleteDirectoryRecursively(databaseDir);
//                return true;
//            } catch (IOException e) {
//                return false;
//            }
//        }
//        return false;
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
