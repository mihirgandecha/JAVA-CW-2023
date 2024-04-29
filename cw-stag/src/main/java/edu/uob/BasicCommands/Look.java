package edu.uob.BasicCommands;

import edu.uob.*;

import java.util.ArrayList;
import java.util.List;

//GOTO - update player location
//GET
//DROP

public class Look extends GameCommand {
    StringBuilder builder = new StringBuilder();
    private String name;

    public Look(GameEngine gameEngine, Player player, String args) throws GameError {
        super(gameEngine, player, args);
        this.name = player.getCurrentLocation();
        setup();
    }

    private void setup() throws GameError {
        if(!confirmLookCommand()) throw new GameError("Invalid basic command, expected look token.");
        setLocationToString();
        setEntitiesDescriptionToString();
        setPathToString();
    }

    private boolean confirmLookCommand(){
        return getBasicCommand().equals("look");
    }

    private void setLocationToString() throws GameError {
        String description = getEngine().map.get(this.name).description;
        this.builder.append("You are in " + description + ". You can see: " + "\n");
    }

    private void setEntitiesDescriptionToString(){
        Location l = getEngine().map.get(this.name);
        this.builder.append(l.getAllEntitiesToString());
    }

    private void setPathToString(){
        String intro = "You can access from here: " + "\n";
        Location l = getEngine().map.get(this.name);
        this.builder.append(intro + l.pathTo + "\n");
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
