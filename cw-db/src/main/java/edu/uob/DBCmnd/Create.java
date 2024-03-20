package edu.uob.DBCmnd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Create implements DBCmnd {
    private static String dbName;
    private static String rootDir;
    private static boolean isDatabase;
    private static boolean isTable;

    public void setDbName(String dirName) {
        dbName = dirName;
        rootDir = "cw-db" + File.separator + "databases" + File.separator + dbName;
        isDatabase = false;
        isTable = true;
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
        if (p.isCmndEmpty(p.tokens) || (!p.isValidCommand())) {
            throw new SyntaxException(1, "Command Empty or missing ';' at end.");
        }
        String createToken = p.getCurrentToken();
        String createExpectedToken = "CREATE";
        if (!createExpectedToken.equals(createToken)) {
            throw new SyntaxException(1, "CREATE command syntax error. No 'CREATE' token found.");
        }
        String nextToken = p.getNextToken();
        switch (nextToken) {
            case "DATABASE":
                parseDb(p);
                isDatabase = true;
                break;
            case "TABLE":
                parseTb(p);
                isTable = true;
                break;
            default:
                throw new SyntaxException(1, "Expected 'DATABASE' or 'TABLE' after 'CREATE'");
        }
    }

    public void parseDb(Parser p) throws SyntaxException, IOException {
        String databaseName = p.getNextToken();
        if (!p.isTbAtrDbName(databaseName)) {
            throw new SyntaxException(1,"CREATE command syntax error. Database name not plain text.");
        }
        String lastTkn = p.getLastToken();
        if (!p.ensureCmdEnd(lastTkn)){
            throw new SyntaxException(1, "';' Token not found.");
        }
        setDbName(databaseName);
    }

    public void parseTb(Parser p) throws SyntaxException, IOException {
        String tableName = p.getNextToken();
        if (!p.isTbAtrDbName(tableName)) {
            throw new SyntaxException(1, "CREATE command syntax error. Table name not plain text.");
        }
        String nextTkn = p.getNextToken();
        if (!p.ensureCmdEnd(nextTkn)){
            throw new SyntaxException(1, "Error.");
        }
    }

    private void parseTbAtrb(Parser p) throws SyntaxException, IOException {
        String firstBrkt = p.getNextToken();
        if (!"(".equals(firstBrkt)){
            throw new SyntaxException(1, "Opening bracket missing.");
        }
        String nextToken = p.getNextToken();
        while (nextToken != null && !")".equals(nextToken)) {
            nextToken = p.getNextToken();
            if (!p.isPlainText(nextToken)) {
                throw new SyntaxException(1, "Invalid Attribute List");
            }
            nextToken = p.getNextToken();
            if (",".equals(nextToken)) {
                p.getNextToken();
            }
        }
        p.getNextToken();

    }

    @Override
    public String execute() throws SyntaxException, IOException {
        if (isDatabase){
            this.checkCreateRoot();
            if(!this.createDB()){
                throw new SyntaxException(1, "Failed to initiate:" + dbName + "at cw-db/databases/" + dbName);
            }
        }
        if (isTable){

        }
        isTable = false;
        isDatabase = false;
        return "[OK]";
    }
}
