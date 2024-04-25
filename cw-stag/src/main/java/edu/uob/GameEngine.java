package edu.uob;

import java.io.File;

public class GameEngine {
    private File entitiesFile;
    private File actionsFile;

    public GameEngine(File entitiesFile, File actionsFile) {
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
    }
}
