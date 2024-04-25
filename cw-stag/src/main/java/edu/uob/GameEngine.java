package edu.uob;

import java.io.File;
import java.util.Map;

public class GameEngine {
    private File entitiesFile;
    private File actionsFile;
    private Map<String, Location> map;

    public GameEngine(File entitiesFile, File actionsFile) {
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.map = processEntitiesFile();
    }

    private Map<String, Location> processEntitiesFile() {
        GraphvizParser p = new GraphvizParser(this.entitiesFile.toString());
        return p.getGameMap();
    }
}
