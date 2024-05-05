package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The `Tokeniser` class processes the command from GameServer, extracts the username, and generates tokens
 * for further processing. Throws `GameError` when the command/player username is invalid or null.
 */

public class Tokeniser {
    private String serverCommand;
    private String username;
    private String cleanCommand;
    private String[] splitCommand;

    public Tokeniser(String command) throws GameError {
        validateCommand(command);
        this.serverCommand = command.toLowerCase().trim();
        this.splitCommand = splitCommandAtColon(serverCommand);
        setUsername();
        setCleanCommand();
    }

    public String getUsername() {
        return this.username;
    }

    public String getCleanCommand() {
        return this.cleanCommand;
    }

    public String getOriginalCommand() {
        return this.serverCommand;
    }

    // Ensure the command is not null or empty
    public void validateCommand(String command) throws GameError {
        if (command == null || command.trim().isEmpty()) {
            throw new GameError("Command cannot be null or empty");
        }
    }

    private void setUsername() throws GameError {
        if (this.splitCommand.length == 2) {
            this.username = this.splitCommand[0].trim();
            if (this.username.isEmpty()) {
                throw new GameError("Username is invalid");
            }
        } else {
            throw new GameError("Invalid command format");
        }
    }

    // Set the clean command without the username and punctuation
    private void setCleanCommand() throws GameError {
        if (this.splitCommand.length == 2) {
            this.cleanCommand = removePunctuation(this.splitCommand[1].trim());
        } else {
            throw new GameError("Invalid command format");
        }
    }

    // Split the clean command into individual tokens
    public List<String> getIndividualTokens() {
        if (cleanCommand == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(cleanCommand.split("\\s+")));
    }

    // Split the command at the first colon, returning at most two parts
    private String[] splitCommandAtColon(String command) {
        return command.split(":", 2);
    }

    // Remove punctuation and convert text to lowercase
    private String removePunctuation(String text) {
        return text.replaceAll("[-,.:!?()]", "").toLowerCase();
    }
}
