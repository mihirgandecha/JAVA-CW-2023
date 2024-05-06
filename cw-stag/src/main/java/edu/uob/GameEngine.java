package edu.uob;

import edu.uob.Command.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameEngine {
    private final Player player;
    private String entitiesFile;
    private String actionsFile;
    private Map<String, Location> map;
    private AdvancedAction advancedAction;
    private HashMap<String, HashSet<AdvancedAction>> actions;
    private String firstLocation;
    private Set<String> advancedActionsNames;

    public GameEngine(String entitiesFile, String actionsFile, Player player) throws Exception {
        this.player = player;
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.map = processEntitiesFile();
        this.actions = processActionsFile();
        setAdvancedActions();
   }

    // Add action commands from the XML file to Set
    private void setAdvancedActions() {
        this.advancedActionsNames = new HashSet<>();
        this.advancedActionsNames.addAll(actions.keySet());
    }

    private Map<String, Location> processEntitiesFile() throws Exception {
        GraphvizParser p = new GraphvizParser(this.entitiesFile);
        this.firstLocation = p.firstNode.getId().getId();
        return p.getGameMap();
    }

    private HashMap<String, HashSet<AdvancedAction>> processActionsFile() throws GameError {
        DocumentParser p = new DocumentParser(this.actionsFile);
        this.advancedAction = p.getActionsState();
        return p.getGameActions();
    }

    public String toString(String cleanCommand) throws Exception {
        return executeCommand(cleanCommand);
    }

    private String executeCommand(String command) throws Exception {
        String[] words = command.split("\\s+");
        String actionWord = words[0];
        if (command.contains("look")) {
            Look look = new Look(this, player, command);
            return look.toString();
        } else if (command.contains("get")) {
            Get get = new Get(this, player, command);
            return get.toString();
        } else if (command.contains("inv")) {
            Inventory inv = new Inventory(this, player, command);
            return inv.toString();
        } else if (command.contains("goto")) {
            Goto aGoto = new Goto(this, player, command);
            return aGoto.toString();
        } else if (command.contains("drop")) {
            Drop drop = new Drop(this, player, command);
            return drop.toString();
        } else if(command.contains("health")){
            String health = String.valueOf(player.getHealth());
            return "Player health " + health;
        } else if (this.advancedActionsNames.contains(actionWord)){
            return handleGameAction(command);
        } else{
            throw new GameError("Unknown command: " + command);
        }
    }

    private String handleGameAction(String command) throws GameError {
        String[] words = command.split("\\s+");
        String actionWord = words[0];
        HashSet<AdvancedAction> possibleActions = actions.get(actionWord);
        if (possibleActions == null) {
            throw new GameError("Unknown command: " + command);
        }
        for (AdvancedAction action : possibleActions) {
            if (action.canExecute(player, map)) {
                return action.execute();
            }
        }
        throw new GameError("Unknown Command: " + command);
    }

    public void setFirstLocation() {
        player.setLocation(this.firstLocation);
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
}
