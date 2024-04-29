package edu.uob.BasicCommands;

import edu.uob.GameEngine;
import edu.uob.Player;

import java.util.ArrayList;

public abstract class GameCommand {
    GameEngine engine;
    Player player;
    String basicCommand;

    public GameCommand(GameEngine gameEngine, Player player, String basicCommand) {
        this.engine = gameEngine;
        this.player = player;
        this.basicCommand = basicCommand;
    }

    public GameEngine getEngine() {
        return this.engine;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getBasicCommand() {
        return this.basicCommand;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
