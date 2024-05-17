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
    public static final String ACTION_GET = "get";
    public static final String ACTION_LOOK = "look";
    public static final String ACTION_INV = "inv";
    public static final String ACTION_INVENTORY = "inventory";
    public static final String ACTION_GOTO = "goto";
    public static final String ACTION_DROP = "drop";
    public static final String ACTION_HEALTH = "health";
    private static final Set<String> BASIC_ACTIONS_NAMES = Set.of(ACTION_GET, ACTION_LOOK, ACTION_INV, ACTION_INVENTORY, ACTION_GOTO, ACTION_DROP, ACTION_HEALTH);
    private final Set<String> advancedActionsNames;
    private final ArrayList<String> allLocationsGameEntities;
    private final HashMap<String, HashSet<AdvancedAction>> actions;
    public List<String> commandToExecute = new ArrayList<>();
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
        ArrayList<String> gameEntities = new ArrayList<>();
        for(Location location : map.values()) {
            location.setAllEntities();
            gameEntities.add(location.getName());
            for(GameEntity gameEntity: location.getEntityList()){
                gameEntities.add(gameEntity.getName());
            }
        }
        return gameEntities;
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
        Map<String, Location> gameMap = p.getGameMap();
        if(!gameMap.containsKey("storeroom")){
            Location storeroom = new Location("storeroom", "Storage for any entities not placed in the game");
            gameMap.put("storeroom", storeroom);
        }
        return gameMap;
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
        this.commandToExecute.clear();
        this.priorityCommand = false;
        checkCommand(cleanCommand);
        return executeCommand(this.commandToExecute);
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
        //strip any words that are not a GameEntity or Action:
        List<String> filteredCommand = filterCommandForDecorativeWords(cleanCommand);
        //extract possible actions and entities from the filtered command
        List<String> possibleEntities = getEntitiesFromCommand(filteredCommand);
        List<String> possibleActions = getActionsFromCommand(filteredCommand);
        if(possibleActions.isEmpty()) throw new GameError("Command Requires at least one VALID action!");
        //if the command is inv | health | look -> prioritise and execute
        List<String> primaryAction = selectPrimaryActions(possibleActions);
        if(this.priorityCommand && primaryAction.size() == 1) {
            if(!possibleEntities.isEmpty()) throw new GameError("Priority commands cannot have defined entities!");
            this.commandToExecute.add(primaryAction.get(0));
            return;
        }
        //handle single action commands (get, drop, goto)
        if (tryExecuteSingleActionCommand(ACTION_GET, primaryAction, possibleActions, possibleEntities)
                || tryExecuteSingleActionCommand(ACTION_DROP, primaryAction, possibleActions, possibleEntities)
                || tryExecuteSingleActionCommand(ACTION_GOTO, primaryAction, possibleActions, possibleEntities)) {
            return;
        }
        if(possibleActions.isEmpty() && possibleEntities.isEmpty()) throw new GameError("Get, Drop and Goto commands can only have one trigger action, and require only one Game Entity!");
        //Else return advanced action:
        this.commandToExecute.add(possibleActions.get(0));
        this.commandToExecute.addAll(possibleEntities);
    }

    private List<String> filterCommandForDecorativeWords(List<String> rawCommand) {
        List<String> filteredWords = new ArrayList<>();
        int i = 0;
        while (i < rawCommand.size()) {
            String token = rawCommand.get(i).trim().toLowerCase();
            String multiWordToken = getMultiWordToken(rawCommand, i);
            if (multiWordToken != null) {
                filteredWords.add(multiWordToken);
                i++;
            } else if (isActionOrEntityCheckingAll(token)) {
                filteredWords.add(token);
            }
            i++;
        }
        return filteredWords;
    }

    private String getMultiWordToken(List<String> rawCommand, int index) {
        StringBuilder tokenBuilder = new StringBuilder();
        tokenBuilder.append(rawCommand.get(index).trim().toLowerCase());

        for (int i = index + 1; i < rawCommand.size(); i++) {
            tokenBuilder.append(" ").append(rawCommand.get(i).trim().toLowerCase());
            String combinedToken = tokenBuilder.toString().trim();
            if (isActionOrEntityCheckingAll(combinedToken)) {
                return combinedToken;
            }
        }

        return null;
    }


    //Helper Functions for Action:
    private boolean isBuiltInAction(String token) {
        return BASIC_ACTIONS_NAMES.contains(token);
    }

    private boolean isAdvancedAction(String token) {
        return advancedActionsNames.contains(token);
    }

    private boolean isAction(String token) {
        return isBuiltInAction(token) || isAdvancedAction(token);
    }

    //Helper Functions for Entity:
    private boolean isGameEntity(String token) {
        return allLocationsGameEntities.contains(token);
    }

    //Helper combining all:
    private boolean isActionOrEntityCheckingAll(String token) {
        return isAction(token) || isGameEntity(token);
    }

    private List<String> getActionsFromCommand(List<String> cleanCommand) {
        List<String> possibleAction = new ArrayList<>();
        for (String token : cleanCommand) {
            if(isAction(token.trim())) {
                possibleAction.add(token.trim());
            }
        }
        return possibleAction;
    }

    private List<String> getEntitiesFromCommand(List<String> cleanCommand) {
        List<String> possibleEntities = new ArrayList<>();
        for(String token : cleanCommand) {
            if(isGameEntity(token.trim())){
                possibleEntities.add(token.trim());
            }
        }
        return possibleEntities;
    }

    private List<String> selectPrimaryActions(List<String> possibleActions) throws GameError {
        List<String> prioritizedActions = List.of(ACTION_LOOK, ACTION_INV, ACTION_INVENTORY, ACTION_HEALTH);
        List<String> foundActions = new ArrayList<>();
        for (String action : possibleActions) {
            for (String prioritizedAction : prioritizedActions) {
                if (action.equalsIgnoreCase(prioritizedAction)) {
                    foundActions.add(action);
                }
            }
        }
        if (foundActions.isEmpty()) {
            return possibleActions;
        } else {
            if(foundActions.size() > 1) {
                throw new GameError("A prioritised action appears more than once: " + possibleActions);
            }
            this.priorityCommand = true;
            return foundActions;
        }
    }

    private boolean tryExecuteSingleActionCommand(String action, List<String> primaryAction, List<String> possibleActions, List<String> possibleEntities) throws GameError {
        if (possibleActions.size() == 1 && possibleActions.get(0).equals(action)) {
            if(possibleEntities.size() != 1) throw new GameError("Cannot have more than one entity for command: " + action);
            this.commandToExecute.add(primaryAction.get(0));
            this.commandToExecute.add(possibleEntities.get(0));
            return true;
        } else {
            primaryAction.remove(action);
            return false;
        }
    }

    private String executeCommand(List<String> commandList) throws Exception {
        String[] words = commandList.toArray(new String[0]);
        String actionWord = words[0].trim();
        String commandToString = commandList.toString();
        boolean printMultiplayer = playerMap.size() > 1;
        if (commandToString.contains(ACTION_LOOK)) {
            Look look = new Look(this, player, commandToString);
            if(printMultiplayer){
                return look + multiplayerToString();
            }
            return look.toString();
        } else if (commandToString.contains(ACTION_GET)) {
            Get get = new Get(this, player, commandToString);
            return get.toString();
        } else if (commandToString.contains(ACTION_INV)) {
            Inventory inv = new Inventory(this, player, commandToString);
            return inv.toString();
        } else if (commandToString.contains(ACTION_GOTO)) {
            Goto aGoto = new Goto(this, player, commandToString);
            if(printMultiplayer){
                return aGoto + multiplayerToString();
            }
            return aGoto.toString();
        } else if (commandToString.contains(ACTION_DROP)) {
            Drop drop = new Drop(this, player, commandToString);
            return drop.toString();
        } else if(commandToString.contains(ACTION_HEALTH)){
            String health = String.valueOf(player.getHealth());
            return "Player health " + health;
        } else if (this.advancedActionsNames.contains(actionWord)){
            return handleGameAction(commandList);
        } else{
            throw new GameError("Unknown command: " + commandToString);
        }
    }

    private String multiplayerToString(){
        StringBuilder stringBuilder = new StringBuilder();
        String checkLocation = this.player.getCurrentLocation();
        for(Player p: playerMap.values()){
            if(!checkLocation.isEmpty() && !p.getPlayerName().equals(this.player.getPlayerName()) && checkLocation.equalsIgnoreCase(p.getCurrentLocation())){
                stringBuilder.append(p.getPlayerName()).append("\n");
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
        // If found, remove the artefact from the location
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
