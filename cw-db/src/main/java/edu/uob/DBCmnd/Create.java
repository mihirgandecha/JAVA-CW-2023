package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Create implements DBCmnd {
    private static String dbName;
    private static String rootDir;

    public void setDbName(String dbName) {
        dbName = dbName;
        rootDir = "cw-db" + File.separator + "databases" + File.separator + dbName;
    }

    //Check if cw-db/databases is present
    private boolean isRootPresent(){
        return Files.exists(getAbsPath("databases"));
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

    //Deletion:
    // Deletes an empty directory at the specified path within the root directory
    public boolean deleteEmptyDir(String directoryName) throws IOException {
        Path dirPath = Paths.get(rootDir, directoryName);
        try {
            return Files.deleteIfExists(dirPath);
        } catch (SyntaxException e) {
            System.out.println("Directory is not empty.");
            return false;
        }
    }

    // Deletes a directory and all its contents
    public void deleteSpecificDir(String directoryName) throws IOException {
        Path dirPath = Paths.get(rootDir, directoryName);
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

    @Override
    public void parse(Parser p) throws SyntaxException, IOException {
        if (p.isCmndEmpty(p.tokens) || p.checkTokensLen(4)) {
            throw new SyntaxException(1, "CREATE command syntax error. Bad token len.");
        }
        String createToken = p.getCurrentToken();
        String createExpectedToken = "CREATE";
        if (!createExpectedToken.equals(createToken)) {
            throw new SyntaxException(1, "CREATE command syntax error. No 'CREATE' token found.");
        }
        String dbToken = p.getNextToken();
        String dbExpectedToken = "DATABASE";
        if (!dbExpectedToken.equals(dbToken)) {
            throw new SyntaxException(1,"CREATE command syntax error. No 'DATABASE' token found.");
        }
        String databaseName = p.getNextToken();
        if (!p.isTbAtrDbName(databaseName)) {
            throw new SyntaxException(1,"CREATE command syntax error. Database name not plain text.");
        }
        String symbolToken = p.getNextToken();
        String symExpectedToken = ";";
        if (!symExpectedToken.equals(symbolToken)) {
            throw new SyntaxException(1,"CREATE command syntax error. No ';' token found.");
        }
        setDbName(databaseName);
    }

    @Override
    public String execute() throws SyntaxException, IOException {
        this.checkCreateRoot();
        if(!this.createDB()){
            throw new SyntaxException(1, "Failed to initiate:" + dbName + "at cw-db/databases/" + dbName);
        }
        return "[OK]";
    }
}
