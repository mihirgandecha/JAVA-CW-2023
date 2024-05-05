<<<<<<< HEAD:cw-stag/src/main/java/edu/uob/basic_commands/GameCommand.java
package edu.uob.basic_commands;

import edu.uob.GameEngine;
import edu.uob.Player;

public abstract class GameCommand {
    GameEngine engine;
    Player player;
    String basicCommand;

    protected GameCommand(GameEngine gameEngine, Player player, String basicCommand) {
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
=======
package edu.uob.Command;

import edu.uob.GameEngine;
import edu.uob.Player;

public abstract class GameCommand {
    GameEngine engine;
    Player player;
    String basicCommand;

    protected GameCommand(GameEngine gameEngine, Player player, String basicCommand) {
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
>>>>>>> Desktop:cw-stag/src/main/java/edu/uob/Command/GameCommand.java
