package edu.uob;

import edu.uob.BasicCommands.Get;
import edu.uob.BasicCommands.Inventory;
import edu.uob.BasicCommands.Look;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GameEngine {
    private final Player player;
    private String entitiesFile;
    private String actionsFile;
    public Map<String, Location> map;
    public HashMap<String, HashSet<GameAction>> gameActions;
    public String firstLocation;

    public GameEngine(String entitiesFile, String actionsFile, Player player) throws Exception {
        this.player = player;
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.map = processEntitiesFile();
        this.gameActions = processActionsFile();
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

    public String toString(String cleanCommand) throws GameError, Exception {
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
        return cleanCommand;
    }

    public void setFirstLocation() {
        player.setLocation(this.firstLocation);
    }

    public String getPlayerStartLocation() {
        return this.firstLocation;
    }

    // Helper method to get location by ID
    private Location getLocation(String locationId) throws GameError {
        if (!map.containsKey(locationId)) {
            throw new GameError("Location not found.");
        }
        return map.get(locationId);
    }

    public Artefact pickupArtefact(String locationId, String artefactName) throws Exception {
        Location location = map.get(locationId);
        if (location == null) {
            throw new Exception("Location not found.");
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


}
