package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.logging.Logger;

import edu.uob.DBParse.*;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    public String storageFolderPath;
    private static final Logger LOGGER = Logger.getLogger(DBServer.class.getName());
    private Parser p;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        updateSorageFolderPath("databases");
        if (!createDirectoryIfAbsent()){
            System.err.println("Cannot generate database directory");
        }
        convertToPlatformIndependant(storageFolderPath);
    }

    public boolean createDirectoryIfAbsent() {
        try {
            Path dirPath = Paths.get(storageFolderPath);
            if(Files.exists(dirPath)){
                LOGGER.info("Directory is present at: " + dirPath);
                return true;
            }
            else{
                Files.createDirectories(Paths.get(storageFolderPath));
                LOGGER.info("Directory Created at: " + dirPath);
                return true;
            }
        }
        catch(IOException ioe) {
            throw new RuntimeException("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    public void updateSorageFolderPath(String absPath){
        storageFolderPath = Paths.get(absPath).toAbsolutePath().toString();
    }

    public String convertToPlatformIndependant(String absIndependentPath){
        return storageFolderPath + File.separator;
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        //System.out.println(command);
        // TODO implement your server logic here - return a string output -> client
        // Different reponse for query, ie Query OK if parsed and interpreted OK, else return error tag + description of problem
        // Parser p = new Parser(command);
        try{
            p = new Parser();
            p.setTokens(command);
            p.parse(p);
            return "OK";
        } catch (SyntaxException e){
            return e.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
