package edu.uob.Command;

import edu.uob.Artefact;
import edu.uob.Location;
import edu.uob.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameAction
{
    //INPUT
    public List<String> triggers;
    //List of entities NECESSARY
    public List<String> subjects;
    //List of entities that are REMOVED
    public List<String> consumed;
    //List of entities that are CREATED
    public List<String> produced;
    //OUTPUT to console
    private String narration;

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
    public boolean canExecute(Player player, Map<String, Location> map) {
        // Check if all required subjects are present in the player's location or inventory
        //TODO incorrect logic for axe + chop tree!
        Location currentLocation = map.get(player.getCurrentLocation());
        if (currentLocation == null) return false;
        for (String subject : subjects) {
            if (!currentLocation.getAllEntitiesToString().contains(subject) && !player.getInventory().toString().contains(subject)) {
                return false;
            }
        }
        // Check if required consumed items are available
        for (String item : consumed) {
            if (!player.getInventory().containsKey(item) && !currentLocation.entityList.contains(item)) {
                return false;
            }
        }
        return true;
    }

    // Method to execute the action
    public String execute(Player player, Map<String, Location> map) {
        Location currentLocation = map.get(player.getCurrentLocation());
        // Consume items from player's inventory or the location
        for (String item : consumed) {
            if (player.getInventory().containsKey(item)) {
                player.getInventory().remove(item);
            } else if (currentLocation != null) {
                currentLocation.removeEntity(item);
            }
        }
        // Produce new items in the location
        for (String item : produced) {
            currentLocation.addArtefact(new Artefact(item, item));
        }
        return narration + "\n";
    }
}
