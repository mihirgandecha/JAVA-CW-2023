package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GameServer {

  private static final char END_OF_TRANSMISSION = 4;
  public GameEngine GameEngine;
  private final String entitiesFileString;
  private final String actionsFileString;

  public static void main(String[] args) throws Exception {
    File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
    File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
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
  public GameServer(File entitiesFile, File actionsFile) throws GameError{
      if (entitiesFile.exists() && entitiesFile.getName().endsWith(".dot") &&
              actionsFile.exists() && actionsFile.getName().endsWith(".xml"))
      {
        this.entitiesFileString = entitiesFile.toString();
        this.actionsFileString = actionsFile.toString();
      }else{
        throw new GameError("File does not exist!");
      }
  }

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
    try {
      if (GameEngine == null) {
        GameEngine = new GameEngine(this.entitiesFileString, this.actionsFileString);
      }
      Tokeniser tokeniser = new Tokeniser(command);
      String username = tokeniser.getUsername();
      List<String> tokens = tokeniser.getTokens();
      Player player = addOrRetrievePlayer(username);
      return GameEngine.execute(tokens, username);
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  private Player addOrRetrievePlayer(String username) {
    Player player;
    if(GameEngine.getPlayerMap().containsKey(username)) {
      player = GameEngine.getPlayerMap().get(username);
    } else {
      player = new Player(username);
      GameEngine.setPlayer(player);
      GameEngine.setNewPlayer(player);
    }
    return player;
  }

  public edu.uob.GameEngine getGameEngine() {
    return GameEngine;
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
