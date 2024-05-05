package edu.uob.Command;

import edu.uob.*;

import java.util.HashMap;

public class Drop extends GameCommand {
    private Location currentLocation;
    private String requestedArtefact;

    public Drop(GameEngine gameEngine, Player player, String basicCommand) throws GameError {
        super(gameEngine, player, basicCommand);
        this.currentLocation = getEngine().getMap().get(player.getCurrentLocation());
        parseArtefactName();
        executeDrop();
    }

    private void parseArtefactName() throws GameError {
        if (basicCommand.startsWith("drop ")) {
            String[] commandParts = basicCommand.split(" ");
            if(commandParts.length > 1) {
                this.requestedArtefact = commandParts[1];
            } else {
                throw new GameError("Unknown drop command!");
            }
        }
    }

    private void executeDrop() throws GameError {
        HashMap<String, Artefact> inventory = player.getInventory();
        Artefact artefact = inventory.remove(requestedArtefact);
        if (artefact == null) {
            throw new GameError("No artefact named '" + requestedArtefact + "' found in your inventory.");
        }
        player.getInventory().remove(requestedArtefact);
        currentLocation.addArtefact(artefact);
        getEngine().getMap().remove(currentLocation.location);
        getEngine().getMap().put(currentLocation.location, currentLocation);
    }

    @Override
    public String toString() {
        return "You dropped a " + requestedArtefact + "\n";
    }
}
