package edu.uob;

import edu.uob.basic_commands.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GameEngine {
    private final Player player;
    private String entitiesFile;
    private String actionsFile;
    private Map<String, Location> map;
    private Map<String, HashSet<GameAction>> gameActions;
    private String firstLocation;

    public GameEngine(String entitiesFile, String actionsFile, Player player) throws Exception {
        this.player = player;
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.setMap(processEntitiesFile());
        this.setGameActions(processActionsFile());
   }

    private Map<String, Location> processEntitiesFile() throws Exception {
        GraphvizParser p = new GraphvizParser(this.entitiesFile);
        this.firstLocation = p.firstNode.getId().getId();
        return p.getGameMap();
    }

    private HashMap<String, HashSet<GameAction>> processActionsFile() throws GameError {
        DocumentParser p = new DocumentParser(this.actionsFile);
        return p.getGameActions();
    }

    public String toString(String cleanCommand) throws Exception {
        if (cleanCommand != null && cleanCommand.contains("look")) {
            Look look = new Look(this, player, cleanCommand);
            return look.toString();
        }
        if (cleanCommand != null && cleanCommand.contains("get")) {
            Get get = new Get(this, player, cleanCommand);
            return get.toString();
        }
        if (cleanCommand != null && cleanCommand.contains("inv")) {
            Inventory inventory = new Inventory(this, player, cleanCommand);
            return inventory.toString();
        }
        if (cleanCommand != null && cleanCommand.contains("goto")) {
            new Goto(this, player, cleanCommand);
            Look look = new Look(this, player, cleanCommand);
            return look.toString();
        }
        if (cleanCommand != null && cleanCommand.contains("drop")) {
            Drop drop = new Drop(this, player, cleanCommand);
            return drop.toString();
        }
        return cleanCommand;
    }

    public void setFirstLocation() {
        player.setLocation(this.firstLocation);
    }

    public String getPlayerStartLocation() {
        return this.firstLocation;
    }

    public Artefact pickupArtefact(String locationId, String artefactName) throws GameError{
        Location location = getMap().get(locationId);
        if (location == null) {
            throw new GameError("Location not found.");
        }
        List<Artefact> artefacts = location.artefacts;
        Artefact foundArtefact = null;
        for (Artefact artefact : artefacts) {
            if (artefact.getName().equalsIgnoreCase(artefactName)) {
                foundArtefact = artefact;
                break;
            }
        }
        if (foundArtefact == null) {
            throw new GameError("Artefact not found.");
        }
        // If found, remove the artifact from the location
        artefacts.remove(foundArtefact);
        return foundArtefact;
    }


    public Map<String, Location> getMap() {
        return map;
    }

    public void setMap(Map<String, Location> map) {
        this.map = map;
    }

    public Map<String, HashSet<GameAction>> getGameActions() {
        return gameActions;
    }

    public void setGameActions(Map<String, HashSet<GameAction>> gameActions) {
        this.gameActions = gameActions;
    }
}
