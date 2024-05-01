package edu.uob.basic_commands;

import edu.uob.Artefact;
import edu.uob.GameEngine;
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
        StringBuilder str = new StringBuilder();
        str.append("You have: " + "\n");
        for(Artefact artefact : inventoryList.values()){
            str.append(artefact.getDescription() + "\n");
        }
        return str.toString();
    }
}
