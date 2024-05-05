package edu.uob.Command;

import edu.uob.*;

import java.util.HashMap;

public class Get extends GameCommand {
    Location currentLocation;
    String requestedArtefact;

    public Get(GameEngine gameEngine, Player player, String basicCommand) throws Exception {
        super(gameEngine, player, basicCommand);
        this.currentLocation = gameEngine.getMap().get(player.getCurrentLocation());
        setArtefactName();
        setup();
    }

    private void setArtefactName() throws GameError {
        if (basicCommand.startsWith("get ")) {
            String[] commandParts = basicCommand.split(" ");
            if(commandParts.length > 1) {
                this.requestedArtefact = commandParts[1];
            } else {
                throw new GameError("Unknown get command!");
            }
        }
    }

    public void setup() throws Exception {
        Artefact artefact = getEngine().pickupArtefact(player.getCurrentLocation(), requestedArtefact);
        if(artefact == null) {
            throw new GameError("Artefact fetched is null");
        }
        HashMap<String, Artefact> inventory = player.getInventory();
        if(inventory == null) {
            inventory = new HashMap<>();
        }
        inventory.put(requestedArtefact, artefact);
        player.setInventory(inventory);
    }

    @Override
    public String toString() {
        return "You picked up a " + requestedArtefact + "\n";
    }
}
