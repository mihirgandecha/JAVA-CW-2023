package edu.uob.BasicCommands;

import edu.uob.GameEngine;
import edu.uob.Player;

public abstract class GameCommand {
    GameEngine engine;
    Player player;

    public GameCommand(GameEngine gameEngine, Player player) {
        this.engine = gameEngine;
        this.player = player;
    }
}
