package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;

import edu.uob.DBCmnd.*;

/** This class implements the DB server. */
public class DBServer {
    public Metadata dbStore = new Metadata();

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
     * KEEP this signature otherwise we won't be able to mark your submission correctly.
     */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
     * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
     * able to mark your submission correctly.
     *
     * <p>This method handles all incoming DB commands and carries out the required actions.
     */
    public String handleCommand(String command) throws IOException {
        // TODO implement your server logic here - return a string output -> client
        dbStore.setStoragePath(this.storageFolderPath);
        try {
            Parser p = new Parser(command);
            p.firstCheck();
            String firstToken = p.getCurrentToken();
            DBCmnd cmd;
            //TODO Do I need to convert if lowercase?
            switch (firstToken) {
                case "USE" -> cmd = (DBCmnd) new Use(dbStore);
                case "CREATE" -> cmd = (DBCmnd) new Create(dbStore);
                case "DROP" -> cmd = (DBCmnd) new Drop(dbStore);
                case "ALTER" -> cmd = (DBCmnd) new Alter(p);
                case "INSERT" -> cmd = (DBCmnd) new Insert(dbStore);
                case "SELECT" -> cmd = (DBCmnd) new Select(dbStore);
                case "UPDATE" -> cmd = (DBCmnd) new Update(p);
                case "DELETE" -> cmd = (DBCmnd) new Delete(p);
                case "JOIN" -> cmd = (DBCmnd) new Join(p);
                default -> throw new SyntaxException(" [SERVER]: Empty/Invalid Command");
            }
            cmd.parse(p);
            return cmd.execute(p);
        } catch (SyntaxException e) {
            //TODO put ERROR here.
            return "" + e.getMessage();
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
