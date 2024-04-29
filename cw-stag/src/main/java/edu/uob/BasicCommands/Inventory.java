package edu.uob.BasicCommands;

import edu.uob.Artefact;
import edu.uob.GameEngine;
import edu.uob.GameError;
import edu.uob.Player;

import java.util.HashMap;

public class Inventory extends GameCommand{


    private final HashMap<String, Artefact> inventoryList;

    public Inventory(GameEngine gameEngine, Player player, String cleanCommand) {
        super(gameEngine, player, cleanCommand);
        this.inventoryList = player.getInventory();
    }

    @Override
    public String toString() {
        if(inventoryList.isEmpty()){
            return "";
        }
        return "You have: " + "\n" + inventoryList.toString();
    }
}
