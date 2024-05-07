package edu.uob;

import edu.uob.Command.*;

import java.util.*;

public class GameEngine {
    private Map<String, Player> GamePlayers;
    private Player player;
    private String entitiesFile;
    private String actionsFile;
    private Map<String, Location> map;
    private AdvancedAction advancedAction;
    private HashMap<String, HashSet<AdvancedAction>> actions;
    private String firstLocation;
    private Set<String> advancedActionsNames;
    private Set<String> basicActionsNames;
    private ArrayList<String> entities;
    public List<String> command;

    public GameEngine(String entitiesFile, String actionsFile, Map<String, Player> GamePlayers) throws Exception {
        this.GamePlayers = GamePlayers;
//        this.player = player;
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.map = processEntitiesFile();
        this.entities = new ArrayList<>();
        for(Location location : map.values()) {
            location.setAllEntities();
            this.entities.add(location.getName());
            for(GameEntity gameEntity: location.entityList){
                this.entities.add(gameEntity.getName());
            }
        }
        this.actions = processActionsFile();
        setAdvancedActions();
   }


    // Add action commands from the XML file to Set
    private void setAdvancedActions() {
        this.advancedActionsNames = new HashSet<>();
        this.advancedActionsNames.addAll(actions.keySet());
        this.basicActionsNames = new HashSet<>();
        this.basicActionsNames.addAll(Arrays.asList("get", "look", "inv", "inventory", "goto", "drop", "health"));
    }

    private Map<String, Location> processEntitiesFile() throws Exception {
        GraphvizParser p = new GraphvizParser(this.entitiesFile);
        this.firstLocation = p.firstNode.getId().getId();
        return p.getGameMap();
    }

    private HashMap<String, HashSet<AdvancedAction>> processActionsFile() throws GameError {
        DocumentParser p = new DocumentParser(this, player, this.actionsFile);
        this.advancedAction = p.getActionsState();
        this.advancedAction.setFirstLocation(this.firstLocation);
        return p.getGameActions();
    }

    public String execute(List<String> cleanCommand, String playerName) throws Exception {
        setGamePlayers(playerName);
        checkCommand(cleanCommand);
        return executeCommand(this.command).toString();
    }

    public ArrayList<String> getEntities(){
        return this.entities;
    }

    public HashMap<String, HashSet<AdvancedAction>> getActions() {
        return actions;
    }

    private void setGamePlayers(String playerName) {
        this.player = GamePlayers.get(playerName);
        setFirstLocation();
    }

    private void checkCommand(List<String> cleanCommand) throws GameError {
        List<String> possibleEntities = new ArrayList<>();
        for(String token : cleanCommand) {
            if(this.entities.contains(token.trim())) {
                possibleEntities.add(token.trim());
            }
        }
        boolean checkAdvanced = false;
        List<String> possibleAction = new ArrayList<>();
        for(String token : cleanCommand) {
            if(this.basicActionsNames.contains(token.trim())){
                possibleAction.add(token.trim());
            } else if(this.advancedActionsNames.contains(token.trim())) {
                possibleAction.add(token.trim());
                checkAdvanced = true;
            }
        }
        if(possibleAction.isEmpty()) throw new GameError("Unknown action");
        if (!checkAdvanced) {
            String action = possibleAction.get(0);
            if ((action.contains("look") || action.contains("inv")) && possibleEntities.size() >=1) {
                throw new GameError("You can't specify any entities with this command.");
            }
            if (possibleEntities.size() == 0) {
                if (!(action.contains("look") || action.contains("inv"))) {
                    throw new GameError("You need to specify at least one entity");
                }
            } else if (possibleEntities.size() > 1) {
                throw new GameError("Too many entities for a basic command!");
            }
        }
        if(checkAdvanced) {
            HashSet<String> narrations = new HashSet<>();
            HashSet<AdvancedAction> actionsList = new HashSet<>();
            int size = possibleEntities.size();
            for(int i = 0; i < size; i++) {
                actionsList.addAll(this.actions.get(possibleAction.get(i)));
            }
            for(AdvancedAction action : actionsList) {
                narrations.add(action.getNarration());
                if(narrations.size() > 1) throw new GameError("Too many Game Actions!");
                for(String entitie : possibleEntities) {
                    if(!action.getSubjects().contains(entitie)) {
                        throw new GameError("Invalid Entity: " + entitie);
                    }
                }
            }
        }
        this.command = new ArrayList<>();
        this.command.add(possibleAction.get(0));
        this.command.addAll(possibleEntities);
    }

    private String executeCommand(List<String> commandList) throws Exception {
        String[] words = commandList.toArray(new String[commandList.size()]);
        String actionWord = words[0].trim();
        String command = commandList.toString();
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
            return handleGameAction(commandList);
        } else{
            throw new GameError("Unknown command: " + command);
        }
    }

    private String handleGameAction(List<String> command) throws GameError {
//        String[] words = command.split("\\s+");
        String actionWord = command.get(0);
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
        if(this.player.getCurrentLocation() == null){
            this.player.setLocation(this.firstLocation);
        }
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

    public void setNewPlayer(Player player) {
        this.GamePlayers.put(player.getPlayerName(), player);
    }

    public void updatePlayer(Map<String, Player> gamePlayers) {
        this.GamePlayers = gamePlayers;
    }
}
