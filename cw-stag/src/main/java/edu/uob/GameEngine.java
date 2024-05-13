package edu.uob;
import edu.uob.Command.*;
import java.util.*;

public class GameEngine {
    private final Map<String, Player> playerMap;
    private Player player;
    private final String entitiesFile;
    private final String actionsFile;
    private final Map<String, Location> map;
    private String firstLocation;
    private static final Set<String> BASIC_ACTIONS_NAMES = Set.of("get", "look", "inv", "inventory", "goto", "drop", "health");
    private final Set<String> advancedActionsNames;
    private final ArrayList<String> allLocationsGameEntities;
    private final HashMap<String, HashSet<AdvancedAction>> actions;
    public List<String> command = new ArrayList<>();
    private boolean priorityCommand = false;

    public GameEngine(String entitiesFile, String actionsFile) throws Exception {
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.playerMap = new HashMap<>();
        this.map = processEntitiesFile();
        this.allLocationsGameEntities = setAllLocationsGameEntities();
        this.actions = processActionsFile();
        this.advancedActionsNames = new HashSet<>();
        setAdvancedActions();
   }

    private ArrayList<String> setAllLocationsGameEntities() {
        ArrayList<String> GameEntities = new ArrayList<>();
        for(Location location : map.values()) {
            location.setAllEntities();
            GameEntities.add(location.getName());
            for(GameEntity gameEntity: location.getEntityList()){
                GameEntities.add(gameEntity.getName());
            }
        }
        return GameEntities;
    }

   public void setPlayer(Player setPlayer){
        this.player = setPlayer;
   }

    // Add action commands from the XML file to Set
    private void setAdvancedActions() {
        this.advancedActionsNames.addAll(actions.keySet());
    }

    private Map<String, Location> processEntitiesFile() throws Exception {
        GraphvizParser p = new GraphvizParser(this.entitiesFile);
        this.firstLocation = p.getFirstLocation();
        return p.getGameMap();
    }

    public Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    private HashMap<String, HashSet<AdvancedAction>> processActionsFile() throws GameError {
        AdvancedAction advancedAction;
        DocumentParser p = new DocumentParser(this, player, this.actionsFile);
        advancedAction = p.getActionsState();
        advancedAction.setFirstLocation(this.firstLocation);
        return p.getGameActions();
    }

    public String execute(List<String> cleanCommand, String playerName) throws Exception {
        setGamePlayer(playerName);
        this.command.clear();
        this.priorityCommand = false;
        checkCommand(cleanCommand);
        return executeCommand(this.command);
    }

    public ArrayList<String> getAllLocationsGameEntities(){
        return this.allLocationsGameEntities;
    }

    public HashMap<String, HashSet<AdvancedAction>> getActions() {
        return actions;
    }

    private void setGamePlayer(String playerName) {
        this.player = playerMap.get(playerName);
        setCurrentLocation();
    }

    private void checkCommand(List<String> cleanCommand) throws GameError {
        //First strip any words that are not a GameEntity or Action:
        List<String> filteredCommand = normalizeAndFilterCommand(cleanCommand);
        // Extract possible actions and entities from the filtered command
        List<String> possibleEntities = getCommandEntityList(filteredCommand);
        List<String> possibleActions = getActionsFromCommand(filteredCommand);
        //Check command has at least one action
        if(possibleActions.isEmpty()) throw new GameError("Command Requires at least one action!");
        //If the command is inv | health | look -> prioritise and execute
        List<String> primaryAction = selectPrimaryActions(possibleActions);
        if(this.priorityCommand && primaryAction.size() == 1) {
            if(!possibleEntities.isEmpty()) throw new GameError("Priority commands cannot have defined entities!");
            this.command.add(primaryAction.get(0));
            return;
        }
        // Handle single action commands (get, drop, goto)
        if (tryExecuteSingleActionCommand("get", primaryAction, possibleActions, possibleEntities)
                || tryExecuteSingleActionCommand("drop", primaryAction, possibleActions, possibleEntities)
                || tryExecuteSingleActionCommand("goto", primaryAction, possibleActions, possibleEntities)) {
            return;
        }
        if(possibleActions.isEmpty() || possibleEntities.isEmpty()) throw new GameError("Get, Drop and Goto commands can only have one trigger action, and require only one Game Entity!");
        //Else return advanced action:
        this.command.add(possibleActions.get(0));
        this.command.addAll(possibleEntities);
    }

