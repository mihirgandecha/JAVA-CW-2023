package edu.uob.DBCmnd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Metadata {
    public String dbName;
    public Path dbPath;
    public String tbName;
    public Path currentDbPath;
    public BufferedWriter tbFile;
    public final String FEXTENSION = ".tab";
    public boolean isFileCreated = false;
    public ArrayList<String> tbAttributes;

    //Check if cw-db/databases is present
    public boolean isDatabasesDirPresent(){
        boolean isDatabasesExists = Files.exists(getAbsPath("databases"));
        return isDatabasesExists;
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
        Path usePath = Paths.get("databases", useCmdPath).toAbsolutePath();
        if (!checkCreateRoot()){
            throw new IOException("[ERROR]");
        }
        return usePath;
    }

    public Path getAbsPath(String directory){
        return Paths.get(directory).toAbsolutePath();
    }

    //Check if exists -> cw/db/databases
    public boolean checkCreateRoot() throws IOException {
        if (!isDatabasesDirPresent()){
            createDir();
            return true;
        }
        else if (isDatabasesDirPresent()){
            return true;
        }
        return false;
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

    public boolean isDbPresentUseCmnd(String dbToken){
        boolean isdbTknPres = Files.exists(Path.of(dbToken).toAbsolutePath());
        return isdbTknPres;
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
    public String convertToPlatformIndependant(String directory){
        return directory + File.separator;
    }

    //Deletion:
    // Deletes an empty directory at the specified path within the root directory
    public boolean deleteEmptyDir(String directoryName) throws IOException {
        Path dPath = getAbsPath("databases" + File.separator + directoryName);
        if (!Files.exists(dPath)){
            return false;
        }
        return Files.deleteIfExists(dPath);
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
