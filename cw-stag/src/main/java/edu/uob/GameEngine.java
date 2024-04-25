package edu.uob;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GameEngine {
    private File entitiesFile;
    private File actionsFile;
    public Map<String, Location> map;
    public DocumentParser p;

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
