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
    public Map<String, Action> playerActions;

    public GameEngine(File entitiesFile, File actionsFile) {
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.map = processEntitiesFile();
    }

    private Map<String, Location> processEntitiesFile() {
        GraphvizParser p = new GraphvizParser(this.entitiesFile.toString());
        return p.getGameMap();
    }

    private void setJsonParser() throws IOException, SAXException, ParserConfigurationException {
        this.p = new DocumentParser(this.actionsFile.toString());
    }
}
