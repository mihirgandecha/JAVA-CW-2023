<<<<<<< HEAD:cw-stag/src/main/java/edu/uob/basic_commands/Inventory.java
package edu.uob.basic_commands;

import edu.uob.Artefact;
import edu.uob.GameEngine;
import edu.uob.Player;

import java.util.HashMap;

public class Inventory extends GameCommand{

    public Inventory(GameEngine gameEngine, Player player, String cleanCommand) {
        super(gameEngine, player, cleanCommand);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if(player.getInventory() == null || player.getInventory().isEmpty()){
            str.append("Inventory is empty" + "\n");
            return str.toString();
        }
        str.append("You have: " + "\n");
        HashMap<String, Artefact> inventoryList = player.getInventory();
        for(Artefact artefact : inventoryList.values()){
            str.append(artefact.getDescription() + "\n");
        }
        return str.toString();
    }
}
=======
package edu.uob.Command;

import edu.uob.Artefact;
import edu.uob.GameEngine;
import edu.uob.Player;

import java.util.HashMap;

public class Inventory extends GameCommand{

    public Inventory(GameEngine gameEngine, Player player, String cleanCommand) {
        super(gameEngine, player, cleanCommand);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if(player.getInventory() == null || player.getInventory().isEmpty()){
            str.append("Inventory is empty" + "\n");
            return str.toString();
        }
        str.append("You have: " + "\n");
        HashMap<String, Artefact> inventoryList = player.getInventory();
        for(Artefact artefact : inventoryList.values()){
            str.append(artefact.getDescription() + "\n");
        }
        return str.toString();
    }
}
>>>>>>> Desktop:cw-stag/src/main/java/edu/uob/Command/Inventory.java
