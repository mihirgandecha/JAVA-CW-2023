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
    private static final String INCORRECT_ERROR_MSG = "Invalid command format";

    public Tokeniser(String command) throws GameError {
        validateCommand(command);
        command = command.toLowerCase().trim();
        this.tokens = splitCommandAtColon(command);
        setUsername();
        this.username = validateUsername(this.username);
        setIndividualTokens();
        this.tokens = removeDuplicates(this.tokens);
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
            throw new GameError(INCORRECT_ERROR_MSG);
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
            throw new GameError(INCORRECT_ERROR_MSG);
        }
    }

    private void setIndividualTokens() throws GameError {
        if (this.tokens.size() == 2) {
            this.cleanCommand = removePunctuation(this.tokens.get(1).trim());
            this.tokens = getIndividualTokens(this.cleanCommand);
        } else {
            throw new GameError(INCORRECT_ERROR_MSG);
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
            if (!cleanedToken.isEmpty() && !tokens.contains(cleanedToken)) {
                tokens.add(cleanedToken + " ");
            }
        }
        return tokens;
    }

    public ArrayList<String> removeDuplicates(List<String> list) {
        Set<String> set = new LinkedHashSet<>(list);
        return new ArrayList<>(set);
    }

    // Split the command at the first colon, returning at most two parts
    private List<String> splitCommandAtColon(String command) throws GameError {
        List<String> parts = new ArrayList<>(Arrays.asList(command.split(":", 2)));
        if (parts.size() != 2) {
            throw new GameError(INCORRECT_ERROR_MSG + ": expected a colon separating username and command");
        }
        return parts;
    }

    // Remove punctuation and convert text to lowercase
    private String removePunctuation(String text) {
        return text.replaceAll("[^a-z]", " ");
    }
}
