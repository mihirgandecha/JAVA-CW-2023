package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tokeniser {
    private String serverCommand;
    private String username;
    private String cleanCommand;
    private String[] splitCommand;

    public Tokeniser(String command){
        this.serverCommand = command;
        // splits command so that: simon: look -> {"simon", "look"}
        this.splitCommand = command.split(":", 2);
        setUsername();
        this.username = getUsername();
        this.cleanCommand = removePunctuation(getCleanCommand());
    }

    public String getUsername(){
        return this.username;
    }

    public String getCleanCommand(){
        return this.cleanCommand;
    }

    public String getOriginalCommand(){
        return this.serverCommand;
    }

    private String setUsername(){
        if(this.splitCommand.length == 2){
            return this.splitCommand[0].trim();
        }
        return null;
    }

    private String getCleanCommandWithoutUsername(){
        if(this.splitCommand.length == 2){
            return this.splitCommand[1].trim();
        }
        return null;
    }

    // Splits the clean command into individual tokens
    public List<String> setIndividualTokens() {
        if (cleanCommand == null) {
            return Collections.emptyList();
        }
        List<String> tokens = Arrays.asList(cleanCommand.split("\\s+"));
        return new ArrayList<>(tokens);
    }

    // Removes punctuation and converts to lowercase
    private String removePunctuation(String text) {
        return text.replaceAll("[-,.:!?()]", "").toLowerCase();
    }
}
