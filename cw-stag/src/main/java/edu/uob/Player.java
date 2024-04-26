package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    public String playerName;
    private int health = 3;
    private HashMap<String, Artefact> inventory;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}
