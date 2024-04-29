package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    public String playerName;
    private int health = 3;
    private HashMap<String, Artefact> inventory;
    public String currentLocation;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public int getHealth() {
        return this.health;
    }

    public HashMap<String, Artefact> getInventory() {
        return this.inventory;
    }

    public void setInventory(HashMap<String, Artefact> inventory) {
        this.inventory = inventory;
    }

    public void setLocation(String location) {
        this.currentLocation = location;
    }

    public String getCurrentLocation() {
        return this.currentLocation;
    }
}
