package edu.uob;

import edu.uob.BasicCommands.Look;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameEngine {
    private String entitiesFile;
    private String actionsFile;
    public Map<String, Location> map;
    public HashMap<String, HashSet<GameAction>> gameActions;
    public Map<String, Player> players;

    public GameEngine(String entitiesFile, String actionsFile) throws Exception {
        this.players = players;
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.map = processEntitiesFile();
        this.gameActions = processActionsFile();
    }

    private Map<String, Location> processEntitiesFile() throws Exception {
        GraphvizParser p = new GraphvizParser(this.entitiesFile);
        return p.getGameMap();
    }

    private HashMap<String, HashSet<GameAction>> processActionsFile() throws GameError {
        DocumentParser p = new DocumentParser(this.actionsFile);
        return p.getGameActions();
    }

    public String toString(String cleanCommand) throws GameError {
        if (cleanCommand != null && cleanCommand.contains("look")) {
            Look look = new Look(this, players.get(0), cleanCommand);
            return look.toString();
        }
        return cleanCommand;
    }

    public Location getPlayerStartLocation() {
        return map.get("cabin");
    }

    public void processPlayers(Map<String, Player> gamePlayers) {
        this.players = gamePlayers;
    }
}
