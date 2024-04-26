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

    public GameEngine(String entitiesFile, String actionsFile, Map<String, Player> players) throws Exception {
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
}
