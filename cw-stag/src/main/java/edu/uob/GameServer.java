package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class GameServer {

  private static final char END_OF_TRANSMISSION = 4;
  private Map<String, Player> GamePlayers;
  public GameEngine GameEngine;
  private final String entitiesFileString;
  private final String actionsFileString;

  public static void main(String[] args) throws Exception {
    File defaultEntitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
    File defaultActionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
    File entitiesFile = defaultEntitiesFile;
    File actionsFile = defaultActionsFile;
    if (args.length == 2) {
      File customEntitiesFile = Paths.get("config" + File.separator + args[0]).toAbsolutePath().toFile();
      File customActionsFile = Paths.get("config" + File.separator + args[1]).toAbsolutePath().toFile();
      if (customEntitiesFile.exists() && customEntitiesFile.getName().endsWith(".dot") &&
              customActionsFile.exists() && customActionsFile.getName().endsWith(".xml")) {
        entitiesFile = customEntitiesFile;
        actionsFile = customActionsFile;
        System.out.println("Using custom files.");
      } else {
        System.out.println("Invalid or non-existent custom files, using default files.");
      }
    }
    GameServer server = new GameServer(entitiesFile, actionsFile);
    server.blockingListenOn(8888);
  }


  /**
   * Do not change the following method signature or we won't be able to mark your
   * submission
   * Instanciates a new server instance, specifying a game with some configuration
   * files
   *
   * @param entitiesFile The game configuration file containing all game entities
   *                     to use in your game
   * @param actionsFile  The game configuration file containing all game actions
   *                     to use in your game
   */

  // Validate file types and existence
  public GameServer(File entitiesFile, File actionsFile) throws GameError {
    this.entitiesFileString = entitiesFile.toString();
    this.actionsFileString = actionsFile.toString();
  }

  // Message to inform the player about the game configuration
//  private void announceGameFiles(String entitiesFile, File actionsFile) throws GameError {
//    System.out.println("🎮 Game Input Loaded 🎮");
//    System.out.println(">> Venturing into new territories with: " + entitiesFile.toString());
//    System.out.println(">> Mastering dynamic challenges with: " + actionsFile.toString());
//    System.out.println("Ready your gear and prepare for an adventure like no other!");
//  }

  /**
   * Do not change the following method signature or we won't be able to mark your
   * submission
   * This method handles all incoming game commands and carries out the
   * corresponding actions.
   * </p>
   *
   * @param command The incoming command to be processed
   */
  public String handleCommand(String command) {
    try{
      initializeGamePlayers();
      Tokeniser tokeniser = new Tokeniser(command);
      String username = tokeniser.getUsername();
      String cleanCommand = tokeniser.getCleanCommand();
      //Check if player already exists:
      Player player = addOrRetrievePlayer(username);
      //Process Command:
      if(GameEngine == null){
        GameEngine = new GameEngine(this.entitiesFileString, this.actionsFileString, player);
        GameEngine.setFirstLocation();
      }
      return GameEngine.toString(cleanCommand);
    } catch (Exception e){
      return e.getMessage();
    }
  }

  private void initializeGamePlayers() {
    if (GamePlayers == null) {
      GamePlayers = new HashMap<>();
    }
  }

  private Player addOrRetrievePlayer(String username) {
    Player player;
    if(GamePlayers.containsKey(username)) {
      player = GamePlayers.get(username);
    } else {
      player = new Player(username);
      GamePlayers.put(username, player);
    }
    return player;
  }

  /**
   * Do not change the following method signature or we won't be able to mark your
   * submission
   * Starts a *blocking* socket server listening for new connections.
   *
   * @param portNumber The port to listen on.
   * @throws IOException If any IO related operation fails.
   */
  public void blockingListenOn(int portNumber) throws IOException {
    try (ServerSocket s = new ServerSocket(portNumber)) {
      System.out.println("Server listening on port " + portNumber);
      while (!Thread.interrupted()) {
        try {
          blockingHandleConnection(s);
        } catch (IOException e) {
          System.out.println("Connection closed");
        }
      }
    }
  }

  /**
   * Do not change the following method signature or we won't be able to mark your
   * submission
   * Handles an incoming connection from the socket server.
   *
   * @param serverSocket The client socket to read/write from.
   * @throws IOException If any IO related operation fails.
   */
  private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
    try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
      System.out.println("Connection established");
      String incomingCommand = reader.readLine();
      if (incomingCommand != null) {
        System.out.println("Received message from " + incomingCommand);
        String result = handleCommand(incomingCommand);
        writer.write(result);
        writer.write("\n" + END_OF_TRANSMISSION + "\n");
        writer.flush();
      }
    }
  }
}
