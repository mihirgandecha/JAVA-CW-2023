package edu.uob;

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

    public String toString(String cleanCommand) {
        Location start = getPlayerStartLocation();
        return "You are in " + start.description + " You can see:\n" + "A " + map.get("cabin").getCharactersToString() + "\n" + "A " + "A ";
    }

//    public void setPlayerStartLocation() {
//
//    }

    public Location getPlayerStartLocation() {
        return map.get("cabin");
    }

    public void processPlayers(Map<String, Player> gamePlayers) {
        this.players = gamePlayers;
    }
}
