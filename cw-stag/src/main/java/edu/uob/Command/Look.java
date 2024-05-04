package edu.uob.Command;

import edu.uob.GameEngine;
import edu.uob.GameError;
import edu.uob.Location;
import edu.uob.Player;

import java.util.ArrayList;

public class Look extends GameCommand {
    StringBuilder builder = new StringBuilder();
    private String name;
    private Location location;

    public Look(GameEngine gameEngine, Player player, String args) throws GameError {
        super(gameEngine, player, args);
        this.name = player.getCurrentLocation();
        setup();
    }

    private void setup() throws GameError {
        if(getEngine().getMap().get(this.name) == null) {
            throw new GameError("Player location does not exist");
        }
        this.location = getEngine().getMap().get(this.name);
        setLocationToString();
        setEntitiesDescriptionToString();
        setPathToString();
    }

    private void setLocationToString() {
        this.builder.append("You are in " + this.location.getDescription() + ". You can see: " + "\n");
    }

    private void setEntitiesDescriptionToString(){
        this.builder.append(this.location.getAllEntitiesToString());
    }

    private void setPathToString(){
        this.builder.append("You can access from here: " + "\n");
        ArrayList<String> paths = this.location.pathTo;
        for (String path : paths) {
            this.builder.append(path + "\n");
        }
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
