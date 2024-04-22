package edu.uob;

import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;

// 1. list for checking if location is true
//
public class GameState {
  ArrayList<Location> locations;
  ArrayList<Furniture> furnitures;
  ArrayList<Character> characters;
  ArrayList<Path> paths;

  public GameState(File entitiesFile, File actionsFile) throws Exception {
    locations = new ArrayList<>();
    furnitures = new ArrayList<>();
    characters = new ArrayList<>();
    paths = new ArrayList<>();
  }

  boolean isFilePresent() {
    return true;
  }

  void setParser() throws ParseException {
  }

}