    private List<String> normalizeAndFilterCommand(List<String> rawCommand) {
        List<String> normalizedCommand = new ArrayList<>();
        for (String token : rawCommand) {
            String normalizedToken = token.trim().toLowerCase();
            if (BASIC_ACTIONS_NAMES.contains(normalizedToken) ||
                    advancedActionsNames.contains(normalizedToken) ||
                    allLocationsGameEntities.contains(normalizedToken)) {
                normalizedCommand.add(normalizedToken);
            }
        }
        return normalizedCommand;
    }

    private List<String> getActionsFromCommand(List<String> cleanCommand) {
        List<String> possibleAction = new ArrayList<>();
        for (String token : cleanCommand) {
            String trimmedToken = token.trim();
            if (BASIC_ACTIONS_NAMES.contains(trimmedToken)) {
                possibleAction.add(trimmedToken);
            } else if (this.advancedActionsNames.contains(trimmedToken)) {
                possibleAction.add(trimmedToken);
            }
        }
        return possibleAction;
    }

    private List<String> getCommandEntityList(List<String> cleanCommand) {
        List<String> possibleEntities = new ArrayList<>();
        for(String token : cleanCommand) {
            if(this.allLocationsGameEntities.contains(token.trim())) {
                possibleEntities.add(token.trim());
            }
        }
        return possibleEntities;
    }

    private List<String> selectPrimaryActions(List<String> possibleActions) throws GameError {
        if (possibleActions.isEmpty()) {
            throw new GameError("Require at least one action!");
        }
        // Give priority to specific actions like "look", "inventory", etc.
        List<String> prioritizedActions = List.of("look", "inventory", "inv", "health");
        List<String> foundActions = new ArrayList<>();
        for (String action : possibleActions) {
            for (String prioritizedAction : prioritizedActions) {
                if (action.equalsIgnoreCase(prioritizedAction)) {
                    foundActions.add(action);
                }
            }
        }
        if (foundActions.isEmpty()) {
            // If no prioritised action is found, return all available actions
            return possibleActions;
        } else {
            // Check if any prioritized action occurs more than once
            if(foundActions.size() > 1) {
                throw new GameError("A prioritised action appears more than once: " + possibleActions);
            }
            this.priorityCommand = true;
            // Since a prioritised action was found, and it is unique, return it
            return foundActions;
        }


//        for (String prioritisedAction : prioritizedActions) {
//            if (possibleActions.toString().equalsIgnoreCase(prioritisedAction)) {
//                this.priorityCommand = true;
//                return List.of(prioritisedAction);
//            }
//        }
//        // If no prioritised action is found, return all available actions
//        return possibleActions;
    }

    private boolean tryExecuteSingleActionCommand(String action, List<String> primaryAction, List<String> possibleActions, List<String> possibleEntities) {
        if (possibleActions.size() == 1 && possibleActions.get(0).equals(action) && possibleEntities.size() == 1) {
            this.command.add(primaryAction.get(0));
            this.command.add(possibleEntities.get(0));
            return true;
        } else {
            primaryAction.remove(action);
            return false;
        }
    }


    private String executeCommand(List<String> commandList) throws Exception {
        String[] words = commandList.toArray(new String[0]);
        String actionWord = words[0].trim();
        String command = commandList.toString();
        boolean printMultiplayer = playerMap.size() > 1;
        if (command.contains("look")) {
            Look look = new Look(this, player, command);
            if(printMultiplayer){
                return look + multiplayerToString();
            }
            return look.toString();
        } else if (command.contains("get")) {
            Get get = new Get(this, player, command);
            return get.toString();
        } else if (command.contains("inv")) {
            Inventory inv = new Inventory(this, player, command);
            return inv.toString();
        } else if (command.contains("goto")) {
            Goto aGoto = new Goto(this, player, command);
            if(printMultiplayer){
                return aGoto + multiplayerToString();
            }
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

    private String multiplayerToString(){
        StringBuilder stringBuilder = new StringBuilder();
        String checkLocation = this.player.getCurrentLocation();
        for(Player p: playerMap.values()){
            if(!checkLocation.isEmpty() && !p.getPlayerName().equals(this.player.getPlayerName()) && checkLocation.equalsIgnoreCase(p.getCurrentLocation())){
                stringBuilder.append(p.getPlayerName() + "\n");
            }
        }
        if (!stringBuilder.isEmpty()) {
            stringBuilder.insert(0, "\n[PLAYERS]:\n");
        }
        return stringBuilder.toString();
    }

    private String handleGameAction(List<String> command) throws GameError {
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

    public void setCurrentLocation() {
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
        this.playerMap.put(player.getPlayerName(), player);
    }

}
