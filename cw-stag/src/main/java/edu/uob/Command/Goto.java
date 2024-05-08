package edu.uob.Command;

import edu.uob.*;

import java.util.ArrayList;

public class Goto extends GameCommand {
    private String currentLocation;
    private String goLocation;
    private StringBuilder builder;
    private Location location;

    public Goto(GameEngine gameEngine, Player player, String basicCommand) throws GameError {
        super(gameEngine, player, basicCommand);
        this.builder = new StringBuilder();
        this.currentLocation = player.getCurrentLocation();
        //TODO Compilation Error! possible this escape before subclass is fully initialised
        this.goLocation = getCommand().get(1);
        executeMove();
    }

    private void executeMove() throws GameError {
        Location currentLoc = getEngine().getMap().get(currentLocation);
        if (currentLoc == null) {
            throw new GameError("Current location is not found in the game map.");
        }
        if (currentLoc.pathTo.contains(goLocation)) {
            player.setLocation(goLocation);
            this.location = getEngine().getMap().get(goLocation);
            setLocationToString();
            setEntitiesDescriptionToString();
            setPathToString();
        } else {
            throw new GameError("There is no path to " + goLocation + " from " + currentLocation);
        }
    }

    private void setLocationToString() {
        String description = this.location.getDescription();
        this.builder.append("You are in " + description + ". You can see: " + "\n");
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
