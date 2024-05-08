package edu.uob.Command;

import edu.uob.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Location> getEngineMap(){
        return this.engine.getMap();
    }

    public Player getPlayer() {
        return this.player;
    }

    public HashMap<String, Artefact> getPlayerInventory(){
        return this.player.getInventory();
    }

    public void setResetPlayer(Player playerReset){
        if(engine.getGamePlayers().containsKey(playerReset.getPlayerName())){
            engine.getGamePlayers().remove(playerReset.getPlayerName());
            engine.getGamePlayers().put(playerReset.getPlayerName(), playerReset);
            this.player = playerReset;
        }
    }

    public String getBasicCommand() {
        return this.basicCommand;
    }

    public ArrayList<String> getEntityList(){
        return this.engine.getEntities();
    }

    public List<String> getCommand(){
        return engine.command;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}