package edu.uob.BasicCommands;

import edu.uob.GameEngine;
import edu.uob.Location;
import edu.uob.Player;
import edu.uob.GameError;

public class Goto extends GameCommand {
    private String currentLocation;
    private String goLocation;

    public Goto(GameEngine gameEngine, Player player, String basicCommand) throws GameError {
        super(gameEngine, player, basicCommand);
        this.currentLocation = player.getCurrentLocation();
        parseCommand(basicCommand);
        executeMove();
    }

    private void parseCommand(String command) throws GameError {
        if (command.startsWith("goto ")) {
            this.goLocation = command.substring(5).trim();
        } else {
            throw new GameError("Invalid command format.");
        }
    }

    private void executeMove() throws GameError {
        Location currentLoc = getEngine().map.get(currentLocation);
        if (currentLoc == null) {
            throw new GameError("Current location is not found in the game map.");
        }
        if (currentLoc.pathTo.contains(goLocation)) {
            player.setLocation(goLocation); // Update player's location
        } else {
            throw new GameError("There is no path to " + goLocation + " from " + currentLocation);
        }
    }
}
