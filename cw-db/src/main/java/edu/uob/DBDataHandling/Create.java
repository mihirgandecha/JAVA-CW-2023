package edu.uob.DBDataHandling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Create {
    private String dbName;
    private final String rootDir = "cw-db" + File.separator + "databases";

    public Create(String createDbDirName){
        this.dbName = createDbDirName;
    }

    //Check if cw-db/databases is present
    private boolean isRootPresent(){
        return Files.exists(getAbsPath("databases"));
    }

    //Check if exists -> cw/db/databases
    public boolean checkCreateRoot() throws IOException {
        if (!isRootPresent()){
            return createDir("databases");
        }
        return false;
    }

    //Creates directory
    public boolean createDir(String directory) throws IOException{
        return Files.createDirectories(getAbsPath(directory)).isAbsolute();
    }

    //Check if cw/databases/<DATABASE_NAME> already exists
    private boolean isDBPresent(){
        return Files.exists(getAbsPath(rootDir));
    }

    //Check if exists -> Creates cw/databases/<DATABASE_NAME>
    public boolean createDB() throws IOException {
        if (!isDBPresent()) {
            return createDir(rootDir);
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

}
