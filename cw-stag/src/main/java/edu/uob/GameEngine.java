package edu.uob;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameEngine {
    private String entitiesFile;
    private String actionsFile;
    public Map<String, Location> map;
    public HashMap<String, HashSet<GameAction>> gameActions;

    public GameEngine(File entitiesFile, File actionsFile) {
        this.entitiesFile = entitiesFile.toString();
        this.actionsFile = actionsFile.toString();
        this.map = processEntitiesFile();
        this.gameActions = new HashMap<>();
    }

    private Map<String, Location> processEntitiesFile() {
        GraphvizParser p = new GraphvizParser(this.entitiesFile);
        return p.getGameMap();
    }

    private HashMap<String, HashSet<GameAction>> processActionsFile() throws GameError {
        DocumentParser p = new DocumentParser(this.actionsFile);
        return p.getGameActions();
    }
}
