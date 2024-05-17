package edu.uob.Command;

import edu.uob.*;

import java.util.HashMap;
import java.util.List;

public class Get extends GameCommand {
    Location currentLocation;
    String requestedArtefact;

    public Get(GameEngine gameEngine, Player player, String basicCommand) throws Exception {
        super(gameEngine, player, basicCommand);
        this.currentLocation = gameEngine.getMap().get(player.getCurrentLocation());
        setArtefactName();
        setup();
    }

    private void setArtefactName() {
        List<String> command = getCommand();
        for (String part : command) {
            if(getEntityList().contains(part)) {
                this.requestedArtefact = part;
                break;
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
