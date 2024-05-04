package edu.uob.Command;

import edu.uob.*;

import java.util.*;

public class AdvancedAction
{
    private HashMap<String, HashSet<AdvancedAction>> gameActions;
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
        // Check if all required subjects are present in the player's location or inventory
        //TODO incorrect logic for axe + chop tree!
        this.currentLocation = map.get(player.getCurrentLocation());
        map.get(player.getCurrentLocation()).setAllEntities();
        this.locationEntities = map.get(player.getCurrentLocation()).entityList;
        this.playerEntities = player.getInventory();
        currentLocation.setAllEntities();
        if (currentLocation == null) return false;
        doesSubjectsExist();
        // Check if required consumed items are available
        for (String item : getConsumed()) {
            if (!player.getInventory().containsKey(item) && !currentLocation.entityList.contains(item)) {
                return false;
            }
        }
        return true;
    }

    private void doesSubjectsExist() throws GameError {
        for(String subject : subjects){
            if(!checkLocationForSubject(subject) && !checkInventoryForSubject(subject)){
                throw new GameError("Game Subject does not exist in Location or Player Inventory!\n");
            }
        }
    }

    private boolean checkLocationForSubject(String subject){
        for(GameEntity entity : this.locationEntities){
            if(subject.equals(entity.getName())){
                return true;
            }
        }
        return false;
    }

    private boolean checkInventoryForSubject(String subject){
        boolean b = this.playerEntities.containsKey(subject);
        return b;
    }

    // Method to execute the action
    public String execute(Player player, Map<String, Location> map) {
        Location currentLocation = map.get(player.getCurrentLocation());
        // Consume items from player's inventory or the location
        for (String item : getConsumed()) {
            if (player.getInventory().containsKey(item)) {
                player.getInventory().remove(item);
            } else if (currentLocation != null) {
                currentLocation.removeEntity(item);
            }
        }
        // Produce new items in the location
        for (String item : getProduced()) {
            currentLocation.addArtefact(new Artefact(item, item));
        }
        return narration + "\n";
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
