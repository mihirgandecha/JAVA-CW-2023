package edu.uob;

import java.util.ArrayList;

public class GameAction
{
    public ArrayList<String> triggers;
    public ArrayList<String> subjects;
    public ArrayList<String> consumed;
    public ArrayList<String> produced;

    public ArrayList<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(ArrayList<String> triggers) {
        this.triggers = triggers;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public ArrayList<String> getConsumed() {
        return consumed;
    }

    public void setConsumed(ArrayList<String> consumed) {
        this.consumed = consumed;
    }

    public ArrayList<String> getProduced() {
        return produced;
    }

    public void setProduced(ArrayList<String> produced) {
        this.produced = produced;
    }
}
