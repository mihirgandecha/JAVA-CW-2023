package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Database {
    protected String dbName;
    protected Path dbPath;
    // private final String rootDir;

//    public Database(){
//        this.rootDir = "databases";
//        this.dbPath = Paths.get("cw-db", "databases", dbName);
//    }

    //Check if cw-db/databases is present
    public boolean isRootPresent(){
        return Files.exists(getAbsPath("databases"));
    }

    public void setDbName(String dbToken){
        dbName = dbToken;
    }

    //Check if exists -> cw/db/databases
    public void checkCreateRoot() throws IOException {
        if (!isRootPresent()){
            createDir("databases");
        }
    }

    //Creates directory
    public boolean createDir(String directory) throws IOException{
        return Files.createDirectories(getAbsPath(directory)).isAbsolute();
    }

    //Check if cw/databases/<DATABASE_NAME> already exists
    private boolean isDBPresent(){
        return Files.exists(getAbsPath(String.valueOf(dbPath)));
    }

    //Check if exists -> Creates cw/databases/<DATABASE_NAME>
    public boolean createDB() throws IOException {
        if (!isDBPresent()) {
            return createDir(String.valueOf(dbPath));
        }
        return false;
    }

    //Testing methods:
    public Path getAbsPath(String directory){
        return Paths.get(directory).toAbsolutePath();
    }

    public String pathToString(String directory){
        return getAbsPath(directory).toString();
    }

    //Returns UNIX+Windows compatible path
    public String convertToPlatformIndependant(String directory){
        return directory + File.separator;
    }

    //Deletion:
    // Deletes an empty directory at the specified path within the root directory
    public boolean deleteEmptyDir(String directoryName) throws IOException {
        Path dirPath = Paths.get(String.valueOf(dbPath), directoryName);
        try {
            return Files.deleteIfExists(dirPath);
        } catch (SyntaxException e) {
            System.out.println("Directory is not empty.");
            return false;
        }
    }

    // Deletes a directory and all its contents
    public void deleteSpecificDir(String directoryName) throws IOException {
        Path dirPath = Paths.get(String.valueOf(dbPath), directoryName);
        deleteDirectoryRecursively(dirPath.toFile());
    }

    // Helper method to delete a directory recursively
    private void deleteDirectoryRecursively(File dir) throws IOException {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        Files.delete(dir.toPath());
    }

}
