package edu.uob;

import java.util.HashMap;

public class Player {
    private final String playerName;
    private HashMap<String, Artefact> inventory;
    public String currentLocation;
    private static final int MAXIMUM_HEALTH = 3;
    private int health;

    public Player(String playerName) {
        this.playerName = playerName;
        this.inventory = new HashMap<>();
        this.health = MAXIMUM_HEALTH;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public int getHealth() {
        return this.health;
    }

    public void increaseHealth(){
        if(this.health < MAXIMUM_HEALTH) this.health++;
    }

    public void decreaseHealth() {
        if(this.health == 0){
            return;
        }
        this.health --;
    }

    public boolean isPlayerDead(){
        return this.health <= 0;
    }

    public HashMap<String, Artefact> getInventory() {
        if(this.inventory == null || this.inventory.isEmpty()) {
            this.inventory = new HashMap<>();
        }
        return this.inventory;
    }

    public void setInventory(HashMap<String, Artefact> inventory) {
        if (this.inventory == null) {
            this.inventory = new HashMap<>();
            this.inventory.putAll(inventory);
        } else{
            this.inventory = inventory;
        }

    }

    public void setLocation(String location) {
        this.currentLocation = location;
    }

    public String getCurrentLocation() {
        if(this.currentLocation == null){
            return null;
        }
        return this.currentLocation;
    }

}