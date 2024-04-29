package edu.uob.BasicCommands;

import edu.uob.*;

import java.util.ArrayList;
import java.util.List;

//GOTO - update player location
//GET
//DROP

public class Look extends GameCommand {
    StringBuilder builder = new StringBuilder();

    public Look(GameEngine gameEngine, Player player, String args) throws GameError {
        super(gameEngine, player, args);
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
        String name = getEngine().getPlayerStartLocation().location;
        String description = getEngine().map.get(name).description;
        this.builder.append("You are in " + description + ". You can see: " + "\n");
    }

    private void setEntitiesDescriptionToString(){
        String name = getEngine().getPlayerStartLocation().location;
        Location l = getEngine().map.get(name);
        this.builder.append(l.getAllEntitiesToString());
    }

    private void setPathToString(){
        String intro = "You can access from here: " + "\n";
        String name = getEngine().getPlayerStartLocation().location;
        Location l = getEngine().map.get(name);
        this.builder.append(intro + l.pathTo + "\n");
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
