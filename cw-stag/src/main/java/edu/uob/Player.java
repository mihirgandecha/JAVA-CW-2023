package edu.uob;

import java.util.ArrayList;

public class Player {
    public String playerName;
    private Location playerCurrentLocation;

    public Player(String playerName, Location mapLocation) {
        this.playerName = playerName;
        this.playerCurrentLocation = mapLocation;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public Location getPlayerCurrentLocation() {
        return this.playerCurrentLocation;
    }

}
