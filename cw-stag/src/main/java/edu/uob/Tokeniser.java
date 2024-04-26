package edu.uob;

public class Tokeniser {
    private String serverCommand;
    private String username;
    private String cleanCommand;

    public Tokeniser(String command){
        this.serverCommand = command;
    }

    private void setUsername(){
        String[] tokens = serverCommand.split(" ", 2);
        if(tokens.length == 2){
            this.username = tokens[0].trim();
        }
    }

    public String getUsername(){
        return this.username;
    }

//    private String getCleanCommandWithoutUsername(){
//        String[] tokens = cleanCommand.split(" ", 2);
//    }




}
