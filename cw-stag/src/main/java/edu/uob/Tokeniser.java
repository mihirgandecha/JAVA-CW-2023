package edu.uob;
import java.util.*;

/**
 * The `Tokeniser` class processes the command from GameServer, extracts the username, and generates tokens
 * for further processing. Throws `GameError` when the command/player username is invalid or null.
 */

public class Tokeniser {
    private String username;
    private String cleanCommand;
    private List<String> tokens;

    public Tokeniser(String command) throws GameError {
        validateCommand(command);
        command = command.toLowerCase().trim();
        this.tokens = splitCommandAtColon(command);
        setUsername();
        this.username = validateUsername(this.username);
        setIndividualTokens();
//        setCleanCommand();
    }

    public String getUsername() {
        return this.username;
    }

    public String getCleanCommand() {
        return this.cleanCommand;
    }

    public List<String> getTokens() {
        return this.tokens;
    }

    // Ensure the command is not null or empty
    public void validateCommand(String command) throws GameError {
        if (command == null || command.trim().isEmpty()) {
            throw new GameError("Command cannot be null or empty");
        }
    }

    private void setUsername() throws GameError {
        if (this.tokens.size() == 2) {
            this.username = this.tokens.get(0).trim();
            if (this.username.isEmpty()) {
                throw new GameError("Username is invalid");
            }
        } else {
            throw new GameError("Invalid command format");
        }
    }


    private String validateUsername(String username) {
        return username.replaceAll("[^a-zA-Z '\\-]", "");
    }

    // Set the clean command without the username and punctuation
    private void setCleanCommand() throws GameError {
        if (this.tokens.size() == 2) {
            this.cleanCommand = removePunctuation(this.tokens.get(1).trim());
        } else {
            throw new GameError("Invalid command format");
        }
    }

    // Split the clean command into individual tokens
//    public ArrayList<String> getIndividualTokens(String command) {
//        StringTokenizer tokenizer = new StringTokenizer(command);
//        ArrayList<String> tokens = new ArrayList<>();
//        while (tokenizer.hasMoreTokens()) {
//            tokens.add(tokenizer.nextToken() + " ");
//        }
//        return tokens;
//    }

    private void setIndividualTokens() throws GameError {
        if (this.tokens.size() == 2) {
            this.cleanCommand = removePunctuation(this.tokens.get(1).trim());
            this.tokens = getIndividualTokens(this.cleanCommand);
        } else {
            throw new GameError("Invalid command format");
        }
    }

    // Split the clean command into individual tokens
    public ArrayList<String> getIndividualTokens(String command) {
        StringTokenizer tokenizer = new StringTokenizer(command);
        ArrayList<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String cleanedToken = removePunctuation(token);
            //Removing repeated words
            if (!cleanedToken.isEmpty()) {
                tokens.add(cleanedToken + " ");
            }
        }
        return tokens;
    }

    // Split the command at the first colon, returning at most two parts
//    private List<String> splitCommandAtColon(String command) {
//        return new ArrayList<>(Arrays.asList(command.split(":", 2)));
//    }

    // Split the command at the first colon, returning at most two parts
    private List<String> splitCommandAtColon(String command) throws GameError {
        List<String> parts = new ArrayList<>(Arrays.asList(command.split(":", 2)));
        if (parts.size() != 2) {
            throw new GameError("Invalid command format: expected a colon separating username and command");
        }
        return parts;
    }

//    private void splitCommandAtColon() {
//        return Arrays.asList(this.tokens.spliterator(":", 2)));
//    }

    // Remove punctuation and convert text to lowercase
    private String removePunctuation(String text) {
        return text.replaceAll("[-,.:!?()]", " ").toLowerCase();
    }
}
