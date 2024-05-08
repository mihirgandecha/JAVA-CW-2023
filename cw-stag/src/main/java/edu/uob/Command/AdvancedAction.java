package edu.uob.Command;

import edu.uob.*;
import edu.uob.Character;

import java.util.*;

public class AdvancedAction extends GameCommand
{
    //INPUT
    private List<String> triggers;
    //List of entities NECESSARY
    private List<String> subjects;
    //List of entities that are REMOVED
    private List<String> consumed;
    //List of entities that are CREATED
    private List<String> produced;
    //OUTPUT to console
    private String narration;
    private Location currentLocation;
    private List<GameEntity> locationEntities;
    private HashMap<String, Artefact> playerEntities;
    private Player player;
    private Location storeroom;
    private Map<String, Location> map;
    private boolean resetActivated = false;
    private String firstLocation;

    public AdvancedAction(GameEngine gameEngine, Player player, String basicCommand) {
        super(gameEngine, player, basicCommand);
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(ArrayList<String> triggers) {
        this.triggers = triggers;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getConsumed() {
        return consumed;
    }

    public void setConsumed(ArrayList<String> consumed) {
        this.consumed = consumed;
    }

    public List<String> getProduced() {
        return produced;
    }

    public void setProduced(ArrayList<String> produced) {
        this.produced = produced;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    // Method to check if the action can be executed based on current game state
    public boolean canExecute(Player player, Map<String, Location> map) throws GameError {
        this.player = player;
        this.currentLocation = map.get(player.getCurrentLocation());
        map.get(player.getCurrentLocation()).setAllEntities();
        this.locationEntities = map.get(player.getCurrentLocation()).entityList;
        this.playerEntities = player.getInventory();
        currentLocation.setAllEntities();
        doesSubjectsExist();
        //TODO - cannot activate action twice! blow horn two times duplicates!!
        this.storeroom = map.get("storeroom");
        this.storeroom.setAllEntities();
        this.map = map;
        return true;
    }

    private void doesSubjectsExist() throws GameError {
        for(String subject : subjects){
            if(!checkLocationForEntity(subject) && !checkInventoryForEntity(subject)){
                throw new GameError("Game Subject does not exist in Location or Player Inventory!\n");
            }
        }
    }

    private boolean checkLocationForEntity(String entityToCheck){
        for(GameEntity entity : this.locationEntities){
            if(entityToCheck.equals(entity.getName())){
                return true;
            }
        }
        return false;
    }

    private boolean checkInventoryForEntity(String entityToCheck){
        return this.playerEntities.containsKey(entityToCheck);
    }

    public String execute() throws GameError {
        consumeEntities();
        if(this.resetActivated){
            this.resetActivated = false;
            return "you died and lost all of your items, you must return to the start of the game\n";
        }
        produceEntities();
        return narration + "\n";
    }

    private void consumeEntities() {
        for (String item : getConsumed()) {
            if (item.equalsIgnoreCase("health")) {
                player.decreaseHealth();
                if(player.isPlayerDead()){
                    resetPlayer();
                    this.resetActivated = true;
                }
                break;
            }
            if (player.getInventory().containsKey(item)) {
                player.getInventory().remove(item);
            } else if (getEngineMap().get(player.currentLocation) != null) {
                getEngineMap().get(player.currentLocation).removeEntity(item);
            }
        }
    }

    private void resetPlayer(){
        if(!player.getInventory().isEmpty()){
            getEngineMap().get(player.currentLocation).artefacts.addAll(player.getInventory().values());
            engine.getMap().get(player.currentLocation).setAllEntities();
            HashMap<String, Artefact> inventory = player.getInventory();
            inventory.clear();
            player.setInventory(inventory);
        }
        Player resetPlayer = new Player(player.getPlayerName());
        setResetPlayer(resetPlayer);
    }

    public void setFirstLocation(String location){
        this.firstLocation = location;
    }


    private void produceEntities() throws GameError {
        for (String item : getProduced()) {
            if (item.equalsIgnoreCase("health")) {
                player.increaseHealth();
                break;
            }
            GameEntity entityToMove = null;
            for(Location location: getEngineMap().values()){
                if(location.getEntityForProduce(item)){
                    entityToMove = location.getEntity(item);
                    break;
                }
            }
//            GameEntity storedEntity = this.storeroom.setEntityForProduce(item);
            if (entityToMove != null) {
                addEntityToLocation(entityToMove);
            } else if (engine.getMap().containsKey(item)) {
                currentLocation.pathTo.add(item);
            } else {
                throw new GameError("Produced entity does not exist in Location or Player Inventory!\n");
            }
        }
    }

    private void addEntityToLocation(GameEntity storedEntity) throws GameError {
        GameEntityType entityType = storedEntity.getType();
        String name = storedEntity.getName();
        String description = storedEntity.getDescription();
        switch (entityType) {
            case ARTEFACT:
                currentLocation.addArtefact(new Artefact(name, description));
                currentLocation.setAllEntities();
                break;
            case FURNITURE:
                currentLocation.addFurniture(new Furniture(name, description));
                currentLocation.setAllEntities();
                break;
            case CHARACTER:
                currentLocation.addCharacters(new Character(name, description));
                currentLocation.setAllEntities();
                break;
            default:
                throw new GameError("Unknown Artefact type!");
        }
    }

    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public void setConsumed(List<String> consumed) {
        this.consumed = consumed;
    }

    public void setProduced(List<String> produced) {
        this.produced = produced;
    }
}
