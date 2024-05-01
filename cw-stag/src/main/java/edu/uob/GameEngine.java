package edu.uob;

import edu.uob.basic_commands.*;

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
    private HashMap<String, HashSet<GameAction>> actions;
    private String firstLocation;
    private Set<String> basicActions;
    private Set<String> advancedActions;

    public GameEngine(String entitiesFile, String actionsFile, Player player) throws Exception {
        this.player = player;
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.map = processEntitiesFile();
        this.actions = processActionsFile();
        setBasicActions();
        setAdvancedActions();
   }

    // Add Basic Actions to Set
   private void setBasicActions(){
        this.basicActions = new HashSet<>();
        this.basicActions.add("inv");
        this.basicActions.add("get");
        this.basicActions.add("goto");
        this.basicActions.add("look");
        this.basicActions.add("drop");
   }

    // Add action commands from the XML file to Set
    private void setAdvancedActions() {
        this.advancedActions = new HashSet<>();
        this.advancedActions.addAll(actions.keySet());
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
//        Sentence sentence = new Sentence(cleanCommand);
        //inventory -> inv
//        List<String> lemmas = sentence.lemmas();
//        List<String> posTags = sentence.posTags();
//        List<String> filteredWords = lemmas.stream()
//                .filter(lemma -> actionKeywords.contains(lemma))
//                .collect(Collectors.toList());
        return executeCommand(cleanCommand);
    }

    private String executeCommand(String command) throws Exception {
        if (command.contains("look")) {
            return new Look(this, player, command).toString();
        } else if (command.contains("get")) {
            return new Get(this, player, command).toString();
        } else if (command.contains("inv")) {
            return new Inventory(this, player, command).toString();
        } else if (command.contains("goto")) {
            return new Goto(this, player, command).toString();
        } else if (command.contains("drop")) {
            new Drop(this, player, command);
            return new Look(this, player, command).toString();
        } else if (this.advancedActions.contains(command)){
            return new Look(this, player, command).toString();
        } else{
            throw new GameError("Unknown command: " + command);
        }
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
