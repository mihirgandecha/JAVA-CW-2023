package edu.uob.Command;
import edu.uob.*;
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

    public void setResetPlayer(Player playerReset){
        if(engine.getPlayerMap().containsKey(playerReset.getPlayerName())){
            engine.getPlayerMap().remove(playerReset.getPlayerName());
            engine.getPlayerMap().put(playerReset.getPlayerName(), playerReset);
            this.player = playerReset;
        }
    }

    public String getBasicCommand() {
        return this.basicCommand;
    }

    public List<String> getEntityList(){
        return this.engine.getAllLocationsGameEntities();
    }

    public List<String> getCommand(){
        return engine.commandToExecute;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}